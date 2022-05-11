package OnlinePart;//клиент админа и клиента

import java.io.IOException;
import java.util.Scanner;

public class Client {
    public static Phone phone;//сокет
    public static Scanner s = new Scanner(System.in);//считывание команд админа
    public static boolean A;//админ или клиент?
    public static int id;

    public static void main(String[] args) throws IOException {
        System.out.println("Admin?");
        A = s.nextBoolean();//считывание информации о компьютере(в будующем - 2 разных файла)
        System.out.println("Id?");
        id = s.nextInt();
        s.nextLine();
        if (A) {
            System.out.println("Logged as administrator");
        } else {
            System.out.println("Logged as client");
        }
        connect();//подключение к серверу
    }

    public static void readRequest() {
        new Thread(() -> {
            while (true) {
                String res = phone.readLine();
                if (!res.equals("-1")) {
                    System.out.println(res);
                } else {
                    try {
                        phone.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //TODO выполнение команд
                //выполнение команд
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void connect() throws IOException {
        int res;
        phone = new Phone("127.0.0.1", Server.PORT);//адрес сервера и порт
        System.out.println("Connected to server: ip address is " + phone.getIp());
        if (A) {
            res = phone.writeLine("Admin$" + id);
        } else {
            res = phone.writeLine("Client$" + id);
        }
        if (res != 0) {
            phone.closed = true;
            phone.close();
        }
        //отправка и получение информации
        //непрерывное чтение данных в отдельном потоке
        readRequest();
        while (true) {
            String request;
           /* считывание команды админов
            и результата(в будующем будет отправлятся автоматически) клиентов
            */
            request = s.nextLine();
            /*
            отправка в зависимости от типа клиента
            phone.writeLine((Math.random() * 100 + 1) + " AVNOAINOI@^F(Q@^^YC*(A%*%@!@_@_!+@(E");
            System.out.println("1");*/
            if (A) {
                res = phone.writeLine("A$" + request);
            } else {
                res = phone.writeLine("C$" + request);
            }
            if (res != 0) {
                phone.close();
            }
        }
    }

}
