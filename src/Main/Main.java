package Main;
//основной игровой класс
//TODO диагональная скорость, поспать, боты, пушки)))))

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
    //менять для масштаба
    private static final int defaultCellSize = 64;
    //TODO тики в кадр
    private static final int TICKSPERFRAME = 20;
    //менять для прогрузки (чем больше, тем больше лаги, но на больших мониторах дальше видно)
    private static final int RENDERDISTANCE = 16;
    //рисовалка и игра
    private static final Drawer drawer = new Drawer();
    //размер клеток
    public static int cellSize = defaultCellSize;
    //пока ненужная карта (нужна для бота)
    private static String[] map;
    //коодинаты игрока, который находится в центре экрана
    private static int mainPlayerX, mainPlayerY;
    //смещение камеры (положения игрока) относительно левого верхнего угла экрана
    private static double cameraX = 500;
    private static final double cameraY = 400;
    //изображения
    private static Image Wall, Player, Bot, Bullet, MapImage, Background;
    private static Game game = new Game();
    private final Keyboard keyboard = new Keyboard();
    //клавиатура + мышь
    public Mouse mouse = new Mouse();

    private Game initGame(Image mapImage) {
        System.out.println("Initialising game...");
        Game res = new Game();

        //Изображение карты
        BufferedImage mapPixel = new BufferedImage(
                mapImage.getWidth(null),
                mapImage.getHeight(null),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D mapGraphics = mapPixel.createGraphics();
        mapGraphics.drawImage(mapImage, 0, 0, null);
        mapGraphics.dispose();

        for (int i = 0; i < mapPixel.getWidth(); i++) {
            for (int j = 0; j < mapPixel.getHeight(); j++) {
                if (mapPixel.getRGB(i, j) == new Color(0, 0, 0).getRGB()) {
                    res.walls.add(new Wall(i * cellSize, j * cellSize, cellSize, cellSize, Wall));
                } else if (mapPixel.getRGB(i, j) == new Color(255, 0, 0).getRGB()) {
                    res.players.add(new Player(i * cellSize, j * cellSize, cellSize, cellSize, Bot, true));
                } else if (mapPixel.getRGB(i, j) == new Color(0, 0, 255).getRGB()) {
                    res.players.add(new Player(i * cellSize, j * cellSize, cellSize, cellSize, Player, false));
                }
            }
        }
        //TODO слияние соседних стен в одну
        System.out.println("Players: " + res.players.size());
        System.out.println("Bullets: " + res.bullets.size());
        System.out.println("Walls: " + res.walls.size());
        System.out.println("Initialising game finished");
        return res;
    }

    //начало игры ()
    public void startDrawing(JFrame frame) {
        new Thread(() -> {
            //подгружаем изображения и прогружаем игру
            reload();

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

            //графика итогового окна
            Graphics2D frameGraphics;

            //длина кадра (число после дроби - фпс)
            frameLength = 1000.0 / 60;
            int frames = 0;

            //главный игровой цикл
            while (true) {
                //время начала кадра
                start = System.currentTimeMillis();

                //получение информации о буфере
                frameGraphics = (Graphics2D) bs.getDrawGraphics();

                //очистка экрана перед рисованием
                frameGraphics.clearRect(0, 0, frame.getWidth(), frame.getHeight());
                frameImage.getGraphics().clearRect(0, 0, frameImage.getWidth(), frameImage.getHeight());
                frameImage.getGraphics().drawImage(Background, 0, 0, null);
                //рисование на предварительном изображении
                drawer.drawGame(game, frameImage.getGraphics(),
                        (int) ((cameraX) - mainPlayerX),
                        (int) ((cameraY) - mainPlayerY),
                        mainPlayerX, mainPlayerY, RENDERDISTANCE * cellSize
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
                        cameraX = 500;
                    } else {
                        cameraX = Display.w / 1.2;
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
                //TODO перезагрузка игры
                // (по идее долждно обнавлять карту при новом изображении, но этого не происходит)
                if (Keyboard.getR()) {
                    System.out.println("Reloading...");
                    reload();
                    System.out.println("Reloading finished");
                }

                //получение координат игрока (центра кадра)
                for (int i = 0; i < game.players.size(); i++) {
                    if (!game.players.get(i).isBot) {
                        mainPlayerX = (int) game.players.get(i).cords.x;
                        mainPlayerY = (int) game.players.get(i).cords.y;
                    }
                }
                //обновления клавиатуры и игры
                game.tick(frames);
                keyboard.update();
                frames++;
            }
        }).start();
    }

    //функция загрузки изображений (путь к папке: src/Resources/Images/)
    public void loadImages() {
        Player = new ImageIcon("src/Resources/Images/bot64.png").getImage();
        Bot = new ImageIcon("src/Resources/Images/player evel64.png").getImage();
        Wall = new ImageIcon("src/Resources/Images/iron block64.png").getImage();
        Bullet = new ImageIcon("src/Resources/Images/bullet.jpg").getImage();
        MapImage = new ImageIcon("src/Resources/Images/Map.png").getImage();
        Background = new ImageIcon("src/Resources/Images/background.jpg").getImage();
    }

    //перезагрузка игры
    public void reload() {
        loadImages();
        game = initGame(MapImage);
    }
}
