 package trainedge.pod_droid;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

 public class SoundActivity extends AppCompatActivity {

    ListView listView;

     List<String> list;

     ListAdapter adapter;

     MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        listView = (ListView) findViewById(R.id.listView);

        list = new ArrayList<>();

        Field[] fields = R.raw.class.getFields();
        for (int i =0; i<fields.length;i++) {
            list.add(fields[i].getName());

        }
        //remove first two elements
        list.remove(0);
        list.remove(0);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();

                }
                int resID = getResources().getIdentifier(list.get(position), "raw", getPackageName());
                mediaPlayer = MediaPlayer.create(SoundActivity.this, resID);
                mediaPlayer.start();
            }

        });

            }
        }



