package uk.minersonline.Minecart.core.utils.objloader;

import uk.minersonline.Minecart.core.model.Vertex;

import java.util.ArrayList;

public class SmoothingGroup {
	
	private ArrayList<Face> faces;
	private ArrayList<Vertex> vertices;
	
	public SmoothingGroup(){
		faces = new ArrayList<Face>();
		vertices = new ArrayList<Vertex>();
	}
	
	public ArrayList<Vertex> getVertices() {
		return vertices;
	}
	public void setVertices(ArrayList<Vertex> vertices) {
		this.vertices = vertices;
	}

	public ArrayList<Face> getFaces() {
		return faces;
	}

	public void setFaces(ArrayList<Face> faces) {
		this.faces = faces;
	}
}
