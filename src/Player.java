import java.awt.*;
import javax.sound.sampled.*;

public class Player extends Entity {

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

    // Timer untuk langkah kaki
    private float walkTimer = 0f;
    private final float WALK_INTERVAL = 0.25f;

    public Player(Level level, Finished finished, float x, float y) {
        super(level, x, y);

        velocityX = 0;
        velocityY = 0;
        isOnGround = false;
        isFlipped = false;

        var res = ResourceManager.getInstance();
        animClip = res.getPlayer(res.PLAYER_IDLE);

        this.finished = finished;
    }

    public void update(float dt) {
        var res = ResourceManager.getInstance();

        var tileSize = res.TILE_SIZE * res.TILE_TO_SCREEN;
        var pttr =  res.PLAYER_SIZE / res.TILE_SIZE;

        var input = InputManager.getInstance();
        var direction = input.getMoveRight() ? 1 : (input.getMoveLeft() ? -1 : 0);

        // Flip berdasarkan arah karakter
        if (direction != 0) {
            isFlipped = direction < 0;
        }

        // Jump
        if (isOnGround && input.getJump()) {
            velocityY += JUMP_FORCE;
            SoundManager.playOnce(res.getJumpClip());
            input.resetJump();
        }

        // Physics
        velocityY -= GRAVITY * dt;
        velocityX = direction * SPEED;

        // ANIMASI + SFX FOOTSTEP
        if (isOnGround) {
            if (velocityX != 0) {
                var runClip = res.getPlayer(res.PLAYER_RUN);
                if (animClip != runClip) {
                    runClip.reset();
                    animClip = runClip;
                    walkTimer = 0; // reset timer saat ganti animasi
                }

                // Footstep SFX setiap WALK_INTERVAL detik
                walkTimer += dt;
                if (walkTimer >= WALK_INTERVAL) {
                    walkTimer -= WALK_INTERVAL; 
                    SoundManager.playWalkSound(res.getWalkClip());
                }

            } else {
                // STOP walk sound saat berhenti
                SoundManager.stopWalkSound();
                
                var idleClip = res.getPlayer(res.PLAYER_IDLE);
                if (animClip != idleClip) {
                    idleClip.reset();
                    animClip = idleClip;
                }
                walkTimer = 0; 
            }
        } 
        else { 
            // STOP walk sound saat di udara
            SoundManager.stopWalkSound();
            walkTimer = 0;

            if (velocityY > 0) {
                animClip = res.getPlayer(res.PLAYER_JUMP);
            } else {
                animClip = res.getPlayer(res.PLAYER_FALL);
            }
        }

        // Collision + movement
        var newPosX = posX + velocityX * dt;
        var newPosY = posY - velocityY * dt;

        // Collision horizontal
        if (velocityX < 0) { 
            if (level.getTile(newPosX, posY) > 0 || level.getTile(newPosX, posY + 0.9f) > 0) {
                newPosX = (int)newPosX + 1;
                velocityX = 0;
            }
        } else if (velocityX > 0) { 
            float rightEdge = newPosX + pttr;
            int tileRight = (int)rightEdge;
            
            if (level.getTile(tileRight, (int)posY) > 0 || 
                level.getTile(tileRight, (int)(posY + 0.9f)) > 0) {
                newPosX = tileRight - pttr;
                velocityX = 0;
            }
        }

        // Collision vertical - perbaiki agar tidak menyebabkan slowdown setelah jump
        boolean wasOnGround = isOnGround;
        isOnGround = false; // reset dulu
        
        if (velocityY <= 0) { // Jatuh atau di ground
            // Cek collision di bawah
            float bottomY = newPosY + pttr;
            int tileBottom = (int)bottomY;
            
            if (level.getTile((int)(newPosX + 0.1f), tileBottom) > 0 ||
                level.getTile((int)(newPosX + pttr - 0.1f), tileBottom) > 0) {
                newPosY = tileBottom - pttr;
                velocityY = 0;
                isOnGround = true;
            }
        } else { // Naik (jump)
            // Cek collision di atas
            int tileTop = (int)newPosY;
            
            if (level.getTile((int)(newPosX + 0.1f), tileTop) > 0 ||
                level.getTile((int)(newPosX + pttr - 0.1f), tileTop) > 0) {
                newPosY = tileTop + 1;
                velocityY = 0;
            }
        }

        posX = newPosX;
        posY = newPosY;

        // Reset jika jatuh
        if (posY > level.getHeight()) {
            posX = posY = 0;
            velocityX = velocityY = 0;
        }

        // Checkpoint
        if (level.isInCheckpoint(posX, posY)) {
            finished.finished(0);
        }

        level.setOffset((posX * tileSize) - CAMERA_OFFSET);
        animClip.update(dt);
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
