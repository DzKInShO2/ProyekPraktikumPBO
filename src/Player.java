import java.awt.*;

public class Player extends Entity {
    private int health;

    private float velocityY;
    private final float GRAVITY = 9.8f;

    private final float SPEED = 10.0f;
    private final float JUMP_FORCE = 7.0f;

    public Player(int x, int y) {
        super(x, y);

        velocityY = 0;
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
        // Pengumpulan informasi yang dibutuhkan untuk membuat karakter
        // bergerak secara konsisten
        var res = ResourceManager.getInstance();
        var input = InputManager.getInstance();

        var playerToTileRatio =  res.PLAYER_SIZE / res.TILE_SIZE;

        var playerSize = res.PLAYER_SIZE * res.TILE_TO_SCREEN;
        var tileSize = res.TILE_SIZE * res.TILE_TO_SCREEN;

        // Gerakan di sumbu-Y
        var isOnAir = level.isTileEmpty(gridX, gridY + playerToTileRatio);
        if (isOnAir) {
            velocityY -= GRAVITY * dt * tileSize;
        } else {
            velocityY = 0;

            // Entah kenapa harus nge-print sesuatu disini supaya ndak
            // ngelag setelah lompat
            System.out.printf("\033[s\033[u", gridY);
        }

        if (!isOnAir && input.getJump()) {
            velocityY = tileSize * JUMP_FORCE;
        }

        posY -= velocityY * dt;
        gridY = (int)(posY / tileSize);

        // Gerakan di sumbu-X
        var directionX = input.getMoveRight() ? 1 : (input.getMoveLeft() ? -1 : 0);
        if (directionX != 0) {
            var isNextTileEmpty = level.isTileEmpty(gridX, gridY);
            if (isNextTileEmpty) {
                var velocityX = directionX * SPEED * tileSize * dt;

                posX += velocityX;
                gridX = (int)(posX / tileSize);
            }
        }
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
