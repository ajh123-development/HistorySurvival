package net.ddns.minersonline.HistorySurvival.engine.io;

import net.ddns.minersonline.HistorySurvival.engine.GuiManager;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse {
	private static boolean[] buttons;
	private static double mouseX, mouseY, lastMouseX, lastMouseY, lastScrollMouseX, lastScrollMouseY, deltaX, deltaY, deltaScrollY, deltaScrollX;
	private static double mouseScrollX, mouseScrollY;
	private GLFWCursorPosCallback mouseMove;
	private GLFWMouseButtonCallback mouseButtons;
	private GLFWScrollCallback mouseScroll;

	public Mouse(GuiManager renderer) {
		buttons = new boolean[GLFW_MOUSE_BUTTON_LAST];

		mouseMove = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				mouseX = xpos;
				mouseY = ypos;
			}
		};

		mouseButtons = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				buttons[button] = (action == GLFW_PRESS);
				renderer.mouseButtonCallback(window, button, action, mods);
			}
		};

		mouseScroll = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double offsetX, double offsetY) {
				mouseScrollX += offsetX;
				mouseScrollY += offsetY;
				renderer.scrollCallback(window, offsetX, offsetY);
			}
		};
	}

	public static float getDX() {
		return (float) deltaX;
	}

	public static float getDY() {
		return (float) deltaY;
	}

	public static float getDMouseScrollY() {
		return (float) deltaScrollY  * 10;
	}

	public static float getDMouseScrollX() {
		return (float) deltaScrollX * 10;
	}

	public GLFWCursorPosCallback getMouseMoveCallback() {
		return mouseMove;
	}

	public GLFWMouseButtonCallback getMouseButtonsCallback() {
		return mouseButtons;
	}

	public GLFWScrollCallback getMouseScrollCallback() {
		return mouseScroll;
	}

	public void destroy() {
		mouseMove.free();
		mouseButtons.free();
		mouseScroll.free();
	}

	public static boolean isButtonDown(int button) {
		return buttons[button];
	}

	public static boolean isButtonClicked(int button) {
		boolean cached = buttons[button];
		buttons[button] = false;
		return cached;
	}

	public static double getMouseX() {
		return mouseX;
	}

	public static double getMouseY() {
		return mouseY;
	}

	public static double getLastMouseX() {
		return lastMouseX;
	}

	public static double getLastMouseY() {
		return lastMouseY;
	}

	public static void setLastMouseX(double lastMouseX) {
		Mouse.lastMouseX = lastMouseX;
	}

	public static void setLastMouseY(double lastMouseY) {
		Mouse.lastMouseY = lastMouseY;
	}

	public static double getMouseScrollX() {
		double cache = mouseScrollX * 10;
		mouseScrollX = 0; return cache;
	}

	public static double getMouseScrollY() {
		double cache = mouseScrollY * 10;
		mouseScrollY = 0; return cache;
	}

	public static void update(){
		deltaX = mouseX - lastMouseX;
		deltaY = mouseY - lastMouseY;
		deltaScrollX = mouseScrollX - lastScrollMouseX;
		deltaScrollY = mouseScrollY - lastScrollMouseY;
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		lastScrollMouseY = mouseScrollY;
		lastScrollMouseX = mouseScrollX;
	}
}
