import java.awt.*;
import javax.swing.*;

public class Main {
    public static void switchPanel(Container pane, JPanel panel) {
        pane.removeAll();
        pane.add(panel);
        pane.revalidate();
        pane.repaint();
        panel.requestFocusInWindow();
    }

    public static MenuPanel createMenuPanel(Container pane) {
        // Aksi yang akan dilakukan pas gameplay selesai
        Finished finished = (code) -> {
            if (code == 0) {
                switchPanel(pane, createMenuPanel(pane));
            } else if (code == 1) {
                switchPanel(pane, new WinPanel(() -> {
                    switchPanel(pane, createMenuPanel(pane));
                }));
            }
        };

        return new MenuPanel(
                // CONTINUE
                () -> {
                    var fc = new JFileChooser();
                    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        var path = fc.getSelectedFile().getAbsolutePath();
                        if (SaveManager.initialize(path, false)) {
                            var level = SaveManager.getLevel();
                            var x = (float)SaveManager.getPositionX();
                            var y = (float)SaveManager.getPositionY();

                            var gameplay = new Gameplay(level, x, y, finished);
                            switchPanel(pane, gameplay);
                        } else {
                            JOptionPane.showMessageDialog(pane, 
                                    "File save tidak valid!",
                                    "Save File Error",
                                    JOptionPane.ERROR_MESSAGE
                                    );
                        }
                    }
                },

            // NEW GAME
            () -> {
                var fc = new JFileChooser();
                if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    var path = fc.getSelectedFile().getAbsolutePath();
                    if (SaveManager.initialize(path, true)) {
                        var gameplay = new Gameplay(0, 0, 0, finished);
                        switchPanel(pane, gameplay);
                    }
                }
            },

            // SETTINGS
            () -> {
                JOptionPane.showMessageDialog(pane, 
                        "Settings belum dibuat\nTapi tombol ini sudah aktif!",
                        "Settings",
                        JOptionPane.INFORMATION_MESSAGE
                        );
            },

            // EXIT
            () -> {
                System.exit(0);
            }
        );
    }

    public static void main(String[] args) {

        var app = new JFrame("Platformer Game");
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SwingUtilities.invokeLater(() -> {

            app.setSize(1280, 720);
            app.setResizable(false);
            app.setVisible(true);

            var input = InputManager.getInstance();

            var rootPane = app.getRootPane();
            var contentPane = app.getContentPane();
            var inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            var actionMap = rootPane.getActionMap();

            inputMap.put(KeyStroke.getKeyStroke("pressed D"), "moveX");
            inputMap.put(KeyStroke.getKeyStroke("pressed A"), "moveX");
            inputMap.put(KeyStroke.getKeyStroke("released D"), "stillX");
            inputMap.put(KeyStroke.getKeyStroke("released A"), "stillX");
            inputMap.put(KeyStroke.getKeyStroke("pressed SPACE"), "jumpY");

            actionMap.put("moveX", input.getActionMoveX());
            actionMap.put("stillX", input.getActionStillX());
            actionMap.put("jumpY", input.getActionJumpY());

            // 2. CREATE MENU PANEL DENGAN CALLBACK 4 TOMBOL
            var menu = createMenuPanel(contentPane);
            switchPanel(contentPane, menu);
        });
    }
}
