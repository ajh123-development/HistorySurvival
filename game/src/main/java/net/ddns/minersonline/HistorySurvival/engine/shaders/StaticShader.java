package net.ddns.minersonline.HistorySurvival.engine.shaders;

import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class StaticShader extends ShaderProgramBase {
	private static final int MAX_LIGHTS = 4;

	private static final String VERTEX_FILE = "shaders/vertexShader.glsl";
	private static final String FRAGMENT_FILE = "shaders/fragmentShader.glsl";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int[] location_lightPositions;
	private int[] location_lightColors;
	private int[] location_attenuation;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColor;
	private int location_numberOfRowsInTextureAtlas;
	private int location_offset;
	private int location_plane;

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoOrds");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColor = super.getUniformLocation("skyColor");
		location_numberOfRowsInTextureAtlas = super.getUniformLocation("numberOfRowsInTextureAtlas");   // texture atlas support
		location_offset = super.getUniformLocation("offset");   // texture atlas support
		location_plane = super.getUniformLocation("plane");

		location_lightPositions = new int[MAX_LIGHTS];
		location_lightColors = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for(int i=0;i<MAX_LIGHTS;i++){
			location_lightPositions[i] = super.getUniformLocation("lightPosition["+i+"]");
			location_lightColors[i] = super.getUniformLocation("lightColor["+i+"]");
			location_attenuation[i] = super.getUniformLocation("attenuation["+i+"]");
		}
	}

	public void loadClipPlane(Vector4f plane){
		super.loadVector(location_plane, plane);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadDiffuseLights(List<Light> lights) {
		for(int i=0;i<MAX_LIGHTS;i++){
			if(i<lights.size()){
				super.loadVector(location_lightPositions[i], lights.get(i).getPosition());
				super.loadVector(location_lightColors[i], lights.get(i).getColor());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector(location_lightPositions[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColors[i], new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}

	public void loadSpecularLight(float shineDamper, float reflectivity) {
		super.loadFloat(location_shineDamper, shineDamper);
		super.loadFloat(location_reflectivity, reflectivity);
	}

	public void loadFakeLighting(boolean useFakeLighting) {
		super.loadBoolean(location_useFakeLighting, useFakeLighting);
	}

	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(location_skyColor, new Vector3f(r, g, b));
	}

	public void loadNumberOfRowsInTextureAtlas(int numberOfRowsInTextureAtlas) {
		super.loadFloat(location_numberOfRowsInTextureAtlas, numberOfRowsInTextureAtlas);
	}

	public void loadOffset(float x, float y) {
		super.loadVector(location_offset, new Vector2f(x, y));
	}
}
