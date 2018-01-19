package level.tile;

import graphics.Screen;
import graphics.Sprite;

public class VoidTile extends Tile {

    public VoidTile(Sprite sprite) {
        super(sprite);
    }

    public void render(int x, int y, Screen screen) {
        screen.renderTile(x << 6, y << 6, this);
    }

    public boolean solid() {
        return false;
    }
}
