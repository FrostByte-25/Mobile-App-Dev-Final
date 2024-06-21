package com.example.mobileappdevfinalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class EnterUserActivity extends AppCompatActivity {

    EditText enterUser;
    Button pressStart;
    ActivityResultLauncher activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_user_layout);

        enterUser = findViewById(R.id.editTextEnterUsername);
        pressStart = findViewById(R.id.buttonPressStart);

        pressStart.setBackgroundColor(Color.GREEN);

        pressStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(enterUser.getText().toString().equals(""))) {
                    Intent intentToLoad = new Intent(EnterUserActivity.this, MainActivity.class);
                    intentToLoad.putExtra("given", enterUser.getText().toString());
                    activityResultLauncher.launch(intentToLoad);
                }
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {

                }
            }
        });


    }
}
