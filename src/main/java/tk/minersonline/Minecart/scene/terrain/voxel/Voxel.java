package tk.minersonline.Minecart.scene.terrain.voxel;

import org.joml.Vector3i;

public class Voxel {
	private final Vector3i position;
	private final VoxelType type;

	public Voxel(Vector3i position, VoxelType type) {
		this.position = position;
		this.type = type;
	}

	public Vector3i getPosition() {
		return position;
	}

	public VoxelType getType() {
		return type;
	}
}

