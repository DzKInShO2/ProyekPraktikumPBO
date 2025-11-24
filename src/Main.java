import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        var app = new JFrame();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SwingUtilities.invokeLater(() -> {
            app.setResizable(false);
            app.setVisible(true);
        });
    }
}
