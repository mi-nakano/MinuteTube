package jp.mzkcreation.minutetube;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;


public class PlayerActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener{
    String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = this.getIntent();
        videoId = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        TextView tv = (TextView) findViewById(R.id.player_title);
        tv.setText(title);
        TextView dv = (TextView) findViewById(R.id.player_description);
        dv.setText(description);


        // フラグメントインスタンスを取得
        YouTubePlayerFragment youTubePlayerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubefragment);

        String key = Util.getYoutubeProperty().getProperty("youtube.apikey");
        // フラグメントのプレーヤーを初期化する
        youTubePlayerFragment.initialize(key, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
        Log.d("warn", "Initialization Failure");
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            // 指定された動画のサムネイルを読み込み、プレーヤーがその動画を再生する準備を行う
            player.cueVideo(videoId);
        }
    }
}
