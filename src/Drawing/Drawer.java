package Drawing;

import java.awt.*;

public class Drawer {
    public Drawer() {
    }

    public void drawGame(Game toDraw, Graphics g, int xLayout, int yLayout) {
        for (int i = 0; i < toDraw.bullets.size(); i++) {
            toDraw.bullets.get(i).draw(g, xLayout, yLayout);
        }
        for (int i = 0; i < toDraw.players.size(); i++) {
            toDraw.players.get(i).draw(g, xLayout, yLayout);
        }
        for (int i = 0; i < toDraw.walls.size(); i++) {
            toDraw.walls.get(i).draw(g, xLayout, yLayout);
        }
    }
}
