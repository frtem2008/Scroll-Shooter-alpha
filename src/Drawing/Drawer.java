package Drawing;
//Класс для рисования игры

import Utils.Vector2D;

import java.awt.*;

public class Drawer {
    //хитбоксы
    private static final boolean showHitboxes = false;

    public Drawer() {
    }

    //рисование игры
    //@param xLayout, yLayout - смещение относительно верхнего левого угла экрана
    // (чтобы игрок был в центре)
    //@param RENDERDISTANCE в клетках, чтобы рисовать только то, что видит игрок
    public void drawGame(Game toDraw, Graphics g, int xLayout, int yLayout, int playerX, int playerY, int RENDERDISTANCE) {
        //отрисовка пуль
        for (int i = 0; i < toDraw.bullets.size(); i++) {
            if (new Vector2D(toDraw.bullets.get(i).cords.x - playerX, toDraw.bullets.get(i).cords.y - playerY).length() < RENDERDISTANCE) {
                toDraw.bullets.get(i).draw(g, xLayout, yLayout);
                if (showHitboxes)
                    toDraw.bullets.get(i).drawHitbox(g, xLayout, yLayout, Color.GREEN);
            }
        }
        //отрисовка игроков и ботов
        for (int i = 0; i < toDraw.players.size(); i++) {
            if (new Vector2D(toDraw.players.get(i).cords.x - playerX, toDraw.players.get(i).cords.y - playerY).length() < RENDERDISTANCE) {
                toDraw.players.get(i).draw(g, xLayout, yLayout);
                if (showHitboxes)
                    if (!toDraw.players.get(i).isBot)
                        toDraw.players.get(i).drawHitbox(g, xLayout, yLayout, Color.BLUE);
                    else
                        toDraw.players.get(i).drawHitbox(g, xLayout, yLayout, Color.CYAN);
            }
        }
        //отрисовка стен
        for (int i = 0; i < toDraw.walls.size(); i++) {
            if (new Vector2D(toDraw.walls.get(i).cords.x - playerX, toDraw.walls.get(i).cords.y - playerY).length() < RENDERDISTANCE) {
                toDraw.walls.get(i).draw(g, xLayout, yLayout);
                if (showHitboxes)
                    toDraw.walls.get(i).drawHitbox(g, xLayout, yLayout, Color.RED);
            }
        }
    }
}
