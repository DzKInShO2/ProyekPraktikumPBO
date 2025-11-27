import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Gameplay extends JPanel {
    private Level level;
    private int player;
    private Thread updateThread;
    private volatile boolean isUpdateRunning;

    public Gameplay(Level level) {
        this.level = level;

        updateThread = new Thread() {
            public void run() {
                isUpdateRunning = true;
                while (isUpdateRunning) {
                }
            }
        };

        updateThread.start();
    }

    protected void paintComponent(Graphics g) {
        level.draw(g);
    }

    private void finished() {
    }
}
