package com.example.friends_in_the_world.Group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.friends_in_the_world.Classes.Group;
import com.example.friends_in_the_world.Classes.Message;
import com.example.friends_in_the_world.Controllers.MainController;
import com.example.friends_in_the_world.R;


public class GroupActivity extends AppCompatActivity implements View.OnClickListener {
    private Button createButton;
    private Button joinButton;
    private Button leaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        createButton = (Button) findViewById(R.id.createGroup);
        createButton.setOnClickListener(this);
        joinButton = (Button) findViewById(R.id.joinGroup);
        joinButton.setOnClickListener(this);
        leaveButton = (Button) findViewById(R.id.leaveGroup);
        leaveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (R.id.createGroup == v.getId()) {

            Intent intent = new Intent(GroupActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        }
        else if (R.id.joinGroup == v.getId()) {

            Intent intent = new Intent(GroupActivity.this, JoinGroupActivity.class);
            startActivity(intent);
        }
        else if (R.id.leaveGroup == v.getId()) {

            //Code for leaving a group
            MainController controller = (MainController) getApplication();
            Group group = controller.getCurrentGroup().getValue();
            if (group != null && group.getID() != null) {
                controller.send(Message.unregister(controller.getCurrentGroup().getValue().getID().toString()));
            }
        }

    }

}

