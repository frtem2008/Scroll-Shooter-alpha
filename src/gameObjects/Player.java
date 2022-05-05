package gameObjects;

import Control.Keyboard;
import utils.GameObject;
import utils.Vector2D;

import java.awt.*;
import java.util.ArrayList;

public class Player extends GameObject {

    public boolean isBot = false;

    public Player(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    public Player(double x, double y, double w, double h, Image texture, boolean isBot) {
        super(x, y, w, h, texture);
        this.isBot = isBot;
    }


    public int move() {
        if (!isBot) {
            if (Keyboard.getA())
                cords.x -= 4;
            else if (Keyboard.getD())
                cords.x += 4;
            if (Keyboard.getW())
                cords.y -= 4;
            else if (Keyboard.getS())
                cords.y += 4;
            return 1;
        } else {
            if (Keyboard.getLeft())
                cords.x -= 2;
            else if (Keyboard.getRight())
                cords.x += 2;
            if (Keyboard.getUp())
                cords.y -= 2;
            else if (Keyboard.getDown())
                cords.y += 2;
            return 0;
        }
    }

    public int moveBot(String[] strMap) {
        if (isBot) {
            Vector2D from = Vector2D.infVector, to = Vector2D.infVector;
            boolean[][] map = new boolean[strMap.length][strMap[0].length()];
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    if (strMap[i].charAt(j) == '#') {
                        map[i][j] = true;
                    } else {
                        map[i][j] = false;
                        if (strMap[i].charAt(j) == '1')
                            to = new Vector2D(i, j);
                        if (strMap[i].charAt(j) == '2')
                            from = new Vector2D(i, j);
                    }
                }
            }

            if (from != Vector2D.infVector && to != Vector2D.infVector) {
                ArrayList<Vector2D> path = utils.LeeAlgorithm.findPath(map, to, from);
                //TODO сдвиг на path [0][0]
            }

            return 1;
        }
        return 0;
    }
}
