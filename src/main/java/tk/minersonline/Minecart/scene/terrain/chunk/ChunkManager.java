package tk.minersonline.Minecart.scene.terrain.chunk;

import org.joml.Vector3i;
import tk.minersonline.Minecart.scene.terrain.voxel.VoxelType;

import java.util.HashMap;
import java.util.Map;

public class ChunkManager {
	private final Map<Vector3i, Chunk> chunks;

	public ChunkManager() {
		chunks = new HashMap<>();
	}

	public Chunk getChunk(Vector3i chunkPosition) {
		return chunks.get(chunkPosition);
	}

	public void generateChunk(Vector3i chunkPosition) {
		if (!chunks.containsKey(chunkPosition)) {
			Chunk chunk = new Chunk(chunkPosition);
			// Generate voxel data for the chunk at the given position
			// For simplicity, let's just generate a flat terrain here
			for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
				for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
					for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
						Vector3i voxelPosition = new Vector3i(x, y, z);
						VoxelType voxelType = VoxelType.DIRT; // For simplicity, we use DIRT everywhere
						chunk.setVoxel(voxelPosition, voxelType);
					}
				}
			}

			chunks.put(chunkPosition, chunk);
		}
	}
}
