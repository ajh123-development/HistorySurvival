package tk.minersonline.Minecart.scene.views;

import org.joml.Matrix4f;

public class ProjectionView implements ProjectionHandler {
	private static final float FOV = (float) Math.toRadians(60.0f);
	private static final float Z_FAR = 1000.f;
	private static final float Z_NEAR = 0.01f;

	private final Matrix4f projMatrix;

	public ProjectionView(int width, int height) {
		projMatrix = new Matrix4f();
		updateMatrix(width, height);
	}

	public Matrix4f getMatrix() {
		return projMatrix;
	}

	public void updateMatrix(int width, int height) {
		projMatrix.setPerspective(FOV, (float) width / height, Z_NEAR, Z_FAR);
	}
}
