import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {
    // порт, который будет прослушивать наш сервер
    static final int PORT = 3443;
    // список клиентов, которые будут подключаться к серверу
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    public int deleteNumber;

    public int uniqueNumberForEachHuman = 1;

    public Database database;

    public Map<Integer, String> states = new HashMap<Integer, String>();

    public int getAmountOfClients(){
        return clients.size();
    }

    public boolean addMap(String login, String password, Integer id){
        for (Map.Entry<Integer,String> pair: states.entrySet()) {
            if (login.compareTo(pair.getValue()) == 0) {
                return false;// нашли наше значение и возвращаем  ключ
            }
        }
        states.put(id, login);
//        if (states.get(id) == null) {
//            //если такого ключа нету, то добавляем
//            states.put(id, login);
//            return true;
//        }
        return true;
    }

    public void deleteMap(int id){
        states.remove(id);
    }

    public Server() {
        database = Database.getObject();
        // сокет клиента, это некий поток, который будет подключаться к серверу
        // по адресу и порту
        Socket clientSocket = null;
        // серверный сокет
        ServerSocket serverSocket = null;
        try {
            // создаём серверный сокет на определенном порту
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен!");
            // запускаем бесконечный цикл
            while (true) {
                // таким образом ждём подключений от сервера
                clientSocket = serverSocket.accept();
                // создаём обработчик клиента, который подключился к серверу
                // this - это наш сервер
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                // каждое подключение клиента обрабатываем в новом потоке
                new Thread(client).start();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                // закрываем подключение
                clientSocket.close();
                System.out.println("Сервер остановлен");
                serverSocket.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // отправляем сообщение всем клиентам
    public void sendMessageToAllClients(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }

    }

    // удаляем клиента из коллекции при выходе из чата
    public void removeClient(ClientHandler client) {

        deleteMap(deleteNumber);

        clients.remove(client);
    }

}