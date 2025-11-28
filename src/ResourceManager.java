import java.io.*;
import java.awt.*;
import java.awt.image.*;

import javax.swing.*;
import javax.imageio.*;

public class ResourceManager {
    public final int PLAYER_SIZE = 32;
    public final int TILE_SIZE = 16;
    public final int TILE_TO_SCREEN = 4;

    public final int PLAYER_IDLE = 0;

    private static ResourceManager instance;

    private Image[] tiles;
    private Image[] player;

    private int tileCountX;
    private int tileCountY;

    private ResourceManager() {
        try {
            var tileset = ImageIO.read(new File("res/PixelAdventure1Free/Terrain/Terrain (16x16).png"));
            tileCountX = tileset.getWidth() / TILE_SIZE;
            tileCountY = tileset.getHeight() / TILE_SIZE;

            tiles = new Image[tileCountX * tileCountY];

            var i = 0;
            for (var y = 0; y < tileCountY; y++) {
                for (var x = 0; x < tileCountX; x++) {
                    var sub = tileset.getSubimage(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    tiles[i++] = (Image)sub;
                }
            }
            player = new Image[1];

            var playerIdle = ImageIO.read(new File("res/PixelAdventure1Free/Main Characters/Virtual Guy/Idle (32x32).png"))
                .getSubimage(0, 0, PLAYER_SIZE, PLAYER_SIZE);

            player[PLAYER_IDLE] = playerIdle;
        } catch (Exception e) {}

    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    public Image getTile(int i) {
        return tiles[i];
    }

    public int getTileCountX() {
        return tileCountX;
    }

    public int getTileCountY() {
        return tileCountY;
    }

    public Image getPlayer(int mode) {
        return player[mode];
    }
}
