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
            // 0 = Quit to Menu, 1 = Win/Next Level
            if (code == 0) {
                switchPanel(pane, createMenuPanel(pane));
            } else if (code == 1) {
                switchPanel(pane, new WinPanel(() -> {
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
                        var x = (float)SaveManager.getPositionX();
                        var y = (float)SaveManager.getPositionY();

                        var gameplay = new Gameplay(level, x, y, finished);
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
                        var gameplay = new Gameplay(0, 100, 100, finished);
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

    private static void setupGlobalInputs(JFrame app) {
        var input = InputManager.getInstance();
        var rootPane = app.getRootPane();
        var inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var actionMap = rootPane.getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true),  "stopRight");
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true),  "stopLeft");
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "jump");

        // --- MAPPING AKSI KE INPUT MANAGER ---
        actionMap.put("moveRight", input.getActionMoveRight());
        actionMap.put("stopRight", input.getActionStopRight());
        actionMap.put("moveLeft",  input.getActionMoveLeft());
        actionMap.put("stopLeft",  input.getActionStopLeft());
        actionMap.put("jump",      input.getActionJump());
    }

    public static void main(String[] args) {

        var app = new JFrame("Super Bagas Pro");
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SwingUtilities.invokeLater(() -> {

            app.setSize(1280, 720);
            app.setResizable(false);
            app.setLocationRelativeTo(null); 
            
            // Setup input keyboard
            setupGlobalInputs(app);

            // Tampilkan Panel Menu Awal
            var contentPane = app.getContentPane();
            var menu = createMenuPanel(contentPane);
            
            switchPanel(contentPane, menu);
            
            app.setVisible(true);
        });
    }
}