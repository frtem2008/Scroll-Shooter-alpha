package OnlinePart;
//если этот код работает то его писал LiveFish, если нет, то example

//библиотеки для работы с сокетами, файлами, списками данных

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

//основной класс сервера
public class Server {
    //для цветной консоли
    public static final String CONNECTIONCOLOR = "Green",
            DISCONNECTIONCOLOR = "Cyan",
            LOGCOLOR = "Normal",
            REGISTRATIONCOLOR = "Yellow",
            FILECREATINGCOLOR = "Blue",
            ERRORCOLOR = "Red",
            INVALIDDATACOLOR = "Purple";
    //ANSI коды
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static int PORT = 26780;//порт сервера
    public static Scanner s = new Scanner(System.in);//для консоли сервера
    public static ArrayList<Phone> phones = new ArrayList<>();//все сокеты
    public static ArrayList<Integer> idAll = new ArrayList<>();//все уникальные id
    public static ArrayList<Integer> activeIds = new ArrayList<>();//все пользователи, активные сейчас
    //файл для определения пути к проекту
    public static File test = new File("test");
    //файлы для хранения запросов
    public static File mainRequestFile = new File("logs/fin req.txt");
    public static boolean COLOREDTEXT;//будет ли использоватся цветной вывод данных

    //печать цветного текста
    public static void printColored(String str, String color) {
        if (COLOREDTEXT) {
            switch (color.toLowerCase(Locale.ROOT)) {
                case "red" -> System.out.println(ANSI_RED + str + ANSI_RESET);
                case "black" -> System.out.println(ANSI_BLACK + str + ANSI_RESET);
                case "green" -> System.out.println(ANSI_GREEN + str + ANSI_RESET);
                case "yellow" -> System.out.println(ANSI_YELLOW + str + ANSI_RESET);
                case "blue" -> System.out.println(ANSI_BLUE + str + ANSI_RESET);
                case "purple" -> System.out.println(ANSI_PURPLE + str + ANSI_RESET);
                case "cyan" -> System.out.println(ANSI_CYAN + str + ANSI_RESET);
                case "white" -> System.out.println(ANSI_WHITE + str + ANSI_RESET);
                case "normal" -> System.out.println(str);
            }
        } else {
            System.out.println(str);
        }
    }

    //спрашивает пользователя о необходимости цветного вывода
    public static void initColors() {
        System.out.println("Do you want to use colored console? (true/false)");
        COLOREDTEXT = s.nextBoolean();
        s.nextLine();
    }

