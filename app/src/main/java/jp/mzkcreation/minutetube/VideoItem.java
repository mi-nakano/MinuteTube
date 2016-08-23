package jp.mzkcreation.minutetube;

import android.util.Log;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;

import java.math.BigInteger;
import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * Created by nakanomizuki on 2016/08/05.
 */
public class VideoItem {
    private String id, title, description, channelTitle, thumbnail;
    private MyTime myTime;
    private BigInteger viewCount;
    private static final BigInteger
            killo = BigInteger.valueOf(1000),
            mega = killo.multiply(killo),
            jpMan = BigInteger.valueOf(10000);


    private VideoItem(String id, String title, String description, String channelTitle, String thumbnail, String time, BigInteger viewCount){
        this.id = id;
        this.title = title;
        this.description = description;
        this.channelTitle = channelTitle;
        this.thumbnail = thumbnail;
        myTime = MyTime.make(time);
        this.viewCount = viewCount;
    }

    public String getVidoId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getChannelTitle(){
        return channelTitle;
    }

    public String getThumbnail(){
        return thumbnail;
    }

    public MyTime getMyTime(){
        return myTime;
    }

    public BigInteger getViewCount(){
        return viewCount;
    }

    public String getViewCountString(){
        Log.d("debug", "viewCount=" + viewCount);
        if(Locale.getDefault().equals(Locale.JAPAN)){
            return getViewCountStrigJp();
        }
        return getViewCountStringEn();
    }
    private String getViewCountStrigJp(){
        int test = viewCount.compareTo(jpMan);
        if (test < 0){
            return viewCount.toString();
        } else{
            return viewCount.divide(jpMan).toString() + "万";
        }
    }
    private String getViewCountStringEn(){
        int test = viewCount.compareTo(killo);
        if (test < 0){
            return viewCount.toString();
        } else{
            int test2 = viewCount.compareTo(mega);
            if(test2 < 0){
                return viewCount.divide(killo).toString() + "K";
            }else{
                return viewCount.divide(mega).toString() + "M";
            }
        }
    }

    @Nonnull
    public static VideoItem makeVideoItem(SearchResult res, String duration, BigInteger viewCount){
        SearchResultSnippet snippet = res.getSnippet();
        String thumbnail = snippet.getThumbnails().getDefault().getUrl();
        return new VideoItem(res.getId().getVideoId(), snippet.getTitle(), snippet.getDescription(), snippet.getChannelTitle(), thumbnail, duration, viewCount);
    }
}
