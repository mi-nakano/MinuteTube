package jp.mzkcreation.minutetube;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static YouTube youtube;
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    ListView searchList;
    EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchList = (ListView) findViewById(R.id.search_list);
        searchText = (EditText) findViewById(R.id.search_text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinner = (Spinner)findViewById(R.id.search_spinner);
                int index = spinner.getSelectedItemPosition();
                SearchTask task = new SearchTask();
                task.execute(searchText.getText().toString(), getDurationParam(index));
            }
        });
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

    class SearchTask extends AsyncTask<String, Void, List<SearchResult>> {

        @Override
        protected List<SearchResult> doInBackground(String... words){
            String searchWord = words[0];
            String duration = words[1];

            try {
                youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("youtube-cmdline-search-sample").build();
                YouTube.Search.List search = youtube.search().list("id,snippet");
                search.setType("video");
                String apiKey = Util.getYoutubeAPIKey();
                search.setKey(apiKey);
                search.setQ(searchWord);
                search.setVideoDuration(duration);
                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
                SearchListResponse searchResponse = search.execute();
                List<SearchResult> searchResultList = searchResponse.getItems();
                return searchResultList;

            } catch (IOException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<SearchResult> results){
            if (results != null) {
                CustomAdapter adapter = new CustomAdapter(MainActivity.this);
                for(SearchResult result : results){
                    adapter.add(Video.makeVideo(result));
                }
                searchList.setAdapter(adapter);
                // ListViewアイテムを選択した場合の動作
                searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        ListView list = (ListView) parent;
                        Video video = (Video) list.getItemAtPosition(position);

                        // 新しいアクティビティをスタート
                        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                        intent.putExtra("id", video.getVidoId());
                        intent.putExtra("title", video.getTitle());
                        intent.putExtra("description", video.getDescription());
                        startActivity(intent);
                    }
                });
            }
        }
    }
}

