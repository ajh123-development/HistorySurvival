package uk.minersonline.Minecart.core.kernel;

import uk.minersonline.Minecart.core.math.Vec2f;
import org.lwjgl.glfw.*;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * 
 * @author oreon3D
 * The GLFW Input-Handler
 *
 */
public class Input {
	
	private static Input instance = null;

	private ArrayList<Integer> pushedKeys = new ArrayList<Integer>();
	private ArrayList<Integer> keysHolding = new ArrayList<Integer>();
	private ArrayList<Integer> releasedKeys = new ArrayList<Integer>();
	
	private ArrayList<Integer> pushedButtons = new ArrayList<Integer>();
	private ArrayList<Integer> buttonsHolding = new ArrayList<Integer>();
	private ArrayList<Integer> releasedButtons = new ArrayList<Integer>();
	
	private Vec2f cursorPosition;
	private Vec2f lockedCursorPosition;
	private float scrollOffset;
	
	private boolean pause = false;
	private boolean showCursor;
	public boolean isShowCursor() {
		return showCursor;
	}
	
	@SuppressWarnings("unused")
	private GLFWKeyCallback keyCallback;
	 
	@SuppressWarnings("unused")
	private GLFWCursorPosCallback cursorPosCallback;
	
	@SuppressWarnings("unused")
	private GLFWMouseButtonCallback mouseButtonCallback;
	
	@SuppressWarnings("unused")
	private GLFWScrollCallback scrollCallback;
	
	@SuppressWarnings("unused")
	private GLFWFramebufferSizeCallback framebufferSizeCallback;
	
	public static Input getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Input();
	    }
	      return instance;
	}
	
	protected Input()
	{
		cursorPosition = new Vec2f();
		
		glfwSetFramebufferSizeCallback(Window.getInstance().getWindow(), (framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
		    @Override
		    public void invoke(long window, int width, int height) {
		        Window.getInstance().setWindowSize(width, height);
		    }
		}));
		
		glfwSetKeyCallback(Window.getInstance().getWindow(), (keyCallback = new GLFWKeyCallback() {

            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
            	if (action == GLFW_PRESS){
            		if (!pushedKeys.contains(key)){
            			pushedKeys.add(key);
            			keysHolding.add(key);
            		}
                }
            	
                if (action == GLFW_RELEASE){
                	keysHolding.remove(new Integer(key));
                	releasedKeys.add(key);
                }

				if(keysHolding.contains(GLFW_KEY_TAB)) {
					lockedCursorPosition = new Vec2f(cursorPosition);
					showCursor = !showCursor;
					glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
				}

				if(!showCursor) {
					glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
				}
            }
        }));
		
		glfwSetMouseButtonCallback(Window.getInstance().getWindow(), (mouseButtonCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (action == GLFW_PRESS){
                	if (!pushedButtons.contains(button)){
                		pushedButtons.add(button);
                		buttonsHolding.add(button);
                	}
                }
                
                if (action == GLFW_RELEASE){
                	releasedButtons.add(button);
                	buttonsHolding.remove(new Integer(button));
                }
            }
		}));
		
		glfwSetCursorPosCallback(Window.getInstance().getWindow(), (cursorPosCallback = new GLFWCursorPosCallback() {

            @Override
            public void invoke(long window, double xpos, double ypos) {
            	cursorPosition.setX((float) xpos);
            	cursorPosition.setY((float) ypos);
            }

		}));
		
		glfwSetScrollCallback(Window.getInstance().getWindow(), (scrollCallback = new GLFWScrollCallback() {
			
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				setScrollOffset((float) yoffset);
			}
		}));
	}
	
	public void update() {
		setScrollOffset(0);
		pushedKeys.clear();
		releasedKeys.clear();
		pushedButtons.clear();
		releasedButtons.clear();
		
		glfwPollEvents();
	}
	
	public boolean isKeyPushed(int key)
	{
		return pushedKeys.contains(key);
	}
	
	public boolean isKeyReleased(int key)
	{
		return releasedKeys.contains(key);
	}
	
	public boolean isKeyHold(int key)
	{
		return keysHolding.contains(key);
	}
	
	public boolean isButtonPushed(int key)
	{
		return pushedButtons.contains(key);
	}
	
	public boolean isButtonreleased(int key)
	{
		return releasedButtons.contains(key);
	}
	
	public boolean isButtonHolding(int key)
	{
		return buttonsHolding.contains(key);
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}
	
	public Vec2f getCursorPosition() {
		return cursorPosition;
	}

	public void setCursorPosition(Vec2f cursorPosition) {
		this.cursorPosition = cursorPosition;
		
		glfwSetCursorPos(Window.getInstance().getWindow(),
				cursorPosition.getX(),
				cursorPosition.getY());
	}

	public Vec2f getLockedCursorPosition() {
		return lockedCursorPosition;
	}

	public void setLockedCursorPosition(Vec2f lockedCursorPosition) {
		this.lockedCursorPosition = lockedCursorPosition;
	}
	
	public ArrayList<Integer> getPushedKeys() {
		return pushedKeys;
	}

	public ArrayList<Integer> getButtonsHolding() {
		return buttonsHolding;
	}

	public float getScrollOffset() {
		return scrollOffset;
	}

	public void setScrollOffset(float scrollOffset) {
		this.scrollOffset = scrollOffset;
	}

	public ArrayList<Integer> getKeysHolding() {
		return keysHolding;
	}

	public ArrayList<Integer> getPushedButtons() {
		return pushedButtons;
	}
}
