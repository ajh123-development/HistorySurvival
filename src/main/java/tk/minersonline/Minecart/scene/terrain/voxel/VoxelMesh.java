package tk.minersonline.Minecart.scene.terrain.voxel;

import org.joml.Vector3f;
import org.joml.Vector3i;
import tk.minersonline.Minecart.scene.objects.Mesh;
import tk.minersonline.Minecart.scene.terrain.chunk.Chunk;
import tk.minersonline.Minecart.templates.models.Cube;
import tk.minersonline.Minecart.util.Direction;

import java.util.ArrayList;
import java.util.List;

public class VoxelMesh {
	public static Mesh generateMesh(Chunk chunk) {
		List<Float> vertices = new ArrayList<>();
		List<Float> texCoords = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();

		int CHUNK_SIZE = Chunk.CHUNK_SIZE;

		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int y = 0; y < CHUNK_SIZE; y++) {
				for (int z = 0; z < CHUNK_SIZE; z++) {
					Voxel voxel = chunk.getVoxel(new Vector3i(x, y, z));
					if (voxel.getType() != VoxelType.AIR) {
						greedyMesh(chunk, x, y, z, voxel.getType(), vertices, texCoords, indices);
					}
				}
			}
		}

		float[] verticesArray = new float[vertices.size()];
		for (int i = 0; i < verticesArray.length; i++) {
			verticesArray[i] = vertices.get(i);
		}

		float[] texCoordsArray = new float[texCoords.size()];
		for (int i = 0; i < texCoordsArray.length; i++) {
			texCoordsArray[i] = texCoords.get(i);
		}

		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}

		return new Mesh(verticesArray, texCoordsArray, indicesArray);
	}

	private static void greedyMesh(Chunk chunk, int x, int y, int z, VoxelType type,
								   List<Float> vertices, List<Float> texCoords, List<Integer> indices) {
		for (Direction dir : Direction.values()) {
			Vector3i neighborPos = new Vector3i(x, y, z).add(dir.getOffset());
			if (chunk.isInsideChunk(neighborPos)) {
				Voxel neighborVoxel = chunk.getVoxel(neighborPos);
				if (neighborVoxel.getType() == type) {
					// This voxel face is adjacent to a voxel of the same type, skip it
					continue;
				}
			}

			// Generate vertices, texCoords, and indices for the current face
			float[] faceVertices = generateFaceVertices(new Vector3i(x, y, z), dir);
			int baseIndex = vertices.size() / 3;
			for (float v : faceVertices) {
				vertices.add(v);
			}
			texCoords.addAll(generateFaceTexCoords());
			for (int i = 0; i < 6; i++) {
				indices.add(baseIndex + Cube.FACE_INDICES[i]);
			}
		}
	}

	private static List<Float> generateFaceTexCoords() {
		return new ArrayList<>(List.of(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f));
	}

	private static float[] generateFaceVertices(Vector3i position, Direction dir) {
		Vector3f[] faceVertices = Cube.FACE_VERTICES[dir.ordinal()];
		float[] vertices = new float[12];
		float size = 1.0f; // Size of a voxel

		for (int i = 0; i < faceVertices.length; i++) {
			Vector3f vertex = faceVertices[i];
			vertices[i * 3] = position.x + vertex.x * size;
			vertices[i * 3 + 1] = position.y + vertex.y * size;
			vertices[i * 3 + 2] = position.z + vertex.z * size;
		}

		return vertices;
	}
}
