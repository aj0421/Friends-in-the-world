package com.example.friends_in_the_world.Group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.friends_in_the_world.Classes.Group;
import com.example.friends_in_the_world.Classes.Member;
import com.example.friends_in_the_world.Classes.Message;
import com.example.friends_in_the_world.Controllers.MainController;
import com.example.friends_in_the_world.MainActivity;
import com.example.friends_in_the_world.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinGroupActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button joinButton;
    private ArrayAdapter<String> listAdapter;
    private Map<String, Group> groups;
    private ListView mainListView;
    private MainController controller;
    private String clickGroup;

    Observer<Map<String, Group>> updateList = (@Nullable Map<String, Group> groups) -> {
        if (groups != null) {
            this.groups = groups;
            List<String> names = new ArrayList<>(groups.keySet());
            listAdapter = new ArrayAdapter<>(this, R.layout.row, R.id.label, names);
            mainListView.setAdapter(listAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        //find ui component
        joinButton = (Button) findViewById(R.id.joinOk);
        joinButton.setOnClickListener(this);
        mainListView = (ListView) findViewById(R.id.list);
        mainListView.setOnItemClickListener(this);

        // listview
        controller = (MainController) getApplication();
        controller.getGroups().observe(this, updateList);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clickGroup = mainListView.getItemAtPosition(position).toString();
    }

    @Override
    public void onClick(View v) {
        if (R.id.joinOk == v.getId()) {
            //join group
            Member user = controller.getUser().getValue();
            if (user != null) {
                controller.send(Message.register(clickGroup, user.getName()));
            }

            Intent intent = new Intent(JoinGroupActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}