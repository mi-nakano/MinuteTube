package jp.mzkcreation.minutetube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
    ArrayList<Video> videoList;

    public CustomAdapter(Context context){
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        videoList = new ArrayList<>();
    }

    public void setVideoList(ArrayList<Video> list){
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

        Video v = videoList.get(position);
        ((TextView)convertView.findViewById(R.id.item_title)).setText(v.getTitle());
        ((TextView)convertView.findViewById(R.id.item_description)).setText(v.getDescription());

        //サムネイルの設定
        ImageView thumbnail = (ImageView)convertView.findViewById(R.id.item_thumbnail);
        thumbnail.setTag(v.getThumbnail());     // URLをタグとしておく
        new ImageGetTask(thumbnail).execute();

        return convertView;
    }

    public void add(Video v){
        videoList.add(v);
    }


    class ImageGetTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView image;
        private String tag;

        public ImageGetTask(ImageView image){
            this.image = image;
            tag = image.getTag().toString();
        }

        @Override
        protected Bitmap doInBackground(String... params){
            try {
                URL imageUrl = new URL(tag);
                InputStream imageIs;
                imageIs = imageUrl.openStream();
                return BitmapFactory.decodeStream(imageIs);
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