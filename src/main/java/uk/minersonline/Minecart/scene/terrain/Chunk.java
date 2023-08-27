package uk.minersonline.Minecart.scene.terrain;

import uk.minersonline.Minecart.scene.terrain.voxel.Voxel;
import uk.minersonline.Minecart.scene.terrain.voxel.VoxelType;

public class Chunk {
	public static final int CHUNK_SIZE = 16;
	private Voxel[][][] Voxels ;

	public boolean isVoxelAt(int x, int y, int z) {
		return x>=0&&y>=0&&z>=0&&x<CHUNK_SIZE&&y<CHUNK_SIZE&&z<CHUNK_SIZE&&Voxels[x][y][z].type != VoxelType.AIR;
	}

	public void setVoxels(Voxel[][][] Voxels) {
		this.Voxels = Voxels;
	}
	public Voxel getVoxelAt(int x, int y, int z) {
		return Voxels[x][y][z];
	}
	public void breakVoxelAt(int x, int y, int z) {
		Voxels[x][y][z].type = VoxelType.AIR;
	}
}