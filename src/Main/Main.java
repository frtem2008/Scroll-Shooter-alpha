package Main;
//основной игровой класс
//TODO диагональная скорость, поспать, боты, пушки)))))

import Control.Keyboard;
import Control.Mouse;
import Drawing.Drawer;
import gameObjects.Game;
import gameObjects.Player;
import gameObjects.Wall;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
    //миникарта
    private static int miniMapX = 10, miniMapY = 50, miniMapSize = 128;
    private static double miniMapScale = 1.0;
    //изображения
    private static Image Wall, Player, Bot, Bullet, MapImage, Background;
    private static Game game = new Game();
    private static BufferedImage miniMapImage;
    private final Keyboard keyboard = new Keyboard();
    //клавиатура + мышь
    public Mouse mouse = new Mouse();

    //инициализация миникарты (замена цветов)
    private BufferedImage initMiniMap(BufferedImage map) {
        BufferedImage miniMap = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                if (map.getRGB(i, j) == new Color(0, 0, 255).getRGB() ||
                        map.getRGB(i, j) == new Color(255, 255, 255).getRGB() ||
                        map.getRGB(i, j) == new Color(255, 0, 0).getRGB()) {
                    miniMap.setRGB(i, j, new Color(0, 0, 0, 128).getRGB());
                } else {
                    miniMap.setRGB(i, j, map.getRGB(i, j));
                }
            }
        }

        return miniMap;
    }

    //отрисовка миникарты
    private void drawMiniMap(Graphics g, final BufferedImage miniMapImage, int playerX, int playerY) {
        for (int i = 0; i < miniMapImage.getWidth(); i++)
            for (int j = 0; j < miniMapImage.getHeight(); j++)
                if (miniMapImage.getRGB(i, j) == new Color(0, 255, 0).getRGB())
                    miniMapImage.setRGB(i, j, new Color(0, 0, 0, 128).getRGB());

        miniMapImage.setRGB(playerX / cellSize, playerY / cellSize, new Color(0, 255, 0).getRGB());
        miniMapImage.setRGB(0, 0, new Color(0, 0, 0).getRGB());

        g.drawImage(miniMapImage, miniMapX, miniMapY, (int) (miniMapSize * miniMapScale), (int) (miniMapSize * miniMapScale), null);
    }

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

            //размер JFrame на самом деле
            Dimension frameSize = frame.getContentPane().getSize();


            //главный игровой цикл
            while (true) {
                //время начала кадра
                start = System.currentTimeMillis();

                //обновление размера JFrame
                frameSize = frame.getContentPane().getSize();
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
                //отрисовка миникарты
                drawMiniMap(frameImage.getGraphics(), miniMapImage, mainPlayerX, mainPlayerY);

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
                        miniMapY = 40;
                    } else {
                        cameraX = Display.w / 1.2;
                        frame.setUndecorated(true);
                        frame.setExtendedState(6);
                        miniMapY = 20;
                    }
                    Display.isFullScreen = !Display.isFullScreen;
                    frame.setVisible(true);
                }

                //код для выхода из игры
                if (Keyboard.getQ()) {
                    System.out.println("Выход");
                    System.exit(20);
                }

                //перезагрузка игры
                if (Keyboard.getR()) {
                    System.out.println("Reloading...");
                    reload();
                    System.out.println("Reloading finished");
                }

                //карта на полный экран
                if (Keyboard.getM()) {
                    miniMapScale = (frameSize.getHeight() - 20) / (double) miniMapSize;
                    miniMapX = (int) (frameSize.getWidth() / 2 - miniMapSize * miniMapScale / 2);
                } else {
                    miniMapScale = 1.0;
                    miniMapX = 20;
                }

                //увеличение миникарты
                if (Keyboard.getCtrl() && Keyboard.getShift() && Keyboard.getEquals()) {
                    while (Keyboard.getCtrl() && Keyboard.getShift() && Keyboard.getEquals()) {
                        keyboard.update();
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (miniMapSize < 256)
                        miniMapSize += 64;
                }
                //уменьшение миникарты
                if (Keyboard.getCtrl() && Keyboard.getShift() && Keyboard.getMinus()) {
                    while (Keyboard.getCtrl() && Keyboard.getShift() && Keyboard.getMinus()) {
                        keyboard.update();
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (miniMapSize > 128)
                        miniMapSize -= 64;
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
        try {
            Player = ImageIO.read(new File("src/Resources/Images/bot64.png"));
            Bot = ImageIO.read(new File("src/Resources/Images/player evel64.png"));
            Wall = ImageIO.read(new File("src/Resources/Images/iron block64.png"));
            Bullet = ImageIO.read(new File("src/Resources/Images/bullet.jpg"));
            MapImage = ImageIO.read(new File("src/Resources/Images/Map2.png"));
            Background = ImageIO.read(new File("src/Resources/Images/background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //перезагрузка игры
    public void reload() {
        loadImages();
        game = initGame(MapImage);
        miniMapImage = initMiniMap((BufferedImage) MapImage);
    }
}
