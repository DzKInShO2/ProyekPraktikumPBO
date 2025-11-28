import java.awt.*;

public class Player extends Entity {
    private int health;

    public Player(int x, int y) {
        super(x, y);
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        var healthNew = health - damage;
        if (healthNew >= 0) {
            health = healthNew;
        }
    }

    public void update(float dt, Level level) {
        var res = ResourceManager.getInstance();
        var input = InputManager.getInstance();

        var playerToTileRatio =  res.PLAYER_SIZE / res.TILE_SIZE;

        var playerSize = res.PLAYER_SIZE * res.TILE_TO_SCREEN;
        var tileSize = res.TILE_SIZE * res.TILE_TO_SCREEN;

        var dirX = input.getMoveRight() ? 1 : (input.getMoveLeft() ? -1 : 0);

        var newPosX = posX + dirX * tileSize * 5 * dt;
        var newGridX = (int)(newPosX / tileSize);
        if (!level.isTileEmpty(newGridX + (playerToTileRatio * dirX), gridY)) {
            newPosX = posX;
            newGridX = gridX;
        } 

        posX = newPosX;
        gridX = newGridX;

        var newPosY = posY + tileSize * 9.8f * dt;
        if (input.getJump()) {
            newPosY -= tileSize;
        }

        var newGridY = (int)(newPosY / tileSize);
        if (!level.isTileEmpty(gridX, newGridY + playerToTileRatio)) {
            newPosY = posY;
            newGridY = gridY;
        } 

        posY = newPosY;
        gridY = newGridY;
    }

    public void draw(Graphics g) {
        var res = ResourceManager.getInstance();
        var player = res.getPlayer(res.PLAYER_IDLE);

        var playerSize = res.PLAYER_SIZE * res.TILE_TO_SCREEN;
        g.drawImage(player, 
                (int)posX, (int)posY, 
                playerSize, playerSize,
                null);
    }
}
