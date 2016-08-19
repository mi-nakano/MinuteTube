package jp.mzkcreation.minutetube;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static YouTube youtube;
    private static final long NUMBER_OF_VIDEOS_RETURNED = 5;
    private static final long TIMEOUT = 10000;

    private ProgressDialog progressDialog;

    PullToRefreshListView refreshListView;
    CustomAdapter adapter;
    EditText searchText;
    String searchedQuery;
    String searchedDuration;
    String pageToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        refreshListView = (PullToRefreshListView) findViewById(R.id.search_list);
        refreshListView.setOnItemClickListener(new Listener());
        refreshListView.setEmptyView(findViewById(R.id.list_empty));
        searchText = (EditText) findViewById(R.id.search_text);
        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // EditTextのフォーカスが外れた場合
                if (hasFocus == false) {
                    // ソフトキーボードを非表示にする
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                v.requestFocus();
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Searching...");
                progressDialog.show();

                Spinner spinner = (Spinner)findViewById(R.id.search_spinner);
                int index = spinner.getSelectedItemPosition();
                final SearchTask task = new SearchTask();
                task.execute(searchText.getText().toString(), getDurationParam(index));
                searchedQuery = searchText.getText().toString();

                // set timeout
                Thread monitor = new Thread(){
                    public void run(){
                        try{
                            task.get(TIMEOUT, TimeUnit.MILLISECONDS);
                        } catch (Exception e){
                            Log.d("debug", "TimeOut!");
                            task.cancel(true);
                        }
                    }
                };
                monitor.start();
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("E77C3EF5B63C9A3BBED4872E06547151")
                .build();
        mAdView.loadAd(adRequest);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getDurationParam(int spinnerIndex){
        switch (spinnerIndex){
            case 0:
                return "short";
            case 1:
                return "medium";
            case 2:
                return "long";
        }
        return "any";
    }

    private List<VideoItem> requestVideos() throws IOException{
        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("youtube-cmdline-search-sample").build();
        YouTube.Search.List search = youtube.search().list("id,snippet");
        search.setType("video");
        String apiKey = Util.getYoutubeAPIKey();
        search.setKey(apiKey);
        search.setQ(searchedQuery);
        search.setVideoDuration(searchedDuration);
        search.setFields("nextPageToken");
        search.setFields("nextPageToken,items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
        if (pageToken != null && !pageToken.equals("")){
            search.setPageToken(pageToken);
        }
        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
        SearchListResponse response = search.execute();
        pageToken = response.getNextPageToken();
        Log.d("debug", "pageToken update: " + pageToken);
        List<SearchResult> searchResultList = response.getItems();

        if(searchResultList == null || searchResultList.isEmpty()){
            return null;
        }

        // idをカンマで区切った文字列を作る
        StringBuilder idsBuilder = new StringBuilder();
        for(SearchResult res : searchResultList){
            idsBuilder.append(res.getId().getVideoId());
            idsBuilder.append(",");
        }
        idsBuilder.deleteCharAt(idsBuilder.length() - 1);

        // videoの再生時間を取得する
        YouTube.Videos.List videos = youtube.videos().list("contentDetails,statistics");
        videos.setKey(apiKey);
        videos.setFields("items(contentDetails/duration,statistics/viewCount)");
        videos.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
        videos.setId(idsBuilder.toString());
        List<Video> videoList = videos.execute().getItems();

        // Videoの情報を返す
        List<VideoItem> viList = new ArrayList<>();
        for(int i=0; i < searchResultList.size(); i++){
            Video v = videoList.get(i);
            VideoItem vi = VideoItem.makeVideoItem(searchResultList.get(i), v.getContentDetails().getDuration(), v.getStatistics().getViewCount());
            viList.add(vi);
        }
        return viList;
    }

    private void addVideosToAdapter(List<VideoItem> vis){
        for(VideoItem vi : vis){
            adapter.add(vi);
        }
    }

    class SearchTask extends AsyncTask<String, Void, List<VideoItem>> {

        @Override
        protected List<VideoItem> doInBackground(String... words){
            searchedQuery = words[0];
            searchedDuration = words[1];

            try{
                return requestVideos();
            } catch (IOException e){
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<VideoItem> results){
            adapter = new CustomAdapter(MainActivity.this);
            if (results != null) {
                addVideosToAdapter(results);
                refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                        Log.d("debug", "refresh");
                        new RefreshTask().execute();
                    }
                });
            } else {
                refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                Toast.makeText(MainActivity.this, "該当する動画が見つかりませんでした", Toast.LENGTH_LONG).show();
            }
            refreshListView.setAdapter(adapter);
            dismissDialog();
        }

        @Override
        public void onCancelled(List<VideoItem> results){
            dismissDialog();
            Toast.makeText(MainActivity.this, "接続に失敗しました.", Toast.LENGTH_LONG).show();
        }

        private void dismissDialog(){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    class Listener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            ListView list = (ListView) parent;
            VideoItem video = (VideoItem) list.getItemAtPosition(position);

            // 新しいアクティビティをスタート
            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            intent.putExtra("id", video.getVidoId());
            intent.putExtra("title", video.getTitle());
            intent.putExtra("description", video.getDescription());
            startActivity(intent);
        }
    }

    class RefreshTask extends AsyncTask<String, Void, List<VideoItem>>{
        @Override
        protected List<VideoItem> doInBackground(String... words){
            try{
                return requestVideos();
            }catch (Exception e){
                Log.d("debug", "Request failed");
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<VideoItem> results){
            if(results != null){
                addVideosToAdapter(results);
            }
            endRefresh();
        }

        @Override
        public void onCancelled(List<VideoItem> results){
            Toast.makeText(MainActivity.this, "接続に失敗しました.", Toast.LENGTH_LONG).show();
            endRefresh();
        }

        private void endRefresh(){
            refreshListView.onRefreshComplete();
        }
    }
}
