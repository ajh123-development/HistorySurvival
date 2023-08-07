package tk.minersonline.Minecart.scene.terrain.chunk;

import org.joml.Matrix4f;
import org.joml.Vector3i;
import tk.minersonline.Minecart.scene.objects.Mesh;
import tk.minersonline.Minecart.scene.terrain.voxel.Voxel;
import tk.minersonline.Minecart.scene.terrain.voxel.VoxelMesh;
import tk.minersonline.Minecart.scene.terrain.voxel.VoxelType;
import tk.minersonline.Minecart.scene.terrain.world.World;

public class Chunk {
	public static final int CHUNK_SIZE = 16; // You can adjust this size as needed
	private final Voxel[][][] voxels;
	private boolean needsMeshUpdate;
	private Mesh meshData;
	private final Matrix4f modelMatrix;
	private final Vector3i position;

	public Chunk(Vector3i position) {
		this.position = position;
		voxels = new Voxel[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
		needsMeshUpdate = true;
		modelMatrix = new Matrix4f();
		modelMatrix.translate(position.x * Chunk.CHUNK_SIZE, position.y * Chunk.CHUNK_SIZE, position.z * Chunk.CHUNK_SIZE);
	}

	public Voxel getVoxel(Vector3i position) {
		return voxels[position.x][position.y][position.z];
	}

	public void setVoxel(Vector3i position, VoxelType type) {
		voxels[position.x][position.y][position.z] = new Voxel(position.add(position.mul(Chunk.CHUNK_SIZE)), type);
		needsMeshUpdate = true;
	}

	public boolean needsMeshUpdate() {
		return needsMeshUpdate;
	}

	public void markMeshUpdated() {
		needsMeshUpdate = false;
	}

	public Mesh getMeshData() {
		return meshData;
	}

	public void generateMesh(World world) {
		meshData = VoxelMesh.generateMesh(this);
	}

	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	public Vector3i getPosition() {
		return position;
	}

	public boolean isInsideChunk(Vector3i position) {
		return position.x >= 0 && position.x < CHUNK_SIZE &&
				position.y >= 0 && position.y < CHUNK_SIZE &&
				position.z >= 0 && position.z < CHUNK_SIZE;
	}
}

