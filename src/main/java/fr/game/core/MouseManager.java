package fr.game.core;

import fr.game.core.utils.Consts;
import fr.game.test.Launcher;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseManager {

    private double oldMouseX = 0, oldMouseY = 0, newMouseX, newMouseY;
    private float horizontalAngle = 0.0f, verticalAngle = 0.0f, distance = 2.0f;

    private boolean inWindow = false, leftButtonPressed = false, rightButtonPressed = false;

    private boolean isLocked = false;

    public MouseManager() {
        horizontalAngle = 0.0f;
        verticalAngle = 0.0f;
    }

    public void init() {
        GLFW.glfwSetCursorPosCallback(Launcher.getWindow().getWindow(), (window, xpos, ypos) -> {
            newMouseX = xpos;
            newMouseY = ypos;
        });

        GLFW.glfwSetCursorEnterCallback(Launcher.getWindow().getWindow(), (window, entered) -> {
            inWindow = entered;
        });

        GLFW.glfwSetMouseButtonCallback(Launcher.getWindow().getWindow(), (window, button, action, mode) -> {
            leftButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            rightButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
        });
    }

    public void input() {

        float dx = (float) (newMouseX - oldMouseX);
        float dy = (float) (newMouseY - oldMouseY);

        verticalAngle -= dy * Consts.CAMERA_ROTATE_SPEED;
        horizontalAngle += dx * Consts.CAMERA_ROTATE_SPEED;

        // set old mouse position to new mouse position
        oldMouseX = newMouseX;
        oldMouseY = newMouseY;



    }

    public float getHorizontalAngle() {
        return horizontalAngle;
    }

    public float getVerticalAngle() {
        return verticalAngle;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public boolean isInWindow() {
        return inWindow;
    }

    public void lockCursor() {
        GLFW.glfwSetInputMode(Launcher.getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        isLocked = true;
    }

    public void unlockCursor() {
        GLFW.glfwSetInputMode(Launcher.getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        isLocked = false;
    }

    public boolean isLocked() {
        return isLocked;
    }
}
