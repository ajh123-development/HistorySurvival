 package uk.minersonline.Minecart.core.scene;

 import uk.minersonline.Minecart.core.math.Transform;

 import java.util.HashMap;

 import static org.lwjgl.opengl.GL11.*;
 import static org.lwjgl.opengl.GL11.GL_CULL_FACE;

 public class GameObject{
	protected boolean drawWireframe = false;
	protected boolean refreshMesh = true;
	private HashMap<String, Component> components;
	private Transform transform;
	
	public GameObject()
	{
		components = new HashMap<>();
		transform = new Transform();
	}
	
	public void addComponent(String string, Component component)
	{
		component.setParent(this);
		components.put(string, component);
	}
	
	public void update()
	{	
		for (String key : components.keySet()) {
			components.get(key).update();
		}
	}
	
	public void input()
	{
		for (String key : components.keySet()) {
			components.get(key).input();
		}
	}
	
	public void render()
	{
		glPolygonMode(GL_FRONT_AND_BACK, drawWireframe ? GL_LINE : GL_FILL);
		if(!drawWireframe){
			glDisable(GL_CULL_FACE);
		}
		for (String key : components.keySet()) {
			components.get(key).render();
		}
	}

	public HashMap<String, Component> getComponents() {
		return components;
	}
	
	public Component getComponent(String component)
	{
		return this.components.get(component);
	}

	public Transform getTransform() {
		return transform;
	}

	public void setTransform(Transform transform) {
		this.transform = transform;
	}

	protected void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
