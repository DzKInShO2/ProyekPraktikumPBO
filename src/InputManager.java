import java.awt.event.*;

public class InputManager extends KeyAdapter {
    private static volatile InputManager instance;

    private volatile boolean isMoveRight;
    private volatile boolean isMoveLeft;
    private volatile boolean isJump;

    private InputManager() {
        isMoveRight = false;
        isMoveLeft = false;
        isJump = false;
    }

    public void keyPressed(KeyEvent e) {
        var keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_D) {
            isMoveRight = true;
        } else if (keyCode == KeyEvent.VK_A) {
            isMoveLeft = true;
        } else if (keyCode == KeyEvent.VK_SPACE) {
            isJump = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        var keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_D) {
            isMoveRight = false;
        } else if (keyCode == KeyEvent.VK_A) {
            isMoveLeft = false;
        }
    }

    public static InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    public boolean getMoveRight() {
        return isMoveRight;
    }

    public boolean getMoveLeft() {
        return isMoveLeft;
    }

    public boolean getJump() {
        return isJump;
    }

    public void resetJump() {
        isJump = false;
    }
}
