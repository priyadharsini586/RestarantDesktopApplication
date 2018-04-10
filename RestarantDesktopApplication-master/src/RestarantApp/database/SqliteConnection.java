package RestarantApp.database;

import java.sql.*;

public class SqliteConnection {

    PreparedStatement preparedStatement = null;
    public static Connection connector()
    {
        try {

            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:prawnandcrab.db");
            return connection;
        }catch (Exception e)
        {
            System.out.println(e);
            return null;
        }
    }

    public void insertOrder_Master()
    {
        ResultSet rs;
        String query ="SELECT * FROM ORDER_MASTER";
        preparedStatement = null;
        try {
            preparedStatement = connector().prepareStatement(query);
           rs = preparedStatement.executeQuery();

           while (rs.next())
           {
             int value1 =   rs.getInt("order_id");
               int value2=  rs.getInt("item_id");
           }
           preparedStatement.close();
           rs.close();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }

    }


    public  void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:prawnandcrab.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS warehouses (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + "	capacity real\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
