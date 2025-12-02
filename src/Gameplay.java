import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Gameplay extends JPanel {
    private int ilevel;
    private Level level;
    private Player player;

    private Finished finished;
    private Thread updateThread;
    private Thread drawThread;
    private volatile boolean isDrawRunning;
    private volatile boolean isUpdateRunning;

    public Gameplay(int level, float x, float y, Finished finished) {
        SaveManager.setLevel(level);
        SaveManager.setPosition(x, y);

        var res = ResourceManager.getInstance();
        this.level = res.getLevel(ilevel = level);

        player = new Player(this.level, (code) -> {
            finished();
        }, x, x);

        updateThread = new Thread() {
            public void run() {
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
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.draw(g);
        level.draw(g);
    }

    private void finished() {
        isUpdateRunning = false;
        isDrawRunning = false;

        var res = ResourceManager.getInstance();
        var nextLevel = res.getLevel(++ilevel);
        if (nextLevel != null) {
            var rootPane = getRootPane().getContentPane();

            rootPane.removeAll();

            var gameplay = new Gameplay(ilevel, 0, 0, finished);
            rootPane.add(gameplay);

            rootPane.revalidate();
            rootPane.repaint();
        } else {
            finished.finished(0);
        }
    }
}
