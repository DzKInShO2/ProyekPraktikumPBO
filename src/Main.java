import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        var app = new JFrame();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SwingUtilities.invokeLater(() -> {
            app.setSize(1280, 720);
            app.setResizable(false);
            app.setVisible(true);

            // Bind input
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

            var contentPane = app.getContentPane();

            // Disini tempat ganti UI
            var gameplay = new Gameplay(0, (code) -> {});
            contentPane.add(gameplay);
        });
    }
}
