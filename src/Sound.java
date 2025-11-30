import javax.sound.sampled.*;
import java.io.File;

public class Sound {

    public static void play(String path) {
        new Thread(() -> {
            try {
                File file = new File(path);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
