package uk.minersonline.Minecart.core.kernel;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiKey;
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

	private final ArrayList<Integer> pushedKeys = new ArrayList<>();
	private final ArrayList<Integer> keysHolding = new ArrayList<>();
	private final ArrayList<Integer> releasedKeys = new ArrayList<>();
	
	private final ArrayList<Integer> pushedButtons = new ArrayList<>();
	private final ArrayList<Integer> buttonsHolding = new ArrayList<>();
	private final ArrayList<Integer> releasedButtons = new ArrayList<>();
	
	private Vec2f cursorPosition;
	private Vec2f lockedCursorPosition;
	private float scrollOffset;
	
	private boolean pause = false;
	private boolean showCursor;
	public boolean isShowCursor() {
		return showCursor;
	}
	
	@SuppressWarnings("unused")
	private final GLFWKeyCallback keyCallback;
	@SuppressWarnings("unused")
	private final GLFWCharCallback charCallback;
	@SuppressWarnings("unused")
	private final GLFWCursorPosCallback cursorPosCallback;

	@SuppressWarnings("unused")
	private final GLFWMouseButtonCallback mouseButtonCallback;
	
	@SuppressWarnings("unused")
	private final GLFWScrollCallback scrollCallback;
	
	@SuppressWarnings("unused")
	private final GLFWFramebufferSizeCallback framebufferSizeCallback;
	
	public static Input getInstance() {
	    if(instance == null) {
	    	instance = new Input();
	    }
		return instance;
	}
	
	protected Input() {
		Window window = Window.getInstance();
		ImGuiIO io = ImGui.getIO();
		io.setKeyMap(ImGuiKey.Tab, GLFW_KEY_TAB);
		io.setKeyMap(ImGuiKey.LeftArrow, GLFW_KEY_LEFT);
		io.setKeyMap(ImGuiKey.RightArrow, GLFW_KEY_RIGHT);
		io.setKeyMap(ImGuiKey.UpArrow, GLFW_KEY_UP);
		io.setKeyMap(ImGuiKey.DownArrow, GLFW_KEY_DOWN);
		io.setKeyMap(ImGuiKey.PageUp, GLFW_KEY_PAGE_UP);
		io.setKeyMap(ImGuiKey.PageDown, GLFW_KEY_PAGE_DOWN);
		io.setKeyMap(ImGuiKey.Home, GLFW_KEY_HOME);
		io.setKeyMap(ImGuiKey.End, GLFW_KEY_END);
		io.setKeyMap(ImGuiKey.Insert, GLFW_KEY_INSERT);
		io.setKeyMap(ImGuiKey.Delete, GLFW_KEY_DELETE);
		io.setKeyMap(ImGuiKey.Backspace, GLFW_KEY_BACKSPACE);
		io.setKeyMap(ImGuiKey.Space, GLFW_KEY_SPACE);
		io.setKeyMap(ImGuiKey.Enter, GLFW_KEY_ENTER);
		io.setKeyMap(ImGuiKey.Escape, GLFW_KEY_ESCAPE);
		io.setKeyMap(ImGuiKey.KeyPadEnter, GLFW_KEY_KP_ENTER);

		cursorPosition = new Vec2f();
		
		glfwSetFramebufferSizeCallback(Window.getInstance().getWindow(), (framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
		    @Override
		    public void invoke(long window, int width, int height) {
				ImGuiIO imGuiIO = ImGui.getIO();
				imGuiIO.setDisplaySize(width, height);
		        Window.getInstance().setWindowSize(width, height);
		    }
		}));
		
		glfwSetKeyCallback(Window.getInstance().getWindow(), (keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
				io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
				io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
				io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
				io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));

            	if (action == GLFW_PRESS){
					io.setKeysDown(key, true);
            		if (!pushedKeys.contains(key)){
            			pushedKeys.add(key);
            			keysHolding.add(key);
            		}
                }

                if (action == GLFW_RELEASE){
					io.setKeysDown(key, false);
                	keysHolding.remove(Integer.valueOf(key));
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

		glfwSetCharCallback(window.getWindow(), (charCallback = new GLFWCharCallback() {
			@Override
			public void invoke(long window, int c) {
				if (!io.getWantCaptureKeyboard()) {
					return;
				}
				io.addInputCharacter(c);
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
                	buttonsHolding.remove(Integer.valueOf(button));
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
