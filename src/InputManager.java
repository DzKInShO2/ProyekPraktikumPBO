import javax.swing.*;
import java.awt.event.*;

public class InputManager {
    private static volatile InputManager instance;

    private volatile boolean isMoveRight;
    private volatile boolean isMoveLeft;
    private volatile boolean isJump;

    private String moveRight;
    private String moveLeft;
    private String jump;

    private Action moveX;
    private Action stillX;
    private Action jumpY;

    private boolean isJustInitialized;

    private InputManager() {
        isJustInitialized = true;

        isMoveRight = false;
        isMoveLeft = false;
        isJump = false;

        moveX = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                var key = e.getActionCommand().charAt(0);
                var keyRight = moveRight.toLowerCase().charAt(0);
                var keyLeft = moveLeft.toLowerCase().charAt(0);

                if (key == keyRight) {
                    isMoveRight = true;
                } else if (key == keyLeft) {
                    isMoveLeft = true;
                }
            }
        };

        stillX = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                var key = e.getActionCommand().charAt(0);
                var keyRight = moveRight.toLowerCase().charAt(0);
                var keyLeft = moveLeft.toLowerCase().charAt(0);

                if (key == keyRight) {
                    isMoveRight = false;
                } else if (key == keyLeft) {
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
            synchronized (InputManager.class) {
                if (instance == null) {
                    instance = new InputManager();
                }
            }
        }
        return instance;
    }

    public void setKeymap(JRootPane root, String moveRight, String moveLeft, String jump) {
        var inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        inputMap.clear();
        inputMap.put(KeyStroke.getKeyStroke("pressed " + moveRight), "moveX");
        inputMap.put(KeyStroke.getKeyStroke("released " + moveRight),  "stillX");
        inputMap.put(KeyStroke.getKeyStroke("pressed " + moveLeft), "moveX");
        inputMap.put(KeyStroke.getKeyStroke("released " + moveLeft),  "stillX");
        inputMap.put(KeyStroke.getKeyStroke("pressed " + jump), "jumpY");

        this.moveRight = moveRight;
        this.moveLeft = moveLeft;
        this.jump = jump;

        if (isJustInitialized) {
            var actionMap = root.getActionMap();
            actionMap.put("moveX", moveX);
            actionMap.put("stillX", stillX);
            actionMap.put("jumpY",  jumpY);

            isJustInitialized = false;
        }
    }

    public String getMoveRightString() {
        return moveRight;
    }

    public String getMoveLeftString() {
        return moveLeft;
    }

    public String getJumpString() {
        return jump;
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
