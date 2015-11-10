package edu.umbc.cs.ebiquity.heimdall.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import edu.umbc.cs.ebiquity.heimdall.R;
import edu.umbc.cs.ebiquity.heimdall.util.CheckPermissionsHelper;

public class MainActivity extends AppCompatActivity {
    private Switch mPolicy1Switch;
    private Button mShowAppsButton;
    private Button mDeleteAppButton;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mShowAppsButton = (Button) findViewById(R.id.showAppsButton);
        mShowAppsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShowAppsMainActivity.class);
                startActivity(intent);
            }
        });

        mDeleteAppButton = (Button) findViewById(R.id.deleteAppButton);
        mDeleteAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse(getAppToDelete()));
                startActivity(intent);
            }
        });

        mPolicy1Switch = (Switch) findViewById(R.id.policy1Switch);
        mPolicy1Switch.setChecked(getPolicyState());
        mPolicy1Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

//        mShowAppsButton.performClick();
        CheckPermissionsHelper.checkMarshMallowPermissions(getApplicationContext(), this);
    }

    private String getAppToDelete() {
        return "package:edu.umbc.cs.ebiquity.mithril.parserapp";
    }

    private boolean getPolicyState() {
        SharedPreferences prefs = getSharedPreferences("policy1", Context.MODE_WORLD_READABLE);
        if(prefs.getString("policy1", "false").equals("true"))
            return true;
        return false;
    }

    private void test() {
        String policy1;
        SharedPreferences prefs = getSharedPreferences("policy1", Context.MODE_WORLD_READABLE);
        policy1 = prefs.getString("policy1", null);
        Log.d("PKDLog", "policy1: " + policy1);
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

//    private void togglePolicyUsingSystemProperties(boolean isChecked) {
//        String policy1;
//        if (isChecked) {
//            System.setProperty("policy1", "true");
//            policy1 = System.getProperty("POLICY1");
//        } else {
//            System.setProperty("policy1", "false");
//            policy1 = System.getProperty("POLICY1");
//        }
//        Log.d("PKDLog", "policy1: " + policy1);
//    }
//
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
