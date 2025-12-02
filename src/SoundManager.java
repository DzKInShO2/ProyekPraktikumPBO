import java.io.File;
import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

public class SoundManager {
    private static float volume = 1.0f;   // 0.0 – 1.0
    private static boolean muted = false;

    private static Clip bgmClip = null;
    // Tracking semua SFX Clip yang sedang diputar
    private static List<Clip> activeSfxClips = new ArrayList<>();
    // Tracking khusus untuk walk sound (agar bisa di-stop saat berhenti)
    private static Clip walkClip = null;

    // Convert volume linear (0–1) ke decibel (-80 – 6 dB)
    private static float toDecibel(float vol) {
        if (vol <= 0f) return -80f;
        return (float)(Math.log10(vol) * 20.0);
    }

    // Play SFX sekali
    public static void playOnce(File file) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);

            FloatControl ctrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            ctrl.setValue(muted ? -80f : toDecibel(volume));

            // Bersihkan Clip yang sudah selesai dari list
            activeSfxClips.removeIf(c -> !c.isRunning());

            // Tambahkan ke tracking list
            activeSfxClips.add(clip);

            clip.start();

            // Auto-remove ketika selesai
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    activeSfxClips.remove(clip);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Play walk sound (dengan tracking khusus)
    public static void playWalkSound(File file) {
        // Stop walk sound yang lama jika masih jalan
        stopWalkSound();
        
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            walkClip = AudioSystem.getClip();
            walkClip.open(stream);

            FloatControl ctrl = (FloatControl) walkClip.getControl(FloatControl.Type.MASTER_GAIN);
            ctrl.setValue(muted ? -80f : toDecibel(volume));

            walkClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Stop walk sound
    public static void stopWalkSound() {
        if (walkClip != null && walkClip.isRunning()) {
            walkClip.stop();
            walkClip.close();
            walkClip = null;
        }
    }

    // Stop semua SFX yang sedang diputar
    public static void stopAllSFX() {
        stopWalkSound(); // Stop walk sound juga
        for (Clip clip : new ArrayList<>(activeSfxClips)) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.close();
            }
        }
        activeSfxClips.clear();
    }

    // Load dan mulai BGM looping
    public static void playBGM(File file) {
        stopBGM();
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(stream);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);

            FloatControl ctrl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
            ctrl.setValue(muted ? -80f : toDecibel(volume));

            bgmClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    public static void setVolume(float v) {
        volume = Math.max(0f, Math.min(1f, v)); // clamp

        if (bgmClip != null) {
            try {
                FloatControl ctrl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
                ctrl.setValue(muted ? -80f : toDecibel(volume));
            } catch (Exception ignored) {}
        }
    }

    public static void mute(boolean state) {
        muted = state;

        if (bgmClip != null) {
            FloatControl ctrl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
            ctrl.setValue(muted ? -80f : toDecibel(volume));
        }
    }

    public static boolean isMuted() {
        return muted;
    }

    public static float getVolume() {
        return volume;
    }
}
