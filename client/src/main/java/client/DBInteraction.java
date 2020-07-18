package client;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DBInteraction {
    private static Connection connection;
    private static PreparedStatement readStatement;
    private static PreparedStatement writeStatement;
    private Boolean connected = false;
    private final String DT_FORMAT = "dd.MM.yyyy HH:mm:ss";

    private static boolean connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:./client/src/main/resources/client.db");
        readStatement = connection.prepareStatement("SELECT Timestamp,Message FROM messageHistory " +
                "WHERE Login = ?;");
        writeStatement = connection.prepareStatement("INSERT INTO messageHistory (Login,Timestamp,Message) " +
                "VALUES (?, ?, ?);");
        return true;
    }

    public DBInteraction() throws Exception {
        try {
            connected = connect();
        }
        catch (ClassNotFoundException|SQLException e){
            e.printStackTrace();
            throw new Exception("DB not connected");
        }
    }

    public void getMessageHistory(List<String> messages,String login) throws SQLException {
        if(!connected){
            return;
        }
        ResultSet rs;
        SimpleDateFormat fmt = new SimpleDateFormat(DT_FORMAT);

        readStatement.setString(1,login);
        rs = readStatement.executeQuery();
        while (rs.next()){
            messages.add(fmt.format(rs.getTimestamp(1)) + ": " + rs.getString(2));
        }
    }

    public void addToMessageHistory(String login,String message) throws SQLException {
        if(!connected){
            return;
        }
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        writeStatement.setString(1,login);
        writeStatement.setDate(2,date);
        writeStatement.setString(3,message);
        writeStatement.executeUpdate();
    }


}
