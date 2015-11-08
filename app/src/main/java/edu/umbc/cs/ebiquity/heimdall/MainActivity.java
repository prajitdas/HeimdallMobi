package edu.umbc.cs.ebiquity.heimdall;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    private Switch policy1Switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        policy1Switch = (Switch) findViewById(R.id.policy1Switch);
        policy1Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /**
                 * This would seemingly not work because of: http://stackoverflow.com/a/7964166/1816861, so I am going to use a ContentProvider instead
                 */
//                togglePolicyUsingSystemProperties(isChecked);
                togglePolicyUsingContentProvider(isChecked);
                test();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "New policy addition is disabled right now!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void test() {
        String policy1;
        SharedPreferences prefs = getSharedPreferences("policy1", Context.MODE_WORLD_READABLE);
        policy1 = prefs.getString("policy1", null);
        Log.d("PKDLog", "policy1: " + policy1);
        Toast.makeText(this, policy1, Toast.LENGTH_LONG);
    }

    private void togglePolicyUsingContentProvider(boolean isChecked) {
        SharedPreferences sharedpreferences = getSharedPreferences("policy1", Context.MODE_WORLD_READABLE);
        if(isChecked) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("policy1", "true");
            editor.commit();
        } else {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("policy1", "false");
            editor.commit();
        }
    }

    private void togglePolicyUsingSystemProperties(boolean isChecked) {
        String policy1;
        if (isChecked) {
            System.setProperty("policy1", "true");
            policy1 = System.getProperty("POLICY1");
        } else {
            System.setProperty("policy1", "false");
            policy1 = System.getProperty("POLICY1");
        }
        Log.d("PKDLog", "policy1: " + policy1);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
