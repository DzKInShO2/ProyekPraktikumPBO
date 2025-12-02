import java.io.File;
import javax.sound.sampled.*;

public class SoundManager {
    public static float volume = 0.0f;
    public static void playOnce(File file) {
        try {
            var stream = AudioSystem.getAudioInputStream(file);
            var clip = AudioSystem.getClip();
            clip.open(stream);

            var control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(volume);

            clip.start();
        } catch (Exception e) {
        };
    }

    public static Clip getLoop(File file) {
        Clip clip = null;
        try {
            var stream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(stream);

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.stop();
        } catch (Exception e) {
        };

        return clip;
    }

    public static void play(Clip clip) {
        try {
            var control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(volume);

            clip.start();
        } catch (Exception e) {}
    }

    public static void stop(Clip clip) {
        clip.stop();
    }
}
