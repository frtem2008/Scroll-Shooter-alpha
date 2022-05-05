package gameObjects;

import utils.GameObject;
import utils.Vector2D;

import java.awt.*;

public class Bullet extends GameObject {
    public Vector2D speed = Vector2D.zeroVector;

    public Bullet(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    public Bullet(double x, double y, double w, double h, Image texture) {
        super(x, y, w, h, texture);
    }

    public Bullet(double x, double y, double w, double h, double xSpeed, double ySpeed) {
        super(x, y, w, h);
        this.speed.x = xSpeed;
        this.speed.y = ySpeed;
    }

    public Bullet(double x, double y, double w, double h, double xSpeed, double ySpeed, Image texture) {
        super(x, y, w, h, texture);
        this.speed.x = xSpeed;
        this.speed.y = ySpeed;
    }

    public void move() {
        cords.x += speed.x;
        cords.y += speed.y;
    }

}