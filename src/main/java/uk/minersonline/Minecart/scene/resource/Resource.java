package uk.minersonline.Minecart.scene.resource;

import uk.minersonline.Minecart.scene.terrain.voxel.VoxelType;

public class Resource {
	private static final int SOUTH = 0;
	private static final int NORTH = 1;
	private static final int EAST = 2;
	private static final int WEST = 3;
	private static final int UP = 4;
	private static final int DOWN = 5;

	public static int getTextureID(VoxelType type, int s) {

		switch (type) {
			case GRASS -> {
				return switch (s) {
					case UP -> 0;
					case DOWN -> 2;
					default -> 3;
				};
			}
			case DIRT -> {
				return 2;
			}
			case STONE -> {
				return 1;
			}
			default -> {
				return 255;
			}
		}
	}

}