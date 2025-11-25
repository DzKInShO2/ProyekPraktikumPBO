import java.awt.*;

public class ResourceManager {
    private ResourceManager instance;

    private Image tilemap;

    private ResourceManager() {
        tilemap = new ImageIcon("res/PixelAdventure1Free/Terrain/Terrain (16x16).png").getImage();
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    public Image getTilemap() {
        return tilemap;
    }
}
