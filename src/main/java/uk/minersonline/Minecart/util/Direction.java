package uk.minersonline.Minecart.util;

import org.joml.Vector3i;

public enum Direction {
	FRONT(new Vector3i(0, 0, 1)),
	BACK(new Vector3i(0, 0, -1)),
	LEFT(new Vector3i(-1, 0, 0)),
	RIGHT(new Vector3i(1, 0, 0)),
	TOP(new Vector3i(0, 1, 0)),
	BOTTOM(new Vector3i(0, -1, 0));

	private Vector3i offset;

	Direction(Vector3i offset) {
		this.offset = offset;
	}

	public Vector3i getOffset() {
		return offset;
	}
}

