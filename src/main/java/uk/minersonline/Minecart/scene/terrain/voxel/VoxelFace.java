package uk.minersonline.Minecart.scene.terrain.voxel;

public class VoxelFace {

	public VoxelType type;
	public int side;

	public VoxelFace(VoxelType type) {
		super();
		this.type = type;
	}


	public boolean equals(final VoxelFace face) { return face.type == this.type ; }
}
