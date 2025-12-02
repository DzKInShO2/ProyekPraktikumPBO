import java.awt.*;

import javax.sound.sampled.*;

public class Player extends Entity {
    private int health;

    private Clip walkClip;

    private boolean isOnGround;

    private float velocityX;
    private float velocityY;
    private final float GRAVITY = 9.8f;
    private final float FRICTION = 2.0f;

    private final float SPEED = 10.0f;
    private final float JUMP_FORCE = 7.0f;
    private final float MAX_VELOCITY_X = SPEED * 50;

    private final int CAMERA_OFFSET = 300;

    private Finished finished;

    public Player(Level level, Finished finished, int x, int y) {
        super(level, x, y);

        velocityX = 0;
        velocityY = 0;
        isOnGround = false;

        var res = ResourceManager.getInstance();
        walkClip = SoundManager.getLoop(res.getWalkClip());

        this.finished = finished;
    }

    public int getHealth() {
        return health;
    }

    public void update(float dt) {
        var res = ResourceManager.getInstance();

        var tileSize = res.TILE_SIZE * res.TILE_TO_SCREEN;
        var pttr =  res.PLAYER_SIZE / res.TILE_SIZE;

        var input = InputManager.getInstance();
        var direction = input.getMoveRight() ? 1 : (input.getMoveLeft() ? -1 : 0);

        if (isOnGround && input.getJump()) {
            velocityY += JUMP_FORCE;
            input.resetJump();
        }

        velocityY -= GRAVITY * dt;
        velocityX = direction * SPEED;

        var newPosX = posX + velocityX * dt;
        var newPosY = posY - velocityY * dt;
        if (velocityX <= 0) {
            if (level.getTile(newPosX, posY) > 0 || level.getTile(newPosX, posY + 0.9f) > 0) {
                newPosX = (int)newPosX;
                velocityX = 0;
            }
        } else {
            if (level.getTile(newPosX + pttr, posY) > 0 || level.getTile(newPosX + pttr, posY + 0.9f) > 0) {
                newPosX = (int)newPosX;
                velocityX = 0;
            }
        }

        if (velocityY <= 0) {
            if (level.getTile(newPosX, posY + pttr) > 0 || level.getTile(newPosX + pttr, posY + pttr) > 0) {
                newPosY = (int)newPosY;
                velocityY = 0;

                isOnGround = true;
            }
        } else {
            if (level.getTile(newPosX, posY) > 0 || level.getTile(newPosX + pttr, posY) > 0) {
                newPosY = (int)newPosY;
                velocityY = 0;
            }

            isOnGround = false;
        }

        posX = newPosX;
        posY = newPosY;

        if (level.isInCheckpoint(posX, posY)) {
            finished.finished(0);
        }

        level.setOffset((posX * tileSize) - CAMERA_OFFSET);
        System.out.printf("\033[s\033[u", velocityY);
    }

    public void draw(Graphics g) {
        var res = ResourceManager.getInstance();
        var player = res.getPlayer(res.PLAYER_IDLE);

        var playerSize = res.PLAYER_SIZE * res.TILE_TO_SCREEN;
        var tileSize = res.TILE_SIZE * res.TILE_TO_SCREEN;
        g.drawImage(player, 
                CAMERA_OFFSET, (int)(posY * tileSize),
                playerSize, playerSize,
                null);
    }
}
