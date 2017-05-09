package trainedge.pod_droid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import trainedge.pod_droid.Model.Podcast;
import trainedge.pod_droid.Model.Songs;
import trainedge.pod_droid.adapters.ChaptersAdapter;
import trainedge.pod_droid.restclient.request.RssFeedRequest;
import trainedge.pod_droid.restclient.service.MyRssPodcastService;
import trainedge.pod_droid.tools.Constants;
import trainedge.pod_droid.tools.Interfaces.NetworkActions;
import trainedge.pod_droid.tools.Interfaces.OnPlayButtonListener;
import trainedge.pod_droid.tools.Interfaces.TempFragmentListener;
import trainedge.pod_droid.tools.Util;

import static trainedge.pod_droid.adapters.CategoryAdapter.CAT_NAME;
import static trainedge.pod_droid.adapters.CategoryAdapter.CAT_POS;

public class PodcastActivity extends AppCompatActivity implements OnPlayButtonListener, RequestListener<byte[]>, AdapterView.OnItemClickListener, View.OnClickListener {

    protected SpiceManager spiceManager = new SpiceManager(MyRssPodcastService.class);

    ChaptersAdapter chaptersAdapter;
    Podcast parsedPodcast;
    String urlPodcast = "";
    Bundle args;
    MediaPlayer player;
    private int playbackPosition = 0;
    private String currentUrl;
    private boolean isPrepared = true;
    private SharedPreferences pod_pref;
    private String category_name;
    private String url;

    private String selected_url;
    private EditText etUrl;
    private Button btLoadPodcast;
    private RelativeLayout rlTitle;
    private TextView tvTitle;
    private ImageView ivPodcastLogo;
    private ListView lvChapters;
    private NetworkActions networkListener;
    private TempFragmentListener fragmentListener;
    private boolean enableOwnLog = true;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        etUrl = (EditText) findViewById(R.id.etUrl);
        btLoadPodcast = (Button) findViewById(R.id.btLoadPodcast);
        rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ivPodcastLogo = (ImageView) findViewById(R.id.ivPodcastLogo);
        lvChapters = (ListView) findViewById(R.id.lvChapters);

        try {
            networkListener = (NetworkActions) this;
            fragmentListener = (TempFragmentListener) this;
        } catch (ClassCastException e) {
            leh("Must implement the interfaces");
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupPodcast();
        initLogicalComponenets();
        assignListeners();

    }

