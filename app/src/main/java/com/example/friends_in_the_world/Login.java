package com.example.friends_in_the_world;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Login  extends AppCompatActivity implements View.OnClickListener{

    private Button loginButton;
    private EditText loginEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //find ui component
        loginButton = (Button) findViewById(R.id.loginBtn);
        loginEdit = (EditText) findViewById(R.id.loginEdt);
        loginButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (R.id.loginBtn == v.getId()) {
            loginEdit = (EditText) findViewById(R.id.loginEdt);

            //stock name in SharedPref
            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.file), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("name", loginEdit.getText().toString());
            editor.apply();

            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
