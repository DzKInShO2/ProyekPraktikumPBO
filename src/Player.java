import java.awt.*;

public class Player extends Entity {
    private int health;

    private float velocityY;
    private float velocityX;
    private final float GRAVITY = 9.8f;
    private final float FRICTION = 2.0f;

    private final float SPEED = 10.0f;
    private final float JUMP_FORCE = 7.0f;
    private final float MAX_VELOCITY_X = SPEED * 50;

    private final int CAMERA_OFFSET = 300;

    public Player(Level level, int x, int y) {
        super(level, x, y);

        velocityY = 0;
        velocityX = 0;
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

    public void update(float dt) {
        // Pengumpulan informasi yang dibutuhkan untuk membuat karakter
        // bergerak secara konsisten
        var res = ResourceManager.getInstance();
        var input = InputManager.getInstance();

        var playerToTileRatio =  res.PLAYER_SIZE / res.TILE_SIZE;

        var playerSize = res.PLAYER_SIZE * res.TILE_TO_SCREEN;
        var tileSize = res.TILE_SIZE * res.TILE_TO_SCREEN;

        // Gerakan di sumbu-Y
        var nextGridY = gridY + playerToTileRatio;
        var isOnAir = level.isTileEmpty(gridX, nextGridY);
        if (isOnAir) {
            velocityY -= GRAVITY * dt * tileSize;
            System.out.printf("\033[s\033[u");
        } else {
            velocityY = 0;

            // Entah kenapa harus nge-print sesuatu disini supaya ndak
            // ngelag setelah lompat
            System.out.printf("\033[s\033[u");
        }

        if (input.getJump()) {
            if (!isOnAir) {
                velocityY = tileSize * JUMP_FORCE;
            }
            input.resetJump();
        }

        posY -= velocityY * dt;
        gridY = (int)(posY / tileSize);

        // Gerakan di sumbu-X
        var directionX = input.getMoveRight() ? 1 : (input.getMoveLeft() ? -1 : 0);
        if (directionX != 0) {
            velocityX += SPEED * tileSize;
        }

        if (velocityX > MAX_VELOCITY_X) {
            velocityX = MAX_VELOCITY_X;
        } else if (velocityX < 0) {
            velocityX = 0;
        }

        var nextGridX = gridX + (directionX * playerToTileRatio);
        if (nextGridX < 0) nextGridX = 0;

        var isNextTileEmpty = true;
        for (var i = 0; i <= playerToTileRatio; i++) {
            isNextTileEmpty &= level.isTileEmpty(nextGridX, gridY - i);
        }

        if (isNextTileEmpty) {
            posX += velocityX * directionX * dt;
            if (posX < 0) posX = 0;

            gridX = (int)Math.round(posX / tileSize);

            level.setOffset(posX - CAMERA_OFFSET);
        }
    }

    public void draw(Graphics g) {
        var res = ResourceManager.getInstance();
        var player = res.getPlayer(res.PLAYER_IDLE);

        var playerSize = res.PLAYER_SIZE * res.TILE_TO_SCREEN;
        g.drawImage(player, 
                CAMERA_OFFSET, (int)posY, 
                playerSize, playerSize,
                null);
    }
}
