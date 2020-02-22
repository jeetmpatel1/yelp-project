import java.io.*;
import java.sql.Connection;

public class populate {
    public static void main(String[] args) throws FileNotFoundException {
        String businessFileName = args[0];
        String reviewFileName = args[1];
        String checkinFileName = args[2];
        String userFileName = args[3];

        System.out.println(businessFileName +  "   " + reviewFileName +  "   "  + checkinFileName  +    "   " +  userFileName );

        Connection connection = DBConnection.getDBConnection();
        System.out.println(connection);

        populate populateObject = new populate();
        String outerPath = System.getProperty("user.dir") + "\\" ;
        populateObject.readFile(outerPath, businessFileName);
        System.out.println(System.getProperty("user.dir"));

    }

    private void readFile(String outerPath , String fileName) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(outerPath + fileName);
        try {
            String data = readFromInputStream(fis);
        } catch (IOException e) {
            System.out.println("Unable to read file " + fileName);
        }
    }

    private String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}
