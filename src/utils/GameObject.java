package utils;

import java.awt.*;

public abstract class GameObject {
    public Vector2D cords = new Vector2D(0, 0), size = new Vector2D(0, 0);
    public Image texture;

    public GameObject(double x, double y, double w, double h) {
        this.cords.x = x;
        this.cords.y = y;
        this.size.x = w;
        this.size.y = h;
    }

    public GameObject(double x, double y, double w, double h, Image texture) {
        this.cords.x = x;
        this.cords.y = y;
        this.size.x = w;
        this.size.y = h;
        this.texture = texture;
    }

    public void draw(Graphics g, int xLayout, int yLayout) {
        g.drawImage(texture, (int) cords.x + xLayout, (int) cords.y + yLayout, (int) size.x, (int) size.y, null);
    }
}
