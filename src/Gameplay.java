import java.awt.*;
import java.awt.event*;
import javax.swing.*;

public class Gameplay extends JPanel {
    private int level;
    private int player;
    private Thread updateThread;
    private volatile boolean isUpdateRunning;

    public Gameplay(int level) {
        this.level = level;

        updateThread = new Thread() {
            public void run() {
                isUpdateRunning = true;
                while (isUpdateRunning) {
                }
            }

            public void stop() {
                isUpdateRunning = false;
            }
        };

        updateThread.start();
    }

    protected void paintComponent(Graphics g) {
    }

    private void finished() {
    }
}
