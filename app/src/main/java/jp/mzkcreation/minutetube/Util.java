package jp.mzkcreation.minutetube;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by nakanomizuki on 2016/08/08.
 */
public class Util {
    private static String YOUTUBE_PROPERTIES = "youtube.properties";
    private static String AD_PROPERTIES = "ad.properties";


    private static Properties getProperties(String fileName){
        Properties properties = new Properties();
        try {
            InputStream in = MainActivity.class.getResourceAsStream("/" + fileName);
            properties.load(in);
        } catch (IOException e) {
            System.err.println("There was an error reading " + fileName + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }
        return properties;
    }
    public static Properties getYoutubeProperty(){
        return getProperties(YOUTUBE_PROPERTIES);
    }

    public static String getYoutubeAPIKey(){
        return getYoutubeProperty().getProperty("youtube.apikey");
    }

    public static Properties getAdProperties(){
        return getProperties(AD_PROPERTIES);
    }

    public static String getAdId(){
        return getAdProperties().getProperty("ad.unit_id");
    }
}
