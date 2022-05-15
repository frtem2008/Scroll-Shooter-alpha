package gameObjects;
//класс для хранения игры

import Main.Main;

import java.util.ArrayList;

public class Game {
    //инициализация в конструкторе
    public Game() {
        walls = new ArrayList<>();
        players = new ArrayList<>();
        bullets = new ArrayList<>();
    }

    //данные
    public ArrayList<Wall> walls;
    public ArrayList<Player> players;
    public ArrayList<Bullet> bullets;

    /**
     * игровой тик
     *
     * @param frames нужен, чтобы двигать ботов раз в несколько кадров
     *               боты пока не двигаются
     */
    public void tick(double frames) {
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).move();
        }
        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).isBot) {
                players.get(i).move(this);
            } else if (frames % 15 == 0) {
                //TODO move bot
                players.get(i).moveBot(Main.botMap, Main.mainPlayerX, Main.mainPlayerY, (int) players.get(i).cords.x, (int) players.get(i).cords.y);
            }
        }
    }
}
