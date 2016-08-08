package jp.mzkcreation.minutetube;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by nakanomizuki on 2016/08/08.
 */
public class Util {
    private static String PROPERTIES_FILENAME = "youtube.properties";

    public static Properties getYoutubeProperty(){
        Properties properties = new Properties();
        try {
            InputStream in = MainActivity.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);
        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }
        return properties;
    }

    public static String getYoutubeAPIKey(){
        return getYoutubeProperty().getProperty("youtube.apikey");
    }
}
