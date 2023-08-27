package uk.minersonline.Minecart.scene.terrain.voxel;

public enum VoxelType {
	AIR(0),
	GRASS(1),
	STONE(2),
	DIRT(3);
	// Add more voxel types as needed

	private final int id;

	VoxelType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}