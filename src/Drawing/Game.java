package Drawing;

import gameObjects.Bullet;
import gameObjects.Player;
import gameObjects.Wall;

import java.util.ArrayList;

public class Game {
    public Game() {
        walls = new ArrayList<>();
        players = new ArrayList<>();
        bullets = new ArrayList<>();
    }

    public ArrayList<Wall> walls;
    public ArrayList<Player> players;
    public ArrayList<Bullet> bullets;

    public void tick(double tpf) {
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).move();
        }
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).isBot) {
                players.get(i).move();
            }
        }
    }
}
