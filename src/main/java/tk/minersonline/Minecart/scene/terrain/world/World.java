package tk.minersonline.Minecart.scene.terrain.world;

import org.joml.Vector3f;
import org.joml.Vector3i;
import tk.minersonline.Minecart.scene.Camera;
import tk.minersonline.Minecart.scene.Scene;
import tk.minersonline.Minecart.scene.terrain.chunk.Chunk;
import tk.minersonline.Minecart.scene.terrain.chunk.ChunkManager;
import tk.minersonline.Minecart.scene.terrain.voxel.Voxel;
import tk.minersonline.Minecart.scene.terrain.voxel.VoxelType;

import java.util.ArrayList;
import java.util.List;

public class World {
	private final ChunkManager chunkManager;
	private final WorldRenderer worldRenderer;
	private final Camera camera;

	public World(Camera camera) {
		this.camera = camera;
		chunkManager = new ChunkManager();
		worldRenderer = new WorldRenderer();
	}

	public void generateWorld() {
		// Generate the initial world terrain by creating and populating chunks
		// (You may use noise-based generation or other methods)
		// For simplicity, let's generate a flat world here
		for (int x = -5; x <= 5; x++) {
			for (int z = -5; z <= 5; z++) {
				Vector3i chunkPosition = new Vector3i(x, 0, z);
				chunkManager.generateChunk(chunkPosition);
			}
		}
	}

	public void update() {
		// Update the world state, handle player interactions, etc.
		// For simplicity, we're not implementing player interactions here
	}

	public void cleanup() {
		worldRenderer.cleanup();
	}

	public void render(Scene scene) {
		// Iterate through visible chunks and render them using the voxelRenderer
		for (Chunk chunk : getVisibleChunks()) {
			if (chunk.needsMeshUpdate()) {
				chunk.generateMesh(this);
				chunk.markMeshUpdated();
			}
			worldRenderer.render(chunk, scene);
		}
	}

	private List<Chunk> getVisibleChunks() {
		// Implement a method to determine which chunks are currently visible to the camera
		// You may use frustum culling or other techniques to optimize visibility checks
		List<Chunk> visibleChunks = new ArrayList<>();
		Vector3f cameraPosition = camera.getPosition();

		// For simplicity, let's assume each chunk is 16 units in size and centered at the origin
		int renderDistanceChunks = 5;
		int chunkSize = 16;
		int renderDistance = renderDistanceChunks * chunkSize;

		for (int x = -renderDistance; x <= renderDistance; x += 1) {
			for (int z = -renderDistance; z <= renderDistance; z += 1) {
				Vector3i chunkPosition = new Vector3i(x, 0, z);
				Chunk chunk = chunkManager.getChunk(chunkPosition);
				if (chunk != null) {
					visibleChunks.add(chunk);
				}
			}
		}

		return visibleChunks;
	}

	public Voxel getVoxelAtWorldPosition(Vector3i worldPosition) {
		Vector3i chunkPosition = calculateChunkPosition(worldPosition);
		Vector3i voxelPosition = calculateVoxelPositionInChunk(worldPosition, chunkPosition);

		Chunk chunk = chunkManager.getChunk(chunkPosition);
		if (chunk != null) {
			return chunk.getVoxel(voxelPosition);
		}
		return null; // or return a default voxel type
	}

	public void setVoxelAtWorldPosition(Vector3i worldPosition, VoxelType voxelType) {
		Vector3i chunkPosition = calculateChunkPosition(worldPosition);
		Vector3i voxelPosition = calculateVoxelPositionInChunk(worldPosition, chunkPosition);

		Chunk chunk = chunkManager.getChunk(chunkPosition);
		if (chunk != null) {
			chunk.setVoxel(voxelPosition, voxelType);
		}
	}

	private Vector3i calculateChunkPosition(Vector3i worldPosition) {
		int chunkSize = Chunk.CHUNK_SIZE;
		int chunkX = Math.floorDiv(worldPosition.x, chunkSize);
		int chunkY = Math.floorDiv(worldPosition.y, chunkSize);
		int chunkZ = Math.floorDiv(worldPosition.z, chunkSize);
		return new Vector3i(chunkX, chunkY, chunkZ);
	}

	private Vector3i calculateVoxelPositionInChunk(Vector3i worldPosition, Vector3i chunkPosition) {
		int chunkSize = Chunk.CHUNK_SIZE;
		int voxelX = (worldPosition.x - chunkPosition.x * chunkSize);
		int voxelY = (worldPosition.y - chunkPosition.y * chunkSize);
		int voxelZ = (worldPosition.z - chunkPosition.z * chunkSize);
		return new Vector3i(voxelX, voxelY, voxelZ);
	}
}
