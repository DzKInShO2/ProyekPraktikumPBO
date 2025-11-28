import java.awt.*;

public abstract class Entity {
    public float posX;
    public float posY;
    public int gridX;
    public int gridY;

    public Entity(int x, int y) {
        gridX = x;
        gridY = y;
        posX = x;
        posY = y;
    }

    public abstract void update(float dt, Level level);
    public abstract void draw(Graphics g);
}
