import java.sql.*;

public class Database {
    private static Database database;
    private String url = "jdbc:mysql://localhost:3306/example" +
            "?characterEncoding=utf8&useSSL=false";
    private String userName = "Evgenia";
    private String passwordBD = "ZXC123zxc";

    private Database() {
        this.database = database;
    }

    public static Database getObject() {
        if(database == null){
            database = new Database();
        }
        return database;
    }

    public boolean setData(String loginPlayer, String passwordPlayer) throws ClassNotFoundException{
        Class.forName("com.mysql.jdbc.Driver");
        try(Connection connection = DriverManager.getConnection(url,userName,passwordBD);
            Statement statement = connection.createStatement()){
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Datas (id MEDIUMINT NOT NULL auto_increment, login VARCHAR(30) NOT NULL, password VARCHAR(30) NOT NULL, PRIMARY KEY(id))");
            boolean is_true = false;
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Datas");
            while(resultSet.next()){
                if(loginPlayer.compareTo(resultSet.getString("login")) == 0) is_true = true;
            }
            if(!is_true) {
                statement.executeUpdate("INSERT INTO Datas (login, password) VALUES ('" + loginPlayer + "', '" + passwordPlayer + "')");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getData(String loginPlayer, String passwordPlayer) throws ClassNotFoundException {

        Class.forName("com.mysql.jdbc.Driver");
        try(Connection connection = DriverManager.getConnection(url,userName,passwordBD);
            Statement statement = connection.createStatement()){
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Datas (id MEDIUMINT NOT NULL auto_increment, login VARCHAR(30) NOT NULL, password VARCHAR(30) NOT NULL, PRIMARY KEY(id))");

            ResultSet resultSet = statement.executeQuery("SELECT * FROM Datas");
            while(resultSet.next()){
                if(loginPlayer.compareTo(resultSet.getString("login")) == 0 &&
                        passwordPlayer.compareTo(resultSet.getString("password")) == 0) return true;
//                System.out.println(resultSet.getInt("id"));
//                System.out.println(resultSet.getString("login"));
//                System.out.println(resultSet.getString("password"));
//                System.out.println("-----------------");
            }

            System.out.println("All okey");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
