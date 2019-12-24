import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

// реализуем интерфейс Runnable, который позволяет работать с потоками
public class ClientHandler implements Runnable {
    // экземпляр нашего сервера
    private Server server;
    // исходящее сообщение
    private PrintWriter outMessage;
    // входящее собщение
    private Scanner inMessage;
    private static final String HOST = "localhost";
    private static final int PORT = 3443;
    // клиентский сокет
    private Socket clientSocket = null;
    // количество клиента в чате, статичное поле
    private static int clients_count = 0;

    // конструктор, который принимает клиентский сокет и сервер
    public ClientHandler(Socket socket, Server server) {
        try {
            clients_count++;
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    // Переопределяем метод run(), который вызывается когда
    // мы вызываем new Thread(client).start();
    @Override
    public void run() {
        try {
            while (true) {
                // сервер отправляет сообщение
                server.sendMessageToAllClients("###new###member###" + server.uniqueNumberForEachHuman++);
                server.sendMessageToAllClients("Новый участник вошёл в чат!");
                server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
                break;
            }

            while (true) {
                // Если от клиента пришло сообщение
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();



                    //--------------------------------Регистрация---------------------------------
                    String loginAndPassword = "";

                    if (clientMessage.length() > 21) loginAndPassword = clientMessage.substring(0, 22);

                    if (loginAndPassword.compareTo("###login###password###") == 0) {
                        loginAndPassword = clientMessage.substring(22);
                        String[] masLoginAndPassword = loginAndPassword.split("###");
                        //if(server.database.setData(masLoginAndPassword[0],masLoginAndPassword[1])){
                        server.sendMessageToAllClients("###otvetRegistration###" + masLoginAndPassword[2] + "###" +
                                server.database.setData(masLoginAndPassword[0],masLoginAndPassword[1]));
                        //отсылать результат клиенту с его номером и ответом
                        //}
                        continue;
                    }
                    //----------------------------------------------------------------------------




                    //--------------------------------Авторизация---------------------------------
                    String loginAndPasswordAutorization = "";

                    if (clientMessage.length() > 21) loginAndPasswordAutorization = clientMessage.substring(0, 22);

                    if (loginAndPasswordAutorization.compareTo("###Autoriz#log#pass###") == 0) {
                        loginAndPasswordAutorization = clientMessage.substring(22);
                        String[] masLoginAndPassword = loginAndPasswordAutorization.split("###");
                        //if(server.database.setData(masLoginAndPassword[0],masLoginAndPassword[1])){
                        boolean nextStep = server.database.getData(masLoginAndPassword[0],masLoginAndPassword[1]);
                        if(nextStep) {
                            if(server.addMap(masLoginAndPassword[0], masLoginAndPassword[1], Integer.parseInt(masLoginAndPassword[2]))){
                                server.sendMessageToAllClients("###Autoriz#log#pass###" + masLoginAndPassword[2] + "###" + nextStep);
                            }
                            else{
                                server.sendMessageToAllClients("###Autoriz#log#pass###" + masLoginAndPassword[2] + "###false");
                            }
                        }else {
                            server.sendMessageToAllClients("###Autoriz#log#pass###" + masLoginAndPassword[2] + "###" + nextStep);
                        }
                        continue;
                        //отсылать результат клиенту с его номером и ответом
                        //}
                    }
                    //----------------------------------------------------------------------------





                    String deleteUser = "";

                    if (clientMessage.length() > 15) deleteUser = clientMessage.substring(0, 16);

                    // если клиент отправляет данное сообщение, то цикл прерывается и
                    // клиент выходит из чата
                    if (deleteUser.compareTo("##session##end##") == 0) {
                        server.deleteNumber = Integer.parseInt(clientMessage.substring(16));
                        break;
                    }
                    // выводим в консоль сообщение (для теста)
                    System.out.println(clientMessage);
                    // отправляем данное сообщение всем клиентам
                    server.sendMessageToAllClients(clientMessage);
                }
                // останавливаем выполнение потока на 100 мс
                Thread.sleep(100);
            }
        }
        catch (InterruptedException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        finally {
            this.close();
        }
    }
    // отправляем сообщение
    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // клиент выходит из чата
    public void close() {
        // удаляем клиента из списка
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
    }
}