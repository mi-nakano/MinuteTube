package jp.mzkcreation.minutetube;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;

import java.math.BigInteger;

import javax.annotation.Nonnull;

/**
 * Created by nakanomizuki on 2016/08/05.
 */
public class VideoItem {
    private String id, title, description, thumbnail;
    private MyTime myTime;
    private BigInteger viewCount;

    public VideoItem(String id, String title, String description, String thumbnail, String time, BigInteger viewCount){
        this.id = id;
        this.title = title;
        this.description = description;
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

    public String getThumbnail(){
        return thumbnail;
    }

    public MyTime getMyTime(){
        return myTime;
    }

    public BigInteger getViewCount(){
        return viewCount;
    }

    @Nonnull
    public static VideoItem makeVideoItem(SearchResult res, String duration, BigInteger viewCount){
        SearchResultSnippet snippet = res.getSnippet();
        String thumbnail = snippet.getThumbnails().getDefault().getUrl();
        return new VideoItem(res.getId().getVideoId(), snippet.getTitle(), snippet.getDescription(), thumbnail, duration, viewCount);
    }
}
