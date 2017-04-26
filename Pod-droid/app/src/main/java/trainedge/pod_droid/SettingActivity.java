package trainedge.pod_droid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,View.OnClickListener {

    public static final String TAG = "Settings";
    private Switch notif;
    private SharedPreferences pref;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = getSharedPreferences("setting_pref", MODE_PRIVATE);
        Button accdet = (Button) findViewById(R.id.acc_detail);
        Button ppol = (Button) findViewById(R.id.priv_pol);


        notif = (Switch) findViewById(R.id.notif);
        mAuth = FirebaseAuth.getInstance();
        accdet.setOnClickListener(this);
        ppol.setOnClickListener(this);
        notif.setOnCheckedChangeListener(this);
        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null) {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    // if (mAuth.getCurrentUser()==null){
                    Intent logint = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivity(logint);
                    finish();

                    // }
                }
            }
        };

        updateUI();


        /*SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("is_logged_in",true);
        editor.apply();*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.acc_detail) {
            Intent accintent = new Intent(SettingActivity.this, AccountActivity.class);
            startActivity(accintent);
        }
        if (v.getId() == R.id.priv_pol) {
            Intent privintent = new Intent(SettingActivity.this, PrivacyActivity.class);
            startActivity(privintent);
        }

    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor editor = pref.edit();
        if(buttonView.getId() == R.id.notif){
            editor.putBoolean("notif_option",isChecked);
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        boolean notifi = pref.getBoolean("notif_option",false);
        notif.setChecked(notifi);
    }

}
