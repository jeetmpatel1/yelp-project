import org.json.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;


public class JsonParsing {

    public static void main(String[] args) throws IOException {
        File outputFile = new File("output.txt");
        File theFile = new File("yelp_business.json");
        LineIterator it = FileUtils.lineIterator(theFile, "UTF-8");
        FileWriter fileWriter = new FileWriter(outputFile,true);

        try (Stream linesStream = Files.lines(theFile.toPath())) {
            linesStream.map(line -> parseBusinessObject((String) line)).forEach(output -> {
                try {
                    write((String) output,fileWriter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        /*try {
            while (it.hasNext()) {
                String line = it.nextLine();
                try {
                    PrintWriter printWriter = new PrintWriter(fileWriter,false);
                    String output = parseBusinessObject(line);
                    printWriter.print(output);
                    printWriter.print("\n");
                    printWriter.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        } finally {
            LineIterator.closeQuietly(it);
        }*/
        System.out.println("Finished");
        //parseBusinessObject(str);
    }
    private static void write(String record, Writer writer) throws IOException {

        writer.write(record);
        writer.write(System.getProperty( "line.separator" ));
        writer.flush();

    }
    public static String parseBusinessObject(String businessObject) {
        JSONObject jsonBusinessObject = new JSONObject(businessObject);
        String businessId = (String) jsonBusinessObject.get("business_id");
        if(businessId.startsWith("uUsfpN81JCMKyH6c0D0bTg")){
            System.out.println("Stops here");
        }
        String fullAddress = (String) jsonBusinessObject.get("full_address");
        int isOpen = ((boolean) jsonBusinessObject.get("open")) ? 1 : 0;
        String city = (String) jsonBusinessObject.get("city");
        String state = (String) jsonBusinessObject.get("state");
        int reviewCount = (int) jsonBusinessObject.get("review_count");
        String businessName = (String) jsonBusinessObject.get("name");
        JSONArray neighborhoods = (JSONArray) jsonBusinessObject.get("neighborhoods");
        String neighborhoodsStr = neighborhoods.toString();
        double longitude = jsonBusinessObject.getDouble("longitude");
        double latitude = jsonBusinessObject.getDouble("latitude");
        double stars = jsonBusinessObject.getDouble("stars");
        String businessType = (String) jsonBusinessObject.get("type");



        String insertStringForBusinessTable = "INSERT INTO y_business VALUES (" +
                "q'[" + businessId + "]'" + "," +
                "q'[" + fullAddress.replaceAll("\n"," ") + "]'" + "," +
                "" + isOpen + "" + "," +
                "q'[" + city + "]'" + "," +
                "q'[" + state + "]'" + "," +
                "" + reviewCount + "" + "," +
                "q'[" + businessName + "]'" + "," +
                "q'[" + neighborhoods + "]'" + "," +
                "" + longitude + "" + "," +
                "" + latitude + "" + "," +
                "" + stars + "" + "," +
                "q'[" + businessType + "]'" +

                ");";



        List<String[]> hoursTableEntries = new ArrayList<String[]>();
        JSONObject hours = jsonBusinessObject.getJSONObject("hours");
        Iterator<String> keys = hours.keys();
        while (keys.hasNext()) {
            String day = keys.next();
            JSONObject timePartObject = (JSONObject) hours.get(day);
            String closeTime = timePartObject.getString("close");
            String openTime = timePartObject.getString("open");
            hoursTableEntries.add(new String[]{businessId, day, openTime, closeTime});
        }

        List<String> finalListOfAttributesToInsert = new ArrayList<String>();
        JSONObject attributes = jsonBusinessObject.getJSONObject("attributes");
        Iterator<String> attributeKeys = attributes.keys();
        StringBuilder attributeSingleKey;

        while (attributeKeys.hasNext()) {
            ArrayList<String> innerStrs = new ArrayList<>();
            String currentKey = attributeKeys.next();
            String currentTempKey = new String(currentKey);
            attributeSingleKey = new StringBuilder(currentTempKey.replaceAll(" ", ""));
            if (attributes.get(currentKey) instanceof Boolean) {
                attributeSingleKey.append("_").append(attributes.getBoolean(currentKey));
            } else if (attributes.get(currentKey) instanceof String) {
                attributeSingleKey.append("_").append(attributes.getString(currentKey).replaceAll(" ", ""));
            } else if (attributes.get(currentKey) instanceof JSONObject) {
                JSONObject tempObj = (JSONObject) attributes.get(currentKey);
                Iterator<String> allInnerKeys = (tempObj).keys();
                currentTempKey.replaceAll(" ", "");
                while (allInnerKeys.hasNext()) {
                    String tempKey = allInnerKeys.next();
                    String currentTempKey2 = new String(tempKey);
                    attributeSingleKey.append("_").append(currentTempKey2).append("_").append(((JSONObject) tempObj).get(tempKey));
                    //
                    /*if (tempObj.get(tempKey) instanceof Boolean) {
                        attributeSingleKey.append("_").append(currentTempKey2).append("_").append(tempObj.getBoolean(tempKey));
                    } else if (tempObj.get(tempKey) instanceof String) {
                        attributeSingleKey.append("_").append(currentTempKey2).append("_").append(tempObj.getString(tempKey).replaceAll(" ", ""));
                    } else if (tempObj.get(tempKey) instanceof Integer) {
                        attributeSingleKey.append("_").append(currentTempKey2).append("_").append(tempObj.getInt(tempKey));
                    }else if (tempObj.get(tempKey) instanceof Double) {
                        attributeSingleKey.append("_").append(currentTempKey2).append("_").append(tempObj.getDouble(tempKey));
                    }*/
                    innerStrs.add(attributeSingleKey.toString());
                    attributeSingleKey = new StringBuilder(currentTempKey.replaceAll(" ", ""));
                }
            } else if (attributes.get(currentKey) instanceof Integer) {
                attributeSingleKey.append("_").append(attributes.getInt(currentKey));
            } else {
                System.out.println("ERROR====" + businessId + " " + attributes.get(currentKey).toString());
            }
            if (innerStrs.size() != 0) {
                finalListOfAttributesToInsert.addAll(innerStrs);
                innerStrs = null;
            } else {
                finalListOfAttributesToInsert.add(attributeSingleKey.toString());
            }
            attributeSingleKey = null;
        }

        return insertStringForBusinessTable;
    }
}

