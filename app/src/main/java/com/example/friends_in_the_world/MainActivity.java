package com.example.friends_in_the_world;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.friends_in_the_world.Classes.Message;
import com.example.friends_in_the_world.Controllers.MainController;
import com.example.friends_in_the_world.Group.GroupActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private static final int REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private MainController controller;
    private MapsActivity map;
    private Button manageGroup;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        controller = (MainController) getApplication();

        manageGroup = (Button) findViewById(R.id.groupBtn);
        manageGroup.setOnClickListener(this);
        map = (MapsActivity) fm.findFragmentById(R.id.map);
        SharedPreferences sharedPref =
                getSharedPreferences(getString(R.string.file), Context.MODE_PRIVATE);
        name = sharedPref.getString("name", "");
        controller.setUsername(name);

        // Start locating the user
        getPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_ACCESS_FINE_LOCATION);
        getPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_ACCESS_COARSE_LOCATION);
        map.startLocating();
    }
    private void getPermission(String name, int req) {
        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, name)) {
            // The permission is not already granted
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, name)) {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{name}, req);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (R.id.groupBtn == v.getId()) {
            controller.send(Message.groups());
            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}