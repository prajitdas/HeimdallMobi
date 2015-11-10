package edu.umbc.cs.ebiquity.heimdall.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import edu.umbc.cs.ebiquity.heimdall.HeimdallApplication;
import edu.umbc.cs.ebiquity.heimdall.R;
import edu.umbc.cs.ebiquity.heimdall.util.WebserviceCheckDataHelper;

public class DeleteAppsActivity extends AppCompatActivity {
    private Button mDeleteAppsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_apps);

        final ArrayList<String> appsToDelete = MainActivity.getWebserviceCheckDataHelper().getAppListToUninstall();
        Log.d(HeimdallApplication.getDebugTag(), "app list size = " + appsToDelete.size());
        if(appsToDelete.size()==0)
            finish();
        mDeleteAppsButton = (Button) findViewById(R.id.deleteAppsButton);
        mDeleteAppsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String appToDelete : appsToDelete) {
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse(appToDelete));
                    startActivity(intent);
                }
                finish();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
