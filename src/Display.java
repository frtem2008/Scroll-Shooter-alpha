//Класс с окном

//TODO анимация, миникарта, боты, онлайн

import javax.swing.*;

public class Display extends JFrame {
    //окно
    public static JFrame frame = new JFrame("Scroll shooter livefish alfa");

    //дефолтные размеры окна
    public static int x = 300, y = 0, w = 1000, h = 800;

    //отслеживание полноэкранного режима
    public static boolean isFullScreen = false;

    //подключение графических библиотек
    static {
        System.setProperty("sun.java2d.opengl", "True");
    }

    //точка входа
    public static void main(String[] args) throws InterruptedException {
        //иконка для панели задач
        frame.setIconImage(new ImageIcon("src/resources/images/Icon.png").getImage());

        //активация кнопки с крестиком
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //разворачиваем сразу на полный экран
        frame.setBounds(300, 0, w, h);
        frame.setVisible(true);

        //создаём новый экземпляр игры и начинаем рисовать
        Main m = new Main();
        m.startDrawing(frame);
    }
}
