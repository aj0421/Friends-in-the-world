package com.example.friends_in_the_world.Group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.friends_in_the_world.Classes.Message;
import com.example.friends_in_the_world.Controllers.MainController;
import com.example.friends_in_the_world.MainActivity;
import com.example.friends_in_the_world.R;


public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {
    private Button createButton;
    private EditText groupName;
    private MainController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        controller = (MainController) getApplication();

        //find ui component
        createButton = (Button) findViewById(R.id.createOk);
        createButton.setOnClickListener(this);
        groupName = (EditText) findViewById(R.id.gname);

    }

    @Override
    public void onClick(View v) {
        if (R.id.createOk == v.getId()) {
            String group = groupName.getText().toString();
            String user = controller.getUser().getValue().getName();
            controller.send(Message.register(group, user));

            Intent intent = new Intent(CreateGroupActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
