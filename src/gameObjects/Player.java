package gameObjects;
//игрок и бот

import Control.Keyboard;
import Control.Mouse;
import Drawing.Game;
import Main.Main;
import Utils.GameObject;
import Utils.Vector2D;

import java.awt.*;
import java.util.ArrayList;

public class Player extends GameObject {

    public boolean isBot = false;

    public Vector2D speed; // скорость игрока по x / y
    public Vector2D maxSpeed = new Vector2D(Main.cellSize / 10.6, Main.cellSize / 10.6); //10.6 - важное число, лучше не менять

    public Player(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    public Player(double x, double y, double w, double h, Image texture, boolean isBot) {
        super(x, y, w, h, texture);
        this.isBot = isBot;
    }

    //движение игрока
    public void move(Game game) {
        //оптимизация столкновений
        //прямоугольник вокруг игрока
        Rectangle collisionArea = new Rectangle((int) (cords.x - maxSpeed.x),
                (int) (cords.y - maxSpeed.y),
                (int) (cords.x + maxSpeed.x),
                (int) (cords.y + maxSpeed.y));

        //отладочный вывод координат
        if (Mouse.mouseClicked) {
            System.out.println("Player X: " + (hitbox.x) + ", Player Y: " + (hitbox.y));
        }

        //изменение скорости
        if ((Keyboard.getA() && Keyboard.getD())
                || (!Keyboard.getA() && !Keyboard.getD())) {
            speed.x *= 0.8;
        }
        if (Keyboard.getA() && !Keyboard.getD()) {
            speed.x--;
        }
        if (!Keyboard.getA() && Keyboard.getD()) {
            speed.x++;
        }

        if ((Keyboard.getW() && Keyboard.getS())
                || (!Keyboard.getW() && !Keyboard.getS())) {
            speed.y *= 0.8;
        }
        if (Keyboard.getW() && !Keyboard.getS()) {
            speed.y--;
        }
        if (!Keyboard.getW() && Keyboard.getS()) {
            speed.y++;
        }

        //ограничение максивмальной скорости
        if (speed.x > 0 && speed.x < 0.75) {
            speed.x = 0;
        }
        if (speed.x < 0 && speed.x > -0.75) {
            speed.x = 0;
        }
        if (speed.x > maxSpeed.x) {
            speed.x = maxSpeed.x;
        }
        if (speed.x < -maxSpeed.x) {
            speed.x = -maxSpeed.x;
        }

        if (speed.y > 0 && speed.y < 0.75) {
            speed.y = 0;
        }
        if (speed.y < 0 && speed.y > -0.75) {
            speed.y = 0;
        }
        if (speed.y > maxSpeed.y) {
            speed.y = maxSpeed.y;
        }
        if (speed.y < -maxSpeed.y) {
            speed.y = -maxSpeed.y;
        }

        //горизонтальные столкновения
        hitbox.x += speed.x;
        for (int i = 0; i < game.walls.size(); i++) {
            //проверка на то, можно ли вообще столкнуться
            if (game.walls.get(i).cords.y > collisionArea.y + collisionArea.height &&
                    game.walls.get(i).cords.x > collisionArea.x + collisionArea.width) {
                break;
            }

            if (game.walls.get(i).cords.y < collisionArea.y - collisionArea.height &&
                    game.walls.get(i).cords.x < collisionArea.x - collisionArea.width)
                continue;

            if (game.walls.get(i).hitbox.intersects(hitbox)) {
                hitbox.x -= speed.x;
                while (!game.walls.get(i).hitbox.intersects(hitbox)) {
                    hitbox.x += Math.signum(speed.x);
                }
                hitbox.x -= Math.signum(speed.x);
                speed.x = 0;
                cords.x = hitbox.x;
            }
        }
        //вертикальные столкновения
        hitbox.y += speed.y;
        for (int i = 0; i < game.walls.size(); i++) {
            //проверка на то, можно ли вообще столкнуться
            if (game.walls.get(i).cords.y > collisionArea.y + collisionArea.height &&
                    game.walls.get(i).cords.x > collisionArea.x + collisionArea.width)
                break;

            if (game.walls.get(i).cords.y < collisionArea.y - collisionArea.height &&
                    game.walls.get(i).cords.x < collisionArea.x - collisionArea.width)
                continue;

            if (game.walls.get(i).hitbox.intersects(hitbox)) {
                hitbox.y -= speed.y;
                while (!game.walls.get(i).hitbox.intersects(hitbox)) {
                    hitbox.y += Math.signum(speed.y);
                }
                hitbox.y -= Math.signum(speed.y);
                speed.y = 0;
                cords.y = hitbox.y;
            }
        }
        //изменение координат относительно скорости
        cords.x += speed.x;
        cords.y += speed.y;

        //перемещение хитбокса
        hitbox.x = (int) cords.x;
        hitbox.y = (int) cords.y;
    }

    //TODO перемещение бота В РАЗРАБОТКЕ
    public int moveBot(String[] strMap, int playerX, int playerY) {
        if (isBot) {
            Vector2D from = Vector2D.infVector, to = Vector2D.infVector;
            boolean[][] map = new boolean[strMap.length][strMap[0].length()];
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    map[i][j] = strMap[i].charAt(j) == '#';
                }
            }
            from = new Vector2D((float) playerX / Main.cellSize, (float) playerY / Main.cellSize);
            to = new Vector2D(cords.y / Main.cellSize, cords.x / Main.cellSize);
            System.out.println("FIND path from (" + from.x + ", " + from.y + ") to (" + to.x + ", " + to.y + ")!!!");
            ArrayList<Vector2D> path;
            //TODO исправить бота он идет направо все время 
            if (from != Vector2D.infVector && to != Vector2D.infVector) {
                path = Utils.LeeAlgorithm.findPath(map, to, from);
                if (path != null) {
                    for (Vector2D ij : path) {
                        if (ij != null)
                            System.out.print(ij.x + " " + ij.y + " -> ");
                        else {
                            System.out.println("NULL PATH VECTOR");
                            return 5;
                        }
                    }

                    System.out.println("FOUND path from (" + from.x + ", " + from.y + ") to (" + to.x + ", " + to.y + ")!!!");
                    if (path.size() > 2) {
                        if (path.get(0).y < path.get(1).y) {
                            cords.x += Main.cellSize;
                            System.out.println("Moving right");
                        } else if (path.get(0).y > path.get(1).y) {
                            cords.x -= Main.cellSize;
                            System.out.println("Moving left");
                        }
                        if (path.get(0).x > path.get(1).x) {
                            cords.y -= Main.cellSize;
                            System.out.println("Moving up");
                        } else if (path.get(0).x < path.get(1).x) {
                            cords.y += Main.cellSize;
                            System.out.println("Moving down");
                        }

                        System.out.println("BOT X: " + cords.x + " BOT Y: " + cords.y);
                        return 1;
                    } else {
                        System.out.println("No path from (" + from.x + ", " + from.y + ") to (" + to.x + ", " + to.y + ")!!!");
                        return 0;
                    }
                } else {
                    System.out.println("No path from (" + from.x + ", " + from.y + ") to (" + to.x + ", " + to.y + ")!!!");
                    return 2;
                }
            } else {
                return 3;
            }
        } else {
            System.out.println("KANOSENBV EIB");
            return 4;
        }
    }
}
