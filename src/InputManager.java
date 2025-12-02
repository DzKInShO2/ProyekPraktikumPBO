import javax.swing.*;
import java.awt.event.*;

public class InputManager {
    private static volatile InputManager instance;

    // === KEY BINDINGS (bisa diubah dari SettingsPanel) ===
    private static int keyRight = KeyEvent.VK_D;
    private static int keyLeft  = KeyEvent.VK_A;
    private static int keyJump  = KeyEvent.VK_SPACE;

    // State Variables
    private volatile boolean isMoveRight;
    private volatile boolean isMoveLeft;
    private volatile boolean isJump;

    // Actions yang terpisah biar bisa di-map ke tombol apa saja
    private Action moveRight;
    private Action stopRight;
    private Action moveLeft;
    private Action stopLeft;
    private Action jump;

    private InputManager() {
        isMoveRight = false;
        isMoveLeft = false;
        isJump = false;

        // 1. ACTION KANAN
        moveRight = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isMoveRight = true;
            }
        };

        stopRight = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isMoveRight = false;
            }
        };

        // 2. ACTION KIRI
        moveLeft = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isMoveLeft = true;
            }
        };

        stopLeft = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isMoveLeft = false;
            }
        };

        // 3. ACTION LOMPAT
        jump = new AbstractAction() {
            @Override
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

    // --- GETTER UNTUK ACTIONS (dipake di Main.java) ---
    public Action getActionMoveRight() { return moveRight; }
    public Action getActionStopRight() { return stopRight; }
    
    public Action getActionMoveLeft() { return moveLeft; }
    public Action getActionStopLeft() { return stopLeft; }
    
    public Action getActionJump() { return jump; }

    // --- GETTER UNTUK STATE (dipake di Gameplay Loop) ---
    public boolean getMoveRight() { return isMoveRight; }
    public boolean getMoveLeft() { return isMoveLeft; }
    public boolean getJump() { return isJump; }

    // Reset jump agar tidak terbang terus (panggil setelah lompat dieksekusi)
    public void resetJump() {
        isJump = false;
    }

    // === STATIC GET/SET UNTUK KEY BINDINGS ===
    public static int getKeyRight() { return keyRight; }
    public static int getKeyLeft()  { return keyLeft; }
    public static int getKeyJump()  { return keyJump; }

    public static void setKeyRight(int code) { keyRight = code; }
    public static void setKeyLeft(int code)  { keyLeft = code; }
    public static void setKeyJump(int code)  { keyJump = code; }
}