package jp.mzkcreation.minutetube;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;

import javax.annotation.Nonnull;

/**
 * Created by nakanomizuki on 2016/08/05.
 */
public class Video {
    private String title, description, thumbnail;

    public Video(String title, String description, String thumbnail){
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
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

    @Nonnull
    public static Video makeVideo(SearchResult res){
        SearchResultSnippet snippet = res.getSnippet();
        String thumbnail = snippet.getThumbnails().getDefault().getUrl();
        return new Video(snippet.getTitle(), snippet.getDescription(), thumbnail);
    }
}