import java.io.File;
import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

public class SoundManager {
    public static float volume = 1.0f;
    public static boolean muted = false;

    private static float toDecibel(float vol) {
        if (vol <= 0f) return -80f;
        return (float)(Math.log10(vol) * 20.0);
    }

    public static void playOnce(File file) {
        if (muted) return;

        try {
            var stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);

            var ctrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            ctrl.setValue(muted ? -80f : toDecibel(volume));
            clip.start();
        } catch (Exception e) {
        }
    }

    public static Clip getLoop(File file) {
        try {
            var stream = AudioSystem.getAudioInputStream(file);
            var clip = AudioSystem.getClip();
            clip.open(stream);

            return clip;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void play(Clip clip) {
        if (muted) return;

        var ctrl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        ctrl.setValue(muted ? -80f : toDecibel(volume));

        clip.start();
    }
    
    public static void stop(Clip clip) {
        clip.stop();
    }

    public static float getVolume() {
        return volume;
    }
}