    private void setupPodcast() {
        //setSupportActionBar(toolbar);
        if (getIntent() != null) {
            Intent intent = getIntent();
            category_name = getIntent().getStringExtra(CAT_NAME);
            pos = getIntent().getIntExtra(CAT_POS, 0);

            url = getResources().getStringArray(R.array.podcast)[pos];

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();

    }

    private void initLogicalComponenets() {
        selected_url = url;
        etUrl.setText(selected_url);
        player = new MediaPlayer();
    }

    private void executePodcastRequest() {
        showLoading(true);
        RssFeedRequest request = new RssFeedRequest(urlPodcast);
        spiceManager.execute(request, this);


    }

    private void assignListeners() {
        btLoadPodcast.setOnClickListener(this);
        lvChapters.setOnItemClickListener(this);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                showLoading(false);
                lvChapters.setEnabled(true);
                Toast.makeText(PodcastActivity.this, "loading", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                showLoading(false);
                lvChapters.setEnabled(true);
                mediaPlayer.start();
            }
        });
    }

    private void fillListview() {
        if (chaptersAdapter == null)
            chaptersAdapter = new ChaptersAdapter(this, parsedPodcast.getChannel().getSongs(), this);
        lvChapters.setAdapter(chaptersAdapter);

    }

    private void playMedia(String url, MediaPlayer mediaPlayer) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();

            }

            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            lvChapters.setEnabled(false);
            showLoading(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseMedia() {
        if (player.isPlaying() && player != null) {
            playbackPosition = player.getCurrentPosition();
            player.pause();
        }
    }

    private void restartMedia() {
        if (!player.isPlaying() && player != null) {

            player.seekTo(playbackPosition);
            player.start();
        }
    }

    private void killMediaPlayer() {
        if (player != null) {
            try {
                player.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPlayListener() {

    }

    private void parseXml(byte[] podcast) {
        try {
            Util.le(new String(podcast, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        InputStream input = new ByteArrayInputStream(podcast);
        Serializer serializer = new Persister();
        try {
            parsedPodcast = serializer.read(Podcast.class, input);
            Util.le(parsedPodcast.getChannel().getAuthor());
            tvTitle.setText(parsedPodcast.getChannel().getTitle());
            Picasso.with(this).load(parsedPodcast.getChannel().getImage().getLink()).into(ivPodcastLogo);
            fillListview();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Songs song = (Songs) parent.getAdapter().getItem(position);
        if (reproduceType(song)) {

            if (!song.getGuid().isEmpty()) {

                if (song.getGuid().equalsIgnoreCase(currentUrl)) {
                    if (!player.isPlaying())
                        restartMedia();
                    else
                        pauseMedia();
                } else {
                    currentUrl = song.getGuid();
                    playMedia(currentUrl, player);
                }
            }
        } else {
            Bundle args = new Bundle();
            args.putString(Constants.KEY_SONG, new Gson().toJson(song));
            //insertTemporal(Constants.TAG_FRAG_DETAIL, args);
            nextActivity(args);

        }

    }

    private void nextActivity(Bundle args) {

    }

    private boolean reproduceType(Songs song) {
        String type = "";
        boolean isAudio = false;
        if (song.getUrl().getType() != null && !song.getUrl().getType().isEmpty())
            type = song.getUrl().getType();
        switch (type) {
            case Constants.typeAudioMpeg:
            case Constants.typeAudioM4a:
                isAudio = true;
                break;
            case Constants.typeVideoM4v:
            case Constants.typeVideoMp4:
                isAudio = false;
                break;


        }
        return isAudio;
    }

    @Override
    public void onClick(View v) {
        hideKb();
        urlPodcast = selected_url;
        if (Util.isValidURL(urlPodcast)) {
            if (Util.isOnline(this))
                executePodcastRequest();
            else {
                Toast.makeText(this, R.string.home_no_internet, Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(this, R.string.home_invalid_url, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        showLoading(false);
        Toast.makeText(this,spiceException.getMessage() , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(byte[] podcast) {
        showLoading(false);
        if (podcast == null) return;
        parseXml(podcast);
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (spiceManager.isStarted())
            spiceManager.shouldStop();
    }

    protected void disableLog() {
        enableOwnLog = false;
    }

    protected void le(String message) {
        if (enableOwnLog) {
            Util.le(message);
        }
    }

    protected void leh(String message) {
        if (enableOwnLog) {
            Util.le(message, true);
        }
    }

    protected void li(String message) {
        if (enableOwnLog) {
            Util.li(message);
        }
    }

    protected void lih(String message) {
        if (enableOwnLog) {
            Util.li(message, true);
        }
    }

    protected void ld(String message) {
        if (enableOwnLog) {
            Util.ld(message);
        }
    }

    protected void ldh(String message) {
        if (enableOwnLog) {
            Util.ld(message, true);
        }
    }

    protected void lw(String message) {
        if (enableOwnLog) {
            Util.lw(message);
        }
    }

    protected void lwh(String message) {
        if (enableOwnLog) {
            Util.lw(message, true);
        }
    }

    protected void toast(String text) {
        Util.showToast(this, text);
    }

    protected void hideKb() {
        Util.hideKeyboard(this);
    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }

    protected void showLoading(boolean show) {
        if (networkListener != null)
            networkListener.showLoader(show);
    }

    protected void insertTemporal(String tag, Bundle args) {
        fragmentListener.inserTemporalFragmnet(tag, args);
    }
}

