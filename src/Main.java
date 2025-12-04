import java.awt.*;
import java.awt.event.KeyEvent;
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
        
        Finished finished = (code) -> {
            if (code == 0) {
                switchPanel(pane, createMenuPanel(pane));
            } else if (code == 1) {
                switchPanel(pane, new WinPanel(() -> {
                    switchPanel(pane, createMenuPanel(pane));
                }));
            } else if (code == -1) {
                switchPanel(pane, new LosePanel(() -> {
                    switchPanel(pane, createMenuPanel(pane));
                }));
            }
        };

        return new MenuPanel(
            
            // 1. Tombol CONTINUE
            () -> {
                var fc = new JFileChooser();
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    var path = fc.getSelectedFile().getAbsolutePath();
                    
                    if (SaveManager.initialize(path, false)) {
                        var level = SaveManager.getLevel();
                        var health = SaveManager.getHealth();
                        var x = (float)SaveManager.getPositionX();
                        var y = (float)SaveManager.getPositionY();

                        var gameplay = new Gameplay(level, health, x, y, finished);
                        switchPanel(pane, gameplay);
                    } else {
                        JOptionPane.showMessageDialog(pane, 
                            "Save file tidak valid atau corrupt!",
                            "Error Loading",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            },

            // 2. Tombol NEW GAME
            () -> {
                var fc = new JFileChooser();
                if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    var path = fc.getSelectedFile().getAbsolutePath();
                    
                    // Initialize save baru (reset level 1)
                    if (SaveManager.initialize(path, true)) {
                        var gameplay = new Gameplay(0, 4, 100, 100, finished);
                        switchPanel(pane, gameplay);
                    }
                }
            },

            // 3. Tombol SETTINGS
            () -> {
                switchPanel(pane, new SettingsPanel(() -> {
                    switchPanel(pane, createMenuPanel(pane));
                }));
            },

            // 4. Tombol EXIT
            () -> {
                int confirm = JOptionPane.showConfirmDialog(pane, 
                    "Apakah anda yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        );
    }

    public static void main(String[] args) {

        var app = new JFrame("Super Bagas Pro");
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SwingUtilities.invokeLater(() -> {
            app.setSize(1280, 720);
            app.setResizable(false);
            app.setLocationRelativeTo(null); 
            
            var input = InputManager.getInstance();
            var rootPane = app.getRootPane();
            input.setKeymap(rootPane, "D", "A", "SPACE");

            var contentPane = app.getContentPane();
            var menu = createMenuPanel(contentPane);
            switchPanel(contentPane, menu);
            
            app.setVisible(true);
        });
    }
}
