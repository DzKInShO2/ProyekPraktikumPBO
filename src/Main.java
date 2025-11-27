import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        var app = new JFrame();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SwingUtilities.invokeLater(() -> {
            app.setSize(1280, 720);
            app.setResizable(false);
            app.setVisible(true);

            var tileCountX = ResourceManager.getInstance().getTileCountX();
            var level = new Level(new int[] {
                0, 0, 0,
                7, 8, 9,
                tileCountX + 7, tileCountX + 8, tileCountX + 9
            }, 3, 3);
            var gameplay = new Gameplay(level);

            app.getContentPane().add(gameplay);
        });
    }
}
