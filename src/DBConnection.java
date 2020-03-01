import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

public class DBConnection {
    public static Connection getDBConnection() {
        Connection connection = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/XE", "system", "oracle");
            return connection;
        } catch (SQLException e) {
            System.out.println("Not connected to database ! : " + e.getMessage());
        }
        return connection;
    }

    public static void insertQueriesInDb() throws IOException, SQLException {
        Connection dbConnection = getDBConnection();
        Statement statement = dbConnection.createStatement();
        String line = "";
        int count = 0;

        long startTime = System.nanoTime();
        FileReader fr = new FileReader(JsonParsing.outputFile);
        BufferedReader brd = new BufferedReader(fr);
        long updatedRows = 0;
        while (line != null) {
            line = brd.readLine();
            System.out.println(line);
            statement.addBatch(line);
            count++;
            System.out.println(count);
            if(count >5000){
                int[] updatedCont = statement.executeBatch();
                updatedRows = updatedRows + Arrays.stream(updatedCont).filter(i -> i==1).count();
                System.out.println("Updated Rows : " + updatedRows);
                System.out.println("=============================================");
                count =0;
                statement.close();
                statement = dbConnection.createStatement();
            }
        }
        statement.close();
        long endTime = System.nanoTime();
        System.out.println(endTime-startTime);
        brd.close();
        fr.close();
    }
}
