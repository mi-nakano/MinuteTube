package jp.mzkcreation.minutetube;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


public class PlayerActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener{

    YouTubePlayerView youtubeView;
    LinearLayout baseLayout;
    String videoId;
    boolean isFullScreen;

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

        String key = Util.getYoutubeProperty().getProperty("youtube.apikey");
        youtubeView = (YouTubePlayerView) findViewById (R.id.youtube_view);
        youtubeView.initialize(key, this);

        baseLayout = (LinearLayout) findViewById(R.id.player_base);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
        Log.d("warn", "Initialization Failure");
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer player, boolean wasRestored) {
        // Playerフルスクリーン時の挙動を手動制御に設定
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        player.setOnFullscreenListener(this);
        if (!wasRestored){
            player.loadVideo(videoId);
        }
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.isFullScreen = isFullscreen;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 横向きにした場合にアクティビティが再構成されないようにする必要がある。
        doLayout();
    }

    private void doLayout() {
        // プレイヤーのパラメータを取得
        LinearLayout.LayoutParams playerParams = (LinearLayout.LayoutParams) youtubeView.getLayoutParams();
        // フルスクリーンの場合
        if (isFullScreen) {
            // YouTubePlayerの画面サイズを設定
            playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            // プレイヤー以外を非表示にする
//            toolbar.setVisibility(View.GONE);
//            otherViews.setVisibility(View.GONE);
        }
        // フルスクリーンではない場合
        else {
            // プレイヤー以外も表示する
//            toolbar.setVisibility(View.VISIBLE);
//            otherViews.setVisibility(View.VISIBLE);
            // 画面を横向きにした場合のレイアウト
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // PlayerとotherViewsのパラメータを設定
                playerParams.width = 0;
//                otherViewsParams.width = 0:
                playerParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                otherViewsParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
                playerParams.weight = 1;
                // 外枠のレイアウト
                baseLayout.setOrientation(LinearLayout.HORIZONTAL);
            }
            // 画面が縦の場合のレイアウト
            else {
                // PlayerとotherViewsのパラメータを設定
                playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
//                otherViewsParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                playerParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                playerParams.weight = 0;
//                otherViewsParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
                // 外枠のレイアウト
                baseLayout.setOrientation(LinearLayout.VERTICAL);
            }
        }
    }
}
