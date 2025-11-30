import javax.swing.*;
import java.awt.event.*;

public class InputManager {
    private static volatile InputManager instance;

    private volatile boolean isMoveRight;
    private volatile boolean isMoveLeft;
    private volatile boolean isJump;

    private Action moveX;
    private Action stillX;
    private Action jumpY;

    private InputManager() {
        isMoveRight = false;
        isMoveLeft = false;
        isJump = false;

        moveX = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                var key = e.getActionCommand().charAt(0);
                if (key == 'd') {
                    isMoveRight = true;
                } else if (key == 'a') {
                    isMoveLeft = true;
                }
            }
        };

        stillX = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                var key = e.getActionCommand().charAt(0);
                if (key == 'd') {
                    isMoveRight = false;
                } else if (key == 'a') {
                    isMoveLeft = false;
                }
            }
        };

        jumpY = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                isJump = true;
            }
        };
    }

    public static InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }

        return instance;
    }

    public Action getActionMoveX() {
        return moveX;
    }

    public Action getActionStillX() {
        return stillX;
    }

    public Action getActionJumpY() {
        return jumpY;
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
