import java.awt.*;
import java.awt.image.*;

public class Level {
    private int[][] data;
    private int width;
    private int height;

    private int goalGridX;
    private int goalGridY;

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

                    goalGridX = x;
                    goalGridY = y;
                }
            }
        }

        image = (Image)buffer;
    }

    public int getHeight() {
        return height;
    }

    public int getTile(float x, float y) {
        int iX = (int)x;
        int iY = (int)y;

        if (iX >= width || iX < 0) return 1;
        if (iY < 0 || iY >= height) return 0;

        return data[iY][iX];
    }

    public boolean isInCheckpoint(float x, float y) {
        var res = ResourceManager.getInstance();
        var checkpointSize = res.CHECKPOINT_SIZE;

        if ((x > (float)goalGridX && x < (float)(goalGridX + checkpointSize)) && 
                (y > (float)goalGridY && y < (float)(goalGridY + checkpointSize)))
            return true;

        return false;
    }

    public void setOffset(float x) {
        offsetX = -x;
    }

    public void draw(Graphics g) {
        var g2d = (Graphics2D)g;
        g2d.drawImage(image, (int)offsetX, 0, null);
    }
}
