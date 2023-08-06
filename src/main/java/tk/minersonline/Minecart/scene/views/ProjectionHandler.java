package tk.minersonline.Minecart.scene.views;

import org.joml.Matrix4f;

public interface ProjectionHandler {
	Matrix4f getMatrix();

	void updateMatrix(int width, int height);
}
