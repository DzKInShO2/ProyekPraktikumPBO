import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPanel extends JPanel {

    private Image bg;

    private Image[] buttons = new Image[4];
    private String[] names = {"Continue", "NewGame", "Settings", "Exit"};

    private Runnable[] callbacks = new Runnable[4];

    private final float SCALE = 0.6f;
    private final float SCALE_HOVER = 0.7f;

    private float[] currentScale = {SCALE, SCALE, SCALE, SCALE};

    private int hoveredIndex = -1;

    private final int START_Y = 240;
    private final int GAP = 90;

    public MenuPanel(Runnable onContinue, Runnable onNewGame, Runnable onSettings, Runnable onExit) {

        callbacks[0] = onContinue;
        callbacks[1] = onNewGame;
        callbacks[2] = onSettings;
        callbacks[3] = onExit;

        bg = new ImageIcon("res/PixelAdventure1Free/Background/MainMenu.jpg").getImage();

        buttons[0] = new ImageIcon("res/UI/btn_continue.png").getImage();
        buttons[1] = new ImageIcon("res/UI/btn_newGame.png").getImage();
        buttons[2] = new ImageIcon("res/UI/btn_settings.png").getImage();
        buttons[3] = new ImageIcon("res/UI/btn_exit.png").getImage();

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoveredIndex = getHoverButtonIndex(e.getX(), e.getY());
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = getHoverButtonIndex(e.getX(), e.getY());
                if (index != -1) {
                    callbacks[index].run();
                }
            }
        });

        Timer timer = new Timer(16, e -> updateAnimation());
        timer.start();

        setFocusable(true);
    }

    private int getHoverButtonIndex(int mx, int my) {
        for (int i = 0; i < buttons.length; i++) {
            int bw = (int)(buttons[i].getWidth(null) * currentScale[i]);
            int bh = (int)(buttons[i].getHeight(null) * currentScale[i]);

            int x = (getWidth() - bw) / 2;
            int y = START_Y + i * GAP;

            if (mx >= x && mx <= x + bw && my >= y && my <= y + bh) {
                return i;
            }
        }
        return -1;
    }

    private void updateAnimation() {

        for (int i = 0; i < buttons.length; i++) {
            float target = (i == hoveredIndex ? SCALE_HOVER : SCALE);

            // LERP
            currentScale[i] += (target - currentScale[i]) * 0.2f;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);

        for (int i = 0; i < buttons.length; i++) {
            drawButton(g, buttons[i], currentScale[i], i);
        }
    }

    private void drawButton(Graphics g, Image img, float scale, int index) {

        int originalW = img.getWidth(null);
        int originalH = img.getHeight(null);

        int w = (int)(originalW * scale);
        int h = (int)(originalH * scale);

        int x = (getWidth() - w) / 2;
        int y = START_Y + index * GAP;

        if (index == hoveredIndex) {
            g.setColor(new Color(255, 255, 255, 80));
            g.fillRoundRect(x - 10, y - 10, w + 20, h + 20, 20, 20);
        }

        g.drawImage(img, x, y, w, h, null);
    }
}
