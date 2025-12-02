import java.awt.*;
import javax.imageio.*;

public class AnimationClip {
    private float time;
    private float spf;
    private int frame;

    private Image[] images;

    public AnimationClip(Image[] images, int fps) {
        this.images = images;
        time = 0;
        frame = 0;
        spf = 1.0f/(float)fps;
    }

    public void update(float dt) {
        time = time + dt;
        if (time > spf) {
            frame++;
            if (frame >= images.length)
                frame = 0;
            time = 0;
        }
    }

    public Image getFrame() {
        var image = images[frame];
        return image;
    }

    public void reset() {
        time = 0;
        frame = 0;
    }
}
