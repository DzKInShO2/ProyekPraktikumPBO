import java.awt.*;

public abstract class Entity {
    public float posX;
    public float posY;

    protected Level level;

    public Entity(Level level, float x, float y) {
        this.level = level;

        posX = x;
        posY = y;
    }

    public abstract void update(float dt);
    public abstract void draw(Graphics g);
}
