package server;

import java.sql.*;

public class DBAuthService implements AuthService {
    private static Connection connection;
    private static PreparedStatement regStatement;
    private static PreparedStatement authStatement;
    private static PreparedStatement getUserStatement;
    private static PreparedStatement loginExistsStatement;
    private static PreparedStatement nicknameExistsStatement;
    private static PreparedStatement changeNicknameStatement;

    private static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:./server/src/main/resources/server.db");
        getUserStatement = connection.prepareStatement("SELECT NickName FROM userData WHERE Login = ? and Password = ?;");
        nicknameExistsStatement = connection.prepareStatement("SELECT 1 FROM userData WHERE NickName = ?;");
        loginExistsStatement = connection.prepareStatement("SELECT 1 FROM userData WHERE Login = ?;");
        regStatement = connection.prepareStatement("INSERT INTO userData (Login,Password,NickName) VALUES (?,?,?);");
        authStatement = connection.prepareStatement("SELECT 1 FROM userData WHERE Login = ? and Password = ?;");
        changeNicknameStatement = connection.prepareStatement("UPDATE userData SET NickName = ? WHERE Login = ?;");
    }


    public DBAuthService() {
        try {
            connect();
        }
        catch (ClassNotFoundException|SQLException e){
            throw new RuntimeException(String.format("Connection error: %s",e.getMessage()));
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) throws SQLException {

            getUserStatement.setString(1,login);
            getUserStatement.setString(2,password);
            ResultSet rs = getUserStatement.executeQuery();
            if(rs.next()){
                return rs.getString(1);
            }

        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) throws SQLException {
        ResultSet rs;
        loginExistsStatement.setString(1,login);
        rs = loginExistsStatement.executeQuery();
        if (rs.next()){
            return false;
        }
        nicknameExistsStatement.setString(1,nickname);
        rs = loginExistsStatement.executeQuery();
        if (rs.next()){
            return false;
        }
        regStatement.setString(1,login);
        regStatement.setString(2,password);
        regStatement.setString(3,nickname);
        if(regStatement.executeUpdate() > 0){
            return true;
        }
        return false;


    }

    @Override
    public boolean changeNickname(String login, String password, String newNickname) throws SQLException {
        ResultSet rs;
        authStatement.setString(1,login);
        authStatement.setString(2,password);
        rs = authStatement.executeQuery();
        if (!rs.next()){
            return false;
        }
        changeNicknameStatement.setString(1,newNickname);
        changeNicknameStatement.setString(2,login);
        if(changeNicknameStatement.executeUpdate() > 0){
            return true;
        }

        return false;
    }
}
