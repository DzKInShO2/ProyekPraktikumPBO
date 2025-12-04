import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Gameplay extends JPanel {
    private int ilevel;
    private Level level;
    private Player player;
    private int attemp;

    private Finished finished;
    private Thread updateThread;
    private Thread drawThread;
    private volatile boolean isDrawRunning;
    private volatile boolean isUpdateRunning;
    private boolean isPaused;

    public Gameplay(int level, int health, float x, float y, Finished finished) {
        setLayout(new BorderLayout(0, 0));

        SaveManager.setHealth(health);
        SaveManager.setLevel(level);
        SaveManager.setPosition(x, y);

        var res = ResourceManager.getInstance();
        this.level = res.getLevel(ilevel = level);
        this.finished = finished;

        attemp = health;
        isPaused = false;
        player = new Player(this.level, (code) -> {
            if (code == 0) {
                finished();
            } else if (code == -1) {
                attemp -= 1;
                if (attemp < 1) {
                    isUpdateRunning = false;
                    isDrawRunning = false;

                    finished.finished(-1);
                } else {
                    SaveManager.setHealth(attemp);
                }
            }
        }, x, y);

        var levelLabel = new JLabel(String.format("Level %d", level + 1));
        levelLabel.setFont(res.getFont());

        add(levelLabel, BorderLayout.NORTH);

        var pausePanel = new JPanel();
        pausePanel.setOpaque(true);
        pausePanel.setLayout(new GridBagLayout());
        {
            pausePanel.setBackground(new Color(0, 0, 0, 160));

            var pauseLabel = new JLabel("Paused.");
            pauseLabel.setFont(res.getFont());
            pauseLabel.setForeground(Color.GRAY);

            var continueLabel = new JLabel("Tekan [Enter] untuk lanjut");
            continueLabel.setFont(res.getFont().deriveFont(24f));
            continueLabel.setForeground(Color.GRAY.brighter());

            var backLabel = new JLabel("Tekan [Escape] untuk kembali ke Menu Utama");
            backLabel.setFont(res.getFont().deriveFont(24f));
            backLabel.setForeground(Color.GRAY.brighter());

            var c = new GridBagConstraints();
            pausePanel.add(pauseLabel);

            c.gridy = 1;
            pausePanel.add(continueLabel, c);

            c.gridy = 2;
            pausePanel.add(backLabel, c);
        }

        Runnable update = () -> {
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
        };

        updateThread = new Thread(update);

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

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (isPaused) {
                        SaveManager.setLevel(level);
                        SaveManager.setPosition(player.posX, player.posY);
                        finished.finished(0);
                    }

                    isPaused = true;
                    isUpdateRunning = false;

                    add(pausePanel);
                    revalidate();
                }

                if (e.getKeyCode() == KeyEvent.VK_ENTER && isPaused) {
                    isPaused = false;

                    remove(pausePanel);
                    revalidate();

                    updateThread = new Thread(update);
                    updateThread.start();
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        var g2d = (Graphics2D)g;

        var res = ResourceManager.getInstance();
        var heart = res.getHealth();
        var tileSize = res.TILE_SIZE * res.TILE_TO_SCREEN;
        var heartSize = (int)(tileSize / 1.5);

        player.draw(g);
        level.draw(g);

        var health = String.format("x %d", attemp);
        g2d.drawImage(heart, 0, tileSize, heartSize, heartSize, null);

        g2d.setFont(res.getFont().deriveFont(20f));
        g2d.drawString(health, heartSize * 1.2f, tileSize + (heartSize / 1.5f));
    }

    private void finished() {
        isUpdateRunning = false;
        isDrawRunning = false;

        var res = ResourceManager.getInstance();
        var nextLevel = res.getLevel(++ilevel);
        if (nextLevel != null) {
            var rootPane = getRootPane().getContentPane();

            rootPane.removeAll();

            var gameplay = new Gameplay(ilevel, attemp, 0, 0, finished);
            rootPane.add(gameplay);

            rootPane.revalidate();
            rootPane.repaint();
        } else {
            finished.finished(1);
        }
    }
}
