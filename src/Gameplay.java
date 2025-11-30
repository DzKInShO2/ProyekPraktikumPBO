import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Gameplay extends JPanel {
    private Level level;
    private Player player;
    private Finished finished;
    private Thread updateThread;
    private Thread drawThread;
    private volatile boolean isDrawRunning;
    private volatile boolean isUpdateRunning;

    public Gameplay(Level level, Finished finished) {
        setFocusable(true);
        setRequestFocusEnabled(true);

        this.level = level;
        this.finished = finished;
        player = new Player(level, (code) -> {
            finished();
        }, 0, 0);

        updateThread = new Thread() {
            public void run() {
                var input = InputManager.getInstance();
                addKeyListener(input);

                isUpdateRunning = true;

                long lastTime = System.nanoTime();
                try {
                    while (isUpdateRunning) {
                        var currentTime = System.nanoTime();
                        var deltaTime = currentTime - lastTime;
                        lastTime = currentTime;

                        player.update((float)deltaTime * (float)1e-9);
                    }
                } catch (Exception e) {}
            }
        };

        drawThread = new Thread(() -> { 
            isDrawRunning = true;
            try {
                while (isDrawRunning) {
                    repaint();
                    Thread.sleep(10);
                }
            } catch (Exception e) {}
        });

        updateThread.start();
        drawThread.start();

        requestFocus();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.draw(g);
        level.draw(g);
    }

    private void finished() {
        isUpdateRunning = false;
        isDrawRunning = false;

        finished.finished(0);
    }
}