    //создание всех файлов сервера
    public static void createFiles() {
        try {
            //получение пути к файлам
            printColored("Attempting to create files\n", LOGCOLOR);
            printColored("Creating test: " + test.createNewFile(), FILECREATINGCOLOR);
            String testPath = test.getAbsolutePath().replaceAll("test", "");

            //создание логового файла
            //переменная для отладки
            boolean successfulCreation;

            //массив всех необходимых файлов (возможность для расширения в будующем)
            File[] files = {
                    mainRequestFile,
            };
            for (int i = 0; i < files.length; i++) {
                File cur = files[i];
                if (!cur.exists()) {
                    successfulCreation = cur.createNewFile();
                    if (successfulCreation) {
                        printColored("Successfully created file " + cur.getName(), FILECREATINGCOLOR);
                    } else {
                        printColored("!Failed to create file " + cur.getName(), FILECREATINGCOLOR);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //обновление всех активных id
    public static void refreshActiveIDs() {
        activeIds.clear();
        for (int i = 0; i < phones.size(); i++) {
            activeIds.add(phones.get(i).id);
        }
    }

    //тестирование цветов в консоли
    public static void colorTest() {
        printColored("Black", "black");
        printColored("Red", "red");
        printColored("Green", "green");
        printColored("Yellow", "yellow");
        printColored("Blue", "blue");
        printColored("Purple", "purple");
        printColored("Cyan", "cyan");
        printColored("White", "white");
    }

    public static void main(String[] args) {
        initColors(); //вопрос об использовании цвета в консоли
        //colorTest();
        createFiles(); //создание необходимых файлов
        new Thread(() -> {
            server();
        }).start();//запуск сервера

        new Thread(() -> {
            serverConsole();
        }).start();//запуск консоли сервера
    }

    //консоль сервера
    public static void serverConsole() {
        String action; //пользовательский ввод
        int writeSuccess;//успешность отправки данных клиенту
        while (true) {
            action = s.nextLine(); //ввод данных

            switch (action) {
                case "/shutdown": //безопасное выключение сервера
                    printColored("Shutting down...", DISCONNECTIONCOLOR);
                    for (int i = 0; i < phones.size(); i++) {
                        //отправка информации об отключении сервера клиентам во избежание ошибок
                        writeSuccess = phones.get(i).writeLine("SYS$SHUTDOWN");
                        int finalWriteSuccess = writeSuccess;
                        //в отдельном потоке, чтобы защитить от ошибок основной поток консоли
                        int finalI = i;
                        new Thread(() -> {
                            checkWriteSuccess(finalWriteSuccess, phones.get(finalI), Thread.currentThread());
                        }).start();
                    }
                    System.exit(0);
                    break;
                case "/connections": //вывод списка всех активных подключений
                    if (phones.size() > 0) {
                        printColored("All active connections: ", LOGCOLOR);
                        for (int i = 0; i < phones.size(); i++) {
                            printColored(phones.get(i).connection, LOGCOLOR);
                        }
                    } else {
                        printColored("No active connections", LOGCOLOR);
                    }
                    break;
                case "/idlist": //вывод всех зарегистрированных id
                    printColored("All registrated IDs: ", LOGCOLOR);
                    for (int i = 0; i < idAll.size(); i++) {
                        printColored(String.valueOf(idAll.get(i)), LOGCOLOR);
                    }
                    break;
                case "/help": //вывод справки
                    printColored("___________________________________", "Cyan");
                    printColored("Help: \n", "Cyan");
                    printColored("""
                            /help to show this
                            /shutdown to shut the server down
                            /disconnect <int id> to disconnect a client from server
                            /connections to show all active connections
                            /idlist to show all registrated ids
                            /msg <int id> <String message> to send a message to the client
                            ___________________________________\040
                            """, "Cyan");
                    break;
                default:
                    break;
            }
        }
    }

    //добавление с перезаписью для типа Phone (для списка сокетов)
    //свой equals был написан
    public static void addReplace(ArrayList<Phone> where, Phone what) {
        if (!where.contains(what)) {
            where.add(what);
        }
    }

    //функция для выявления неактивных клиентов (нет таймаута, тк в данном случае отключение должно происходить мнгновенно)
    public static void checkWriteSuccess(int writeSuccess, Phone phone, Thread current) {
        if (writeSuccess != 0) {
            try {
                printColored("\nClient with id " + phone.id + " disconnected\n", DISCONNECTIONCOLOR);
                phones.remove(phone);//удаление сокета из списка активных

                if (idAll.size() <= 0) {
                    printColored("No active admins to send disconnection information", INVALIDDATACOLOR);
                } else {
                    for (int i = 0; i < idAll.size(); i++) {
                        Phone cur = getPhoneById(phones, idAll.get(i));
                        writeSuccess = cur.writeLine("SYS$DISCONNECT$" + Math.abs(phone.id));
                        checkWriteSuccess(writeSuccess, cur, Thread.currentThread());
                    }
                }

                if (writeSuccess != -2) {
                    phone.closed = true;
                    phone.close();
                } else {
                    printColored("Interrupting client working thread", LOGCOLOR);
                    phone.closed = true;
                    phone.close();
                    current.interrupt(); //остановка потока, обрабатывавшего этот сокет
                }
                refreshActiveIDs();//обновление базы активных id при отключении
            } catch (IOException e) {
                //e.printStackTrace();
                printColored("\nFailed to log disconnection of a client", ERRORCOLOR);
            }
        }
    }

    //функция для удаления неактивных клиентов
    public static void checkReadSuccess(String read, Phone phone) {
        if (read != null) {
            if (read.equals("-1") || read.equals("-2")) {
                try {
                    phones.remove(phone);
                    phone.closed = true;
                    phone.close();
                    refreshActiveIDs(); //обновление базы активных id при отключении
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //основной поток сервера
    public static void server() {
        try (ServerSocket server = new ServerSocket(PORT)) { //запуск сервера
            printColored("Server started\n", "Yellow");
            boolean work = true;
            while (work) {
                Phone phone = new Phone(server);//создание сокета сервера и ожидание присоединения клиентов
                new Thread(() -> {//каждый клиент в отдельном потоке
                    printColored("Client connected: ip adress is " + phone.getIp(), CONNECTIONCOLOR);
                    //переменные для вывода и отправки информации
                    String data, success, dataSent;
                    int writeSuccess;

                    boolean isActive = true;
                    addReplace(phones, phone);//запись о сокетах

                    //в бесконечном цикле обрабатываем данные клиента
                    while (isActive) {
                        data = phone.readLine();//считывание данных
                        checkReadSuccess(data, phone);

                        if (data != null) {
                            if (data.equals("-2")) {
                                isActive = false;
                            }

                            refreshActiveIDs(); //обновление итендификаторов

                            try {
                                Thread.sleep(10);//пауза в запросах
                            } catch (InterruptedException e) {
                                printColored("Failed to do Thread.sleep: thread " + Thread.currentThread().getName() + " is interrupted", ERRORCOLOR);
                            }
                        }
                    }
                }).start();
            }
        } catch (NullPointerException | IOException e) {
            printColored("Failed to start a server:\n_________________________", ERRORCOLOR);
            e.printStackTrace();
        }
    }

    //получение Phone из списка по уникальному итендификатору
    public static Phone getPhoneById(ArrayList<Phone> phoneList, long id) {
        for (int i = 0; i < phoneList.size(); i++) {
            if (phoneList.get(i).id == id) {
                return phoneList.get(i);
            }
        }

        return null;
    }

    //приведение LocalDateTime в формат дд-мм[час:мин:сек]
    public static String dateNormalize(LocalDateTime date) {
        String res;
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        int hours = date.getHour();
        int min = date.getMinute();
        int sec = date.getSecond();
        res = day + "-" + month + "[" + hours + ":" + min + ":" + sec + "]";
        return res;
    }

    //функции для работы с файлами
    public static void appendStrToFile(File file, String str) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
            out.write(str + "\n");
            out.close();
        } catch (IOException e) {
            printColored("exception occurred" + e, ERRORCOLOR);
        }
    }

    public static void clearFile(String fileName) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, false));
            out.write("");
            out.close();
        } catch (IOException e) {
            printColored("exception occurred" + e, ERRORCOLOR);
        }
    }
}