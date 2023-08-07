package tk.minersonline.Minecart.templates.models;

import org.joml.Vector3f;

public class Cube {
	// Vertices of the cube, each vertex is a 3D vector (x, y, z)
	public static final Vector3f[] VERTICES = {
			new Vector3f(0.0f, 0.0f, 0.0f), // Bottom left front
			new Vector3f(1.0f, 0.0f, 0.0f), // Bottom right front
			new Vector3f(1.0f, 1.0f, 0.0f), // Top right front
			new Vector3f(0.0f, 1.0f, 0.0f), // Top left front
			new Vector3f(0.0f, 0.0f, 1.0f), // Bottom left back
			new Vector3f(1.0f, 0.0f, 1.0f), // Bottom right back
			new Vector3f(1.0f, 1.0f, 1.0f), // Top right back
			new Vector3f(0.0f, 1.0f, 1.0f)  // Top left back
	};

	// Indices for the vertices that make up each face of the cube
	public static final int[] FACE_INDICES = {0, 1, 2, 2, 3, 0};

	// Array of vertices for each face of the cube (front, back, left, right, top, bottom)
	public static final Vector3f[][] FACE_VERTICES = {
			{VERTICES[0], VERTICES[1], VERTICES[2], VERTICES[3]}, // Front face
			{VERTICES[4], VERTICES[5], VERTICES[6], VERTICES[7]}, // Back face
			{VERTICES[0], VERTICES[4], VERTICES[7], VERTICES[3]}, // Left face
			{VERTICES[1], VERTICES[5], VERTICES[6], VERTICES[2]}, // Right face
			{VERTICES[3], VERTICES[2], VERTICES[6], VERTICES[7]}, // Top face
			{VERTICES[0], VERTICES[1], VERTICES[5], VERTICES[4]}  // Bottom face
	};
}