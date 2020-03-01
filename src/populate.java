import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

public class populate {
    public static void main(String[] args) throws IOException, SQLException {
        Connection connection = DBConnection.getDBConnection();
        System.out.println(connection);

        String businessFileName = args[0];
        String reviewFileName = args[1];
        String checkinFileName = args[2];
        String userFileName = args[3];

        System.out.println(businessFileName +  "   " + reviewFileName +  "   "  + checkinFileName  +    "   " +  userFileName );

        JsonParsing.createFile(businessFileName);
        JsonParsing.createFile(reviewFileName);
        JsonParsing.createFile(checkinFileName);
        JsonParsing.createFile(userFileName);


        JsonParsing.parseBusinessFile();
        System.out.println("Parsing Business file Completed");
        JsonParsing.parseUserFile();
        System.out.println("Parsing User file Completed");
        JsonParsing.parseReviewFile();
        System.out.println("Parsing Review file Completed");
        DBConnection.insertQueriesInDb();
    }
}
