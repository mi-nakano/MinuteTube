package jp.mzkcreation.minutetube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by nakanomizuki on 2016/08/05.
 */
public class CustomAdapter extends BaseAdapter{
    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<VideoItem> videoList;
    LruCache<String, Bitmap> cache;

    private final int CACHE_SIZE = 1024 * 1024;

    public CustomAdapter(Context context){
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        videoList = new ArrayList<>();
        cache = new LruCache<>(CACHE_SIZE);
    }

    public void setVideoList(ArrayList<VideoItem> list){
        videoList = list;
    }

    @Override
    public int getCount(){
        return videoList.size();
    }

    @Override
    public Object getItem(int position){
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position){
        return position + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        convertView = layoutInflater.inflate(R.layout.list_item, parent, false);

        VideoItem v = videoList.get(position);
        ((TextView)convertView.findViewById(R.id.item_title)).setText(v.getTitle());
        ((TextView)convertView.findViewById(R.id.item_viewCount)).setText(v.getViewCountString());
        ((TextView)convertView.findViewById(R.id.item_channel)).setText(v.getChannelTitle());
        ((TextView)convertView.findViewById(R.id.item_duration)).setText(v.getMyTime().toString());

        //サムネイルの設定
        ImageView thumbnail = (ImageView)convertView.findViewById(R.id.item_thumbnail);
        thumbnail.setTag(v.getThumbnail());     // URLをタグとしておく
        new ImageGetTask(this, thumbnail).execute();


        return convertView;
    }

    public void add(VideoItem v){
        videoList.add(v);
    }

    void addCache(String tag, Bitmap bitmap){
        if(cache.get(tag) == null) {
            cache.put(tag, bitmap);
        }
    }

    Bitmap getFromCache(String tag){
        return cache.get(tag);
    }


    class ImageGetTask extends AsyncTask<String, Void, Bitmap> {
        private CustomAdapter adapter;
        private ImageView image;
        private String tag;

        public ImageGetTask(CustomAdapter adapter, ImageView image){
            this.adapter = adapter;
            this.image = image;
            tag = image.getTag().toString();
        }

        @Override
        protected Bitmap doInBackground(String... params){
            Bitmap bitmap = adapter.getFromCache(tag);
            if(bitmap != null){
                return bitmap;
            }
            try {
                URL imageUrl = new URL(tag);
                InputStream imageIs;
                imageIs = imageUrl.openStream();
                bitmap = BitmapFactory.decodeStream(imageIs);
                adapter.addCache(tag, bitmap);
                return bitmap;
            } catch (MalformedURLException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result){
            if(tag.equals(image.getTag())){
                image.setImageBitmap(result);
            }
        }
    }
}
