import java.awt.*;

public class Level {
    private int[] data;
    private int width;
    private int height;

    public Level(int[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        var res = ResourceManager.getInstance();

        var g2d = (Graphics2D)g;
        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                var i = data[y * width + x] - 1;

                if (i > 0) {
                    g2d.drawImage(res.getTile(i), 
                            x * res.TILE_SIZE * 4, y * res.TILE_SIZE * 4,
                            res.TILE_SIZE * 4, res.TILE_SIZE * 4, 
                            null);
                }
            }
        }
    }
}
