import java.awt.*;

public abstract class Entity {
    public float posX;
    public float posY;
    public int gridX;
    public int gridY;

    public Entity(int x, int y) {
        gridX = x;
        gridY = y;

        var res = ResourceManager.getInstance();
        var tileSize = res.TILE_SIZE * res.TILE_TO_SCREEN;

        posX = x * tileSize;
        posY = y * tileSize;
    }

    public abstract void update(float dt, Level level);
    public abstract void draw(Graphics g);
}
