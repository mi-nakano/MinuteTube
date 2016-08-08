package jp.mzkcreation.minutetube;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = this.getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");

        TextView tv = (TextView) findViewById(R.id.player_title);
        tv.setText(title);
        TextView dv = (TextView) findViewById(R.id.player_description);
        dv.setText(description);
    }
}
