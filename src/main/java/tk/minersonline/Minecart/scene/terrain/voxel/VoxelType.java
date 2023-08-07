package tk.minersonline.Minecart.scene.terrain.voxel;

public enum VoxelType {
	AIR(0),
	DIRT(1),
	STONE(2);
	// Add more voxel types as needed

	private final int id;

	VoxelType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}