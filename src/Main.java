import javax.swing.*;

public class Main {

    public enum GameState {
        MENU,
        PLAY,
        FINISHED
    }

    public static GameState state = GameState.MENU;

    public static void switchPanel(JFrame frame, JPanel panel) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();
        panel.requestFocusInWindow();
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

            MenuPanel menu = new MenuPanel(

                // CONTINUE
                () -> {
                    // Continue = lanjutkan level terakhir (sementara level 0)
                    // Perlu di ubah di sini untuk saveManagernya terpakai atau endaknya hehe @dzaka
                    Gameplay gameplay = new Gameplay(0, (code) -> {});
                    switchPanel(app, gameplay);
                },

                // NEW GAME
                () -> {
                    // New game = mulai level 0
                    Gameplay gameplay = new Gameplay(0, (code) -> {});
                    switchPanel(app, gameplay);
                },

                // SETTINGS
                () -> {
                    JOptionPane.showMessageDialog(app, 
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

            switchPanel(app, menu);
        });
    }
}