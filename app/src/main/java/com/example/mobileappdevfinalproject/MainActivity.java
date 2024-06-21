package com.example.mobileappdevfinalproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Main activity that manages two fragments: TopFragment and BotFragment.
 * It includes functionality to replace the TopFragment with another BotFragment on button click.
 */
public class MainActivity extends FragmentActivity{

    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String givenUser;
    int currentScore;
    RadioGroup radioGroup;
    RadioButton noob,pro,dev;
    Map<String, String> playerData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        givenUser = extras.getString("given");
        Log.d("intent test", givenUser);

        radioGroup = findViewById(R.id.radioGroup);
        noob = findViewById(R.id.radioButtonNoob);
        pro = findViewById(R.id.radioButtonPro);
        dev = findViewById(R.id.radioButtonDev);

        noob.setChecked(true);
        pro.setClickable(true);
        dev.setClickable(true);




        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButtonNoob)
                {
                    GameSurface.setIcons("noob");
                }
                if(checkedId == R.id.radioButtonDev)
                {
                    GameSurface.setIcons("god");
                }
                if(checkedId == R.id.radioButtonPro)
                {
                    GameSurface.setIcons("pro");
                }
                //checkSetsUnlocked();
            }
        });



        // Get the FragmentManager and start a new FragmentTransaction
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        // Create instances of TopFragment and BotFragment
        TopFragment topFragment = new TopFragment();
        BotFragment botFragment = new BotFragment();

        // Add the fragments to their respective containers
        fragmentTransaction.add(R.id.layout_top, topFragment);
        fragmentTransaction.add(R.id.layout_bot, botFragment);
        fragmentTransaction.commit();




    }

    public String getUsername() {
        return givenUser;
    }

    public void checkSetsUnlocked()
    {

        DocumentReference docRef = db.collection("Players").document(givenUser);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("data retrieval in main", "DocumentSnapshot data: " + document.getData());
                        playerData.put("username", ""+document.get("username"));
                        playerData.put("score", ""+document.get("score"));
                    }
                    else {
                        Log.d("data retrieval in main", "No such document creating new");
                        playerData.put("username", givenUser);
                        playerData.put("score", ""+0);

                    }
                }
                else {
                    Log.d("data retrieval in main", "get failed with ", task.getException());
                    playerData.put("username", givenUser);
                    playerData.put("score", ""+0);
                }
            }
        });

        String score = playerData.get("score");
        if(Integer.parseInt(score) >= 100)
        {
            pro.setClickable(true);
        }
        if(Integer.parseInt(score) >= 1000)
        {
            dev.setClickable(true);
        }


    }

}