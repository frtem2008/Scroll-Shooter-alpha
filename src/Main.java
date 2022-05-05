//основной игровой класс

import Control.Keyboard;
import Control.Mouse;
import Drawing.Drawer;
import Drawing.Game;
import gameObjects.Player;
import gameObjects.Wall;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Main {
    //нажата ли мышь

    public static final String[] map = {
            "##############",
            "#1#####00000##",
            "#00000#000#0##",
            "##00#000##000#",
            "######0####0##",
            "##20000#######",
            "##############",
    };

    public static final int TICKSPERFRAME = 20;
    public static final int CELLSIZE = 128;
    public static int mainPlayerX, mainPlayerY;

    public static double cameraX;
    public static double cameraY;

    public static Image Wall, Player, Bot, Bullet;
    public static boolean showHitboxes = false;
    //сам холст
    JFrame frame;
    //клавиатура + мышь
    Keyboard keyboard = new Keyboard();
    Mouse mouse = new Mouse();
    //рисовалка и игра
    public static Game game = new Game();
    Drawer drawer = new Drawer();

    private Game initGame() {
        Game res = new Game();

        System.out.println(CELLSIZE);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length(); j++) {
                if (map[i].charAt(j) == '#')
                    res.walls.add(new Wall(j * CELLSIZE, i * CELLSIZE, CELLSIZE, CELLSIZE, Wall));
                else if (map[i].charAt(j) == '1') {
                    res.players.add(new Player(j * CELLSIZE, i * CELLSIZE, CELLSIZE, CELLSIZE, Player, false));
                    mainPlayerX = j * CELLSIZE;
                    mainPlayerY = i * CELLSIZE;
                    cameraX = mainPlayerX + Display.frame.getWidth() / 2.0;
                    cameraY = mainPlayerY + 200;
                } else if (map[i].charAt(j) == '2')
                    res.players.add(new Player(j * CELLSIZE, i * CELLSIZE, CELLSIZE, CELLSIZE, Bot, true));
            }
        }
        return res;
    }

    //начало игры ()
    public void startDrawing(JFrame frame) {
        //подгружаем изображения
        this.loadImages();

        //игра
        game = initGame();

        //создаём холст
        this.frame = frame;

        //привязываем слушатели
        frame.addKeyListener(keyboard);
        frame.addMouseListener(mouse);
        frame.addMouseMotionListener(mouse);


        //изображение для отрисовки (для изменения пикселей после рисования объектов)
        BufferedImage frameImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);

        //создание буфера
        frame.createBufferStrategy(2);
        BufferStrategy bs = frame.getBufferStrategy();

        //для использования tab, alt и т.д
        frame.setFocusTraversalKeysEnabled(false);

        //для стабилизации и ограничения фпс
        long start, end, len;
        double frameLength;

        //фрейм графика
        Graphics2D frameGraphics;

        //длина кадра (число после дроби - фпс)
        frameLength = 1000.0 / 60;


        //главный игровой цикл
        while (true) {
            //время начала кадра
            start = System.currentTimeMillis();

            //получение информации о буфере
            frameGraphics = (Graphics2D) bs.getDrawGraphics();

            //очистка экрана перед рисованием
            frameGraphics.clearRect(0, 0, frame.getWidth(), frame.getHeight());
            frameImage.getGraphics().clearRect(0, 0, frameImage.getWidth(), frameImage.getHeight());

            //рисование на предварительном изображении
            drawer.drawGame(game, frameImage.getGraphics(),
                    (int) ((cameraX) - mainPlayerX),
                    400 - mainPlayerY
            );
            //рисование на итоговом окне
            frameGraphics.drawImage(frameImage, 0, 0, frameImage.getWidth(), frameImage.getHeight(), null);

            //очистка мусора
            frameImage.getGraphics().dispose();
            frameGraphics.dispose();

            //показ буфера на холсте
            bs.show();

            //замер времени, ушедшего на отрисовку кадра
            end = System.currentTimeMillis();
            len = end - start;

            //стабилизация фпс
            if (len < frameLength) {
                try {
                    Thread.sleep((long) (frameLength - len));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //разворот на полный экран
            if (Keyboard.getF11()) {
                while (Keyboard.getF11()) {
                    keyboard.update();
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                frame.dispose();
                if (Display.isFullScreen) {
                    frame.setUndecorated(false);
                    frame.setExtendedState(Frame.NORMAL);
                    frame.setBounds(Display.x, Display.y, Display.w, Display.h);
                } else {
                    frame.setUndecorated(true);
                    frame.setExtendedState(6);
                }
                Display.isFullScreen = !Display.isFullScreen;
                frame.setVisible(true);
            }

            //код для выхода из игры
            if (Keyboard.getQ()) {
                System.out.println("Выход");
                System.exit(20);
            }

            game.tick(TICKSPERFRAME);

            for (int i = 0; i < game.players.size(); i++) {
                if (game.players.get(i).move() == 1) {
                    mainPlayerX = (int) game.players.get(i).cords.x;
                    mainPlayerY = (int) game.players.get(i).cords.y;
                }
            }
            //обновления клавиатуры и физики игрока
            keyboard.update();
        }

    }

    //функция загрузки изображений (путь к папке: src/resources/images/)
    public void loadImages() {
        Player = new ImageIcon("src/Resources/Images/bot64.png").getImage();
        Bot = new ImageIcon("src/Resources/Images/player evel64.png").getImage();
        Wall = new ImageIcon("src/Resources/Images/iron block64.png").getImage();
        Bullet = new ImageIcon("src/Resources/Images/bullet.jpg").getImage();
    }
}
