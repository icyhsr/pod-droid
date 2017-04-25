package trainedge.pod_droid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import trainedge.pod_droid.restclient.request.RssFeedRequest;
import trainedge.pod_droid.tools.Util;

import static trainedge.pod_droid.adapters.CategoryAdapter.CAT_NAME;
import static trainedge.pod_droid.adapters.CategoryAdapter.CAT_POS;

public class PodcastViewerActivity extends AppCompatActivity {

    private String url;
    private String category_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupPodcast();
        loadPodcast();
    }

    private void loadPodcast() {
        try {
            RssFeedRequest request = new RssFeedRequest(url);
            byte[] bytes = request.loadDataFromNetwork();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupPodcast() {
        Intent intent = getIntent();
        if (getIntent() != null) {
            category_name = getIntent().getStringExtra(CAT_NAME);
            int pos = getIntent().getIntExtra(CAT_POS, 0);
            url = getResources().getStringArray(R.array.podcast)[pos];
            getSupportActionBar().setSubtitle(category_name);
        }
    }

}
