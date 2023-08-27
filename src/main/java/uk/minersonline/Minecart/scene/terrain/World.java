package uk.minersonline.Minecart.scene.terrain;

import org.joml.Vector3i;
import uk.minersonline.Minecart.scene.terrain.voxel.Voxel;
import uk.minersonline.Minecart.scene.terrain.voxel.VoxelFace;
import uk.minersonline.Minecart.scene.terrain.voxel.VoxelType;

import java.util.concurrent.ConcurrentHashMap;

public class World {
	private final ConcurrentHashMap<Vector3i, Chunk> chunks;

	public World() {
		chunks = new ConcurrentHashMap<>();
	}
	public Voxel getVoxelAt(int x, int y, int z)  {
		Vector3i chunkCoord = new Vector3i(x/Chunk.CHUNK_SIZE, y/Chunk.CHUNK_SIZE, z/Chunk.CHUNK_SIZE);
		if(! chunks.containsKey(chunkCoord)) {
			return null;
		}

		Chunk chunk = chunks.get(chunkCoord);

		int cx = x%Chunk.CHUNK_SIZE;
		int cy = y%Chunk.CHUNK_SIZE;
		int cz = z%Chunk.CHUNK_SIZE;

		return chunk.getVoxelAt(cx,cy,cz);
	}
	public boolean isVoxelAt(int x, int y, int z) {
		Vector3i chunkCoord = new Vector3i(x/Chunk.CHUNK_SIZE, y/Chunk.CHUNK_SIZE, z/Chunk.CHUNK_SIZE);
		if(! chunks.containsKey(chunkCoord)) {
			return false;
		}

		Chunk chunk = chunks.get(chunkCoord);

		int cx = x%Chunk.CHUNK_SIZE;
		int cy = y%Chunk.CHUNK_SIZE;
		int cz = z%Chunk.CHUNK_SIZE;

		if(!chunk.isVoxelAt(cx, cy, cz)) {
			return false;
		}
		VoxelType type = chunk.getVoxelAt(cx,cy,cz).type;
		return type != VoxelType.AIR;
	}

	public boolean breakVoxelAt( int x,  int y, int z) {
		if(!isVoxelAt(x,y,z)) {
			return false;
		}

		Vector3i chunkCoord = new Vector3i((x/Chunk.CHUNK_SIZE), (y/Chunk.CHUNK_SIZE), (z/Chunk.CHUNK_SIZE));
		if(! chunks.containsKey(chunkCoord)) {
			return false;
		}
		Chunk chunk = chunks.get(chunkCoord);
		int cx = x%Chunk.CHUNK_SIZE;
		int cy = y%Chunk.CHUNK_SIZE;
		int cz = z%Chunk.CHUNK_SIZE;
		chunk.breakVoxelAt(cx,cy,cz);
		return true;
	}
	public VoxelFace f(int x, int y, int z, int side) {
		if(!isVoxelAt(x,y,z)) {
			return null;
		}
		VoxelType type = getVoxelAt(x,y,z).type;

		VoxelFace voxelFace = new VoxelFace(type);

		voxelFace.side = side;

		return voxelFace;
	}
	public void generateChunk(Vector3i vec) {
		int x = vec.x;
		int y = vec.y;
		int z = vec.z;

		Voxel[][][] Voxels = new Voxel[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];
		Voxel v;
		for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
			for (int j =Chunk.CHUNK_SIZE-1; j >=0 ; j--) {
				for (int k = 0; k < Chunk.CHUNK_SIZE; k++) {
					v = new Voxel();
					v.type = VoxelType.STONE;
					Voxels[i][j][k] = v;
				}
			}
		}

		Chunk chunk = new Chunk();
		chunk.setVoxels(Voxels);
		chunks.put(new Vector3i(x,y,z), chunk);
	}
	public Chunk getChunk(int x,int y, int z) {
		return chunks.get(new Vector3i(x,y,z));
	}
	public boolean chunkLoaded(Vector3i v) {
		return chunks.containsKey(v);
	}
}