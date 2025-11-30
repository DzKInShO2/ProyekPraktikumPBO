import java.awt.*;
import java.awt.image.*;

public class Level {
    private int[][] data;
    private int width;
    private int height;

    private float offsetX;
    private Image image;

    public Level(int[][] data) {
        this.data = data;
        this.width = data[0].length;
        this.height = data.length;

        offsetX = 0;

        var res = ResourceManager.getInstance();
        var tileSize = res.TILE_SIZE * res.TILE_TO_SCREEN;
        var checkpointSize = res.CHECKPOINT_SIZE * res.TILE_TO_SCREEN;

        var buffer = new BufferedImage(width * tileSize, height * tileSize,
                BufferedImage.TYPE_INT_ARGB);

        var g2d = (Graphics2D)buffer.createGraphics();
        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                var i = data[y][x] - 1;

                if (i > 0) {
                    g2d.drawImage(res.getTile(i), 
                            x * tileSize, y * tileSize,
                            tileSize, tileSize, 
                            null);
                } else if (i == -2) {
                    g2d.drawImage(res.getCheckpoint(), 
                            x * tileSize, y * tileSize,
                            checkpointSize, checkpointSize, 
                            null);
                }
            }
        }

        image = (Image)buffer;
    }

    public int getTile(int x, int y) {
        if (x > width - 1 || x < 0) return 0;
        if (y > height - 1 || y < 0) return 0;

        return data[y][x];
    }

    public boolean isTileEmpty(int x, int y) {
        if (x > width - 1|| x < 0) return false;
        if (y < 0 || y > height - 1) return true;

        return data[y][x] <= 0;
    }

    public void setOffset(float x) {
        offsetX = -x;
    }

    public void draw(Graphics g) {
        var g2d = (Graphics2D)g;
        g2d.drawImage(image, (int)offsetX, 0, null);
    }
}
