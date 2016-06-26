package com.osfg.certificatepinning;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.osfg.certificatepinning.utils.CertpinningUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    boolean pinCerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((Button)findViewById(R.id.submit_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = ((EditText)findViewById(R.id.enter_url_id)).getText().toString();
                if(validateUrl(url)) {
                    ((Button)findViewById(R.id.submit_id)).setClickable(false);
                    ((Button)findViewById(R.id.clear_id)).setClickable(false);
                    ((Button)findViewById(R.id.submit_id)).setText("Executing....");
                    new NetworkTask(MainActivity.this,MainActivity.this.pinCerts,url).execute();
                }
            }
        });


        ((Button)findViewById(R.id.clear_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)findViewById(R.id.enter_url_id)).setText("");
                ((TextView)findViewById(R.id.resultView)).setText("Results will be displayed here...");
            }
        });

        ((Button)findViewById(R.id.mode_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Please select pinning mode from options menu!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
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


        switch(id)
        {
            case R.id.action_settings:
                CertpinningUtil.showDialog("Notes",getResources().getStringArray(R.array.notes_array),this);
                return true;
            case R.id.action_httpclient_pinned:
                pinCerts = true;
                ((EditText)findViewById(R.id.enter_url_id)).setText("https://www.ssllabs.com/");
                ((Button)findViewById(R.id.mode_id)).setText(getString(R.string.mode_pinned));
                return true;
            case R.id.action_httpclient_unpinned:
                ((Button)findViewById(R.id.mode_id)).setText(getString(R.string.mode_unpinned));
                pinCerts = false;
                return true;
             default:
                return super.onOptionsItemSelected(item);
        }

    }

    private boolean validateUrl(String url) {
        if(url == null || url.length() < 1) {
            Log.e(TAG,"Invalid URL. Please provide valid URL");
            Toast.makeText(this, "Please provide valid URL", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
