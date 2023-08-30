package uk.minersonline.Minecart.gui;

import imgui.type.ImBoolean;

public class ControlsManager {
	public static ImBoolean DrawChunkWireframe = new ImBoolean(false);
	public static ImBoolean DrawChunkNodeBounds = new ImBoolean(false);
	public static ImBoolean RefreshChunkMesh = new ImBoolean(true);
	public static ImBoolean DrawChunkSeamBounds = new ImBoolean(false);
	public static ImBoolean PlayerNoClip = new ImBoolean(false);
	public static ImBoolean TerrainBuildMode = new ImBoolean(false);
}
