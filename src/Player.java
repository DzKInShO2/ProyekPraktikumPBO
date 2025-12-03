import java.awt.*;
import javax.sound.sampled.*;

public class Player extends Entity {
    private Clip walkClip;
    private AnimationClip animClip;

    private boolean isFlipped;
    private boolean isOnGround;

    private float velocityX;
    private float velocityY;

    private final float GRAVITY = 9.8f;
    private final float SPEED = 10.0f;
    private final float JUMP_FORCE = 7.0f;

    private final int CAMERA_OFFSET = 300;

    private Finished finished;

    public Player(Level level, Finished finished, float x, float y) {
        super(level, x, y);

        velocityX = 0;
        velocityY = 0;
        isOnGround = false;
        isFlipped = false;

        var res = ResourceManager.getInstance();
        animClip = res.getPlayer(res.PLAYER_IDLE);

        walkClip = SoundManager.getLoop(res.getWalkClip());

        this.finished = finished;
    }

    public void update(float dt) {
        var res = ResourceManager.getInstance();

        var tileSize = res.TILE_SIZE * res.TILE_TO_SCREEN;
        var pttr =  res.PLAYER_SIZE / res.TILE_SIZE;

        var input = InputManager.getInstance();
        var direction = input.getMoveRight() ? 1 : (input.getMoveLeft() ? -1 : 0);

        if (direction != 0) {
            isFlipped = direction < 0;
        }

        if (isOnGround && input.getJump()) {
            velocityY += JUMP_FORCE;
            SoundManager.playOnce(res.getJumpClip());
            input.resetJump();
        }

        velocityY -= GRAVITY * dt;
        velocityX = direction * SPEED;

        if (isOnGround) {
            if (velocityX != 0) {
                var runClip = res.getPlayer(res.PLAYER_RUN);
                if (animClip != runClip) {
                    runClip.reset();
                    animClip = runClip;
                }

                SoundManager.play(walkClip);
            } else {
                SoundManager.stop(walkClip);
                
                var idleClip = res.getPlayer(res.PLAYER_IDLE);
                if (animClip != idleClip) {
                    idleClip.reset();
                    animClip = idleClip;
                }
            }
        } 
        else { 
            SoundManager.stop(walkClip);

            if (velocityY > 0) {
                animClip = res.getPlayer(res.PLAYER_JUMP);
            } else {
                animClip = res.getPlayer(res.PLAYER_FALL);
            }
        }

        var newPosX = posX + velocityX * dt;
        var newPosY = posY - velocityY * dt;
        if (velocityX < 0) { 
            if (level.getTile(newPosX, posY) > 0 || level.getTile(newPosX, posY + 0.9f) > 0) {
                newPosX = (int)newPosX + 1;
                velocityX = 0;
            }
        } else if (velocityX > 0) { 
            if (level.getTile(newPosX + pttr, posY) > 0 || 
                level.getTile(newPosX + pttr, posY + 0.9f) > 0) {
                newPosX = (int)newPosX;
                velocityX = 0;
            }
        }
        
        if (velocityY <= 0) {
            if (level.getTile(newPosX + 0.2f, newPosY + pttr) > 0 ||
                level.getTile(newPosX + 1.8f, newPosY + pttr) > 0) {
                newPosY = (int)newPosY;
                velocityY = 0;
                isOnGround = true;
            }
        } else {
            if (level.getTile(newPosX + 0.1f, newPosY) > 0 ||
                level.getTile(newPosX + 1.8f, newPosY) > 0) {
                newPosY = newPosY + 1;
                velocityY = 0;
            }

            isOnGround = false;
        }

        posX = newPosX;
        posY = newPosY;

        if (posY > level.getHeight()) {
            posX = posY = 0;
            velocityX = velocityY = 0;
        }

        if (level.isInCheckpoint(posX, posY)) {
            finished.finished(0);
        }

        level.setOffset((posX * tileSize) - CAMERA_OFFSET);
        animClip.update(dt);
        System.out.printf("\033[s\033[u");
    }

    public void draw(Graphics g) {
        var res = ResourceManager.getInstance();
        var playerSize = res.PLAYER_SIZE * res.TILE_TO_SCREEN;
        var tileSize = res.TILE_SIZE * res.TILE_TO_SCREEN;

        if (isFlipped) {
            g.drawImage(animClip.getFrame(),
                CAMERA_OFFSET + playerSize, (int)(posY * tileSize),
                -playerSize, playerSize, null);
        } else {
            g.drawImage(animClip.getFrame(),
                CAMERA_OFFSET, (int)(posY * tileSize),
                playerSize, playerSize, null);
        }
    }
}
