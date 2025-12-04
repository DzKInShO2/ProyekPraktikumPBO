import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class LosePanel extends JPanel {
    public LosePanel(Runnable callback) {
        setLayout(new GridBagLayout());

        var label = new JLabel("Anda kalah hahahahahaha!");
        label.setAlignmentY(JLabel.CENTER_ALIGNMENT);

        var res = ResourceManager.getInstance();
        label.setForeground(Color.GRAY.darker());
        label.setFont(res.getFont());

        var instruction = new JLabel("Tekan [Escape] untuk kembali");
        instruction.setForeground(Color.GRAY);
        instruction.setFont(res.getFont().deriveFont(24f));

        add(label);

        var c = new GridBagConstraints();
        c.gridy = 1;
        add(instruction, c);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    callback.run();
                }
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        var res = ResourceManager.getInstance();
        var i = res.getBlueBackground();
        var w = i.getWidth(null);
        var h = i.getHeight(null);

        for (var y = 0; y < getHeight(); y += h) {
            for (var x = 0; x < getWidth(); x += w) {
                g.drawImage(i, x, y, w, h, this);
            }
        }
    }
}
