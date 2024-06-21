package com.example.mobileappdevfinalproject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameSurface extends SurfaceView implements Runnable, View.OnTouchListener{
    // Almost all of these variables are required anytime you are implementing a SurfaceView
    Thread gameThread;  // required for functionality
    SurfaceHolder holder; // required for functionality
    volatile boolean running = false; // variable shared amongst threads; required for functionality
    Bitmap ball, background, movingbg;
    int yPos = 200;
    double yVelocity = 5.0;
    double gravity = -0.4;
    double direct = 1.0;
    String mode = "ufo";
    Paint paintProperty; // required for functionality
    HoldThread holdThread;
    int image;
    double bgx = 0;
    ArrayList<pillar> pillars = new ArrayList<pillar>();
    long timeStart = System.currentTimeMillis();
    long timeSurvived;
    boolean spawnLimit = false;
    int frequency = 20;
    int portalCount = 0;
    ArrayList<portal> portals = new ArrayList<portal>();
    boolean immunity = false;
    int immuneTime;
    int maxImmuneTime = 100;
    Canvas canvas = null;
    int allMinSpace = 500;
    int allMaxSpace = 700;
    int allSpeed = 7;
    String username = "test";

    static String set = "noob";
    MediaPlayer bgMusic;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, String> playerData = new HashMap<>();



    public GameSurface(Context context, String u) {
        super(context);
        holder = getHolder();
        setOnTouchListener(this);

        username = u;
        updatePlayerData();
        bgMusic = MediaPlayer.create(GameSurface.this.getContext(), R.raw.retray);
        bgMusic.setLooping(true);
        bgMusic.start();




        background = BitmapFactory.decodeResource(getResources(), R.drawable.retraybg);
        image = R.drawable.playerufo;
        ball = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), image),
                200, 200, false);

        movingbg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.retraybg),
                2800, 1600, false);


        paintProperty = new Paint();


    }

    @Override
    public void run() {
        Drawable d = getResources().getDrawable(R.drawable.retraybg, null);

        // loop should run as long as running == true
        while(true) {
            while (running) {
                // if holder is null or invalid, exit loop
                if (!holder.getSurface().isValid())
                    continue;

                canvas = holder.lockCanvas(null);

                d.setBounds(getLeft(), getTop(), getRight(), getBottom());
                d.draw(canvas);

                //playerData.put("username", username);
                //playerData.put("score", 0);






                canvas.drawBitmap(movingbg, (int) bgx, 0, null);
                if (bgx > -1750) {
                    bgx -= 0.4;
                } else {
                    bgx = 0;
                }

                paintProperty.setColor(Color.WHITE);
                paintProperty.setTextSize(100);
                timeSurvived = (System.currentTimeMillis() - timeStart)/100;
                canvas.drawText(""+timeSurvived/10, 50, 150, paintProperty);

                paintProperty.setColor(Color.WHITE);
                paintProperty.setTextSize(50);
                canvas.drawText("Top Score: "+playerData.get("score"), 650, 50,paintProperty);


                canvas.drawBitmap(ball, 100, yPos, null);


                if (mode.equals("ufo") || mode.equals("wave")) {
                    if (yPos + (int) yVelocity * direct <= 1350 && yPos + (int) yVelocity * direct >= 0) {
                        yPos -= (int) yVelocity * direct;
                    } else if (yPos + (int) yVelocity * direct < 0) {
                        yPos = 1340;
                    } else if (yPos + (int) yVelocity * direct > 1350) {
                        yPos = 10;
                    }
                } else {
                    if (yPos + (int) yVelocity <= 1350 && yPos + (int) yVelocity >= 0) {
                        yPos -= (int) yVelocity;
                    } else if (yPos + (int) yVelocity < 0) {
                        yPos = 1340;
                    } else if (yPos + (int) yVelocity > 1350) {
                        yPos = 10;
                    }
                }

                if (mode.equals("ufo")) {
                    updateImage();
                    image = R.drawable.playerufo;
                    if (yVelocity + gravity >= -10.0) {
                        yVelocity += gravity;
                    } else {
                        yVelocity = -10.0;
                    }
                }
                if (mode.equals("swing")) {

                    updateImage();

                    if (yVelocity + gravity * direct >= -10.0 && yVelocity + gravity * direct <= 10.0) {
                        yVelocity += gravity * direct;
                    } else if (yVelocity + gravity * direct <= -10.0) {
                        yVelocity = -10.0;
                    } else if (yVelocity + gravity * direct >= 10.0) {
                        yVelocity = 10.0;
                    }
                }
                if (mode.equals("wave")) {
                    updateImage();
                    yVelocity = -10;
                }

                int elapsedTime = (int)((System.currentTimeMillis() - timeStart)/100);
                if (elapsedTime % frequency == 0 && spawnLimit == false) {
                    pillars.add(new pillar(allSpeed, allMinSpace, allMaxSpace));
                    pillars.get(pillars.size() - 1).setTopPillar(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.laserpillar), 150, (pillars.get(pillars.size() - 1)).getTopLength(), false));
                    pillars.get(pillars.size() - 1).setBottomPillar(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.laserpillar), 150, (pillars.get(pillars.size() - 1)).getBottomLength(), false));
                    spawnLimit = true;

                    if (portalCount == 5) {
                        String portalType = mode;
                        int portalSprite;
                        do {
                            int rand = (int) (Math.random() * 5);
                            switch (rand) {
                                case 0:
                                    portalType = "ufo";
                                    portalSprite = R.drawable.ufoportal;
                                    break;
                                case 1:
                                    portalType = "swing";
                                    portalSprite = R.drawable.swingportal;
                                    break;
                                case 2:
                                    portalType = "wave";
                                    portalSprite = R.drawable.waveportal;
                                    break;
                                case 3:
                                    portalType = "normalGravity";
                                    portalSprite = R.drawable.upsideup;
                                    break;
                                case 4:
                                    portalType = "reversedGravity";
                                    portalSprite = R.drawable.updisedown;
                                    break;
                                default:
                                    portalType = "reversedGravity";
                                    portalSprite = R.drawable.updisedown;
                            }
                        } while (portalType == mode);


                        portals.add(new portal(portalType, pillars.get(pillars.size() - 1).getTop(), Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), portalSprite), 150, pillars.get(pillars.size() - 1).getSpace(), false), pillars.get(pillars.size() - 1).getX(), pillars.get(pillars.size() - 1).getSpeed(), pillars.get(pillars.size() - 1).getSpace()));
                        portalCount = 0;
                        Log.d("new portal", "" + portals.get(portals.size() - 1).getSprite());
                    } else {
                        portalCount++;
                    }
                } else if (elapsedTime % frequency == 1 && spawnLimit == true) {
                    spawnLimit = false;
                }
                Log.d("time", "" + elapsedTime);
                Log.d("# of pillars", "" + pillars.size());


                for (int i = 0; i < pillars.size(); i++) {
                    canvas.drawBitmap(pillars.get(i).getTopPillar(), pillars.get(i).getX(), 0, null);
                    canvas.drawBitmap(pillars.get(i).getBottomPillar(), pillars.get(i).getX(), pillars.get(i).getBottom(), null);
                    pillars.get(i).updatePillar();

                }
                for (int i = 0; i < portals.size(); i++) {
                    canvas.drawBitmap(portals.get(i).getSprite(), portals.get(i).getPx(), portals.get(i).getPy(), null);
                    portals.get(i).updatePortal();
                }

                for (int i = 0; i < portals.size(); i++) {
                    if (portals.get(i).ifEntered(100, yPos)) {
                        if (mode.equals("wave") && holdThread != null) {
                            holdThread.stopHold();
                            holdThread = null;
                        }

                        if(portals.get(i).getType().equals("wave") ||
                                portals.get(i).getType().equals("ufo") ||
                                portals.get(i).getType().equals("swing"))
                        {
                            mode = portals.get(i).getType();
                        }
                        else if (portals.get(i).getType().equals("normalGravity"))
                        {
                            direct = 1;
                        }
                        else if (portals.get(i).getType().equals("reversedGravity"))
                        {
                            direct = -1;
                        }

                        if(immunity == false)
                        {
                            immunity = true;
                        }
                    }
                }

                if(immunity == true && immuneTime < maxImmuneTime)
                {
                    immuneTime++;
                }
                else if(immunity == true && immuneTime >= maxImmuneTime)
                {
                    immunity = false;
                    immuneTime=0;
                }

                for (int i = 0; i < pillars.size(); i++) {
                    if (pillars.get(i).ifCrashed(100, yPos) && immunity == false) {
                        Log.d("collison occured", "stop game");
                        setDeathScreen();
                        running = false;


                        String oldScore = playerData.get("score");
                        if(timeSurvived > Integer.parseInt(oldScore)) {
                            playerData.put("score", ""+timeSurvived);
                        }
                        db.collection("Players").document(username).set(playerData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("database", "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("database", "Error writing document", e);
                                    }
                                });

                        updatePlayerData();

                    }
                }


                for (int i = pillars.size() - 1; i >= 0; i--) {
                    if (pillars.get(i).getX() <= -150) {
                        pillars.remove(i);
                    }

                }
                for (int i = portals.size() - 1; i >= 0; i--) {
                    if (portals.get(i).getPx() <= -150) {
                        portals.remove(i);
                    }
                }


                Log.d("Current Acceleration", "" + gravity * direct);
                Log.d("Current Velocity", "" + yVelocity);
                Log.d("Current Y pos", "" + yPos);


                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void updatePlayerData()
    {
        DocumentReference docRef = db.collection("Players").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("data retrieval", "DocumentSnapshot data: " + document.getData());
                        playerData.put("username", ""+document.get("username"));
                        playerData.put("score", ""+document.get("score"));
                    }
                    else {
                        Log.d("data retrieval", "No such document creating new");
                        playerData.put("username", username);
                        playerData.put("score", ""+0);

                    }
                }
                else {
                    Log.d("data retrieval", "get failed with ", task.getException());
                    playerData.put("username", username);
                    playerData.put("score", ""+0);
                }
            }
        });
    }

    public void setDeathScreen() {
        //canvas.drawText("Time Survived: " + (score/10.0), 10, 75, paintProperty);

        paintProperty.setColor(Color.WHITE);
        paintProperty.setTextSize(70);
        canvas.drawText("You Died!", 20, 595, paintProperty);
        canvas.drawText("Time Survived: "+(timeSurvived/10.0)+" Seconds", 20, 685, paintProperty);
        canvas.drawText("_________________________", 20, 715, paintProperty);
        canvas.drawText("Score: "+(timeSurvived), 20, 805, paintProperty);
    }

    public void resume(){
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void pause(){
        running = false;
        while(true){
            try{
                gameThread.join();
            }catch (InterruptedException e){
                e.printStackTrace();}
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.d("click occured", mode);

        if((mode.equals("ufo") || mode.equals("swing")) && event.getAction() == MotionEvent.ACTION_DOWN) {
            clickMode();
        }

        if(mode.equals("wave")) {
            //holdMode();

            int action = event.getAction();

            switch(action){
                case MotionEvent.ACTION_DOWN:
                    if (holdThread == null){
                        holdThread = new HoldThread();
                        holdThread.start();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (holdThread != null){
                        holdThread.stopHold();
                        holdThread = null;
                    }
                    break;
            }
        }
        if(running == false)
        {
            for (int i = pillars.size() - 1; i >= 0; i--) {
                pillars.remove(i);
            }
            for (int i = portals.size() - 1; i >= 0; i--) {
                portals.remove(i);
            }

            if (holdThread != null) {
                holdThread.stopHold();
                holdThread = null;
            }
            yPos = 200;
            yVelocity = 5.0;
            gravity = -0.4;
            direct = 1.0;
            mode = "ufo";
            running = true;
            timeStart = System.currentTimeMillis();
            timeSurvived = 0;
        }
        return true;
    }

    public void holdMode()
    {
        yVelocity=10.5;
    }

    public void clickMode() {
        if(mode.equals("ufo"))
        {
            yVelocity = 10.5;
        }
        if(mode.equals("swing"))
        {
            direct *=-1;
        }

    }

    public void updateImage()
    {
        if(mode.equals("swing")) {
            if (gravity* direct > 0) {
                if(set.equals("god") && Integer.parseInt(playerData.get("score")) >= 1000)
                {
                    image = R.drawable.swingu;
                }
                else if(set.equals("pro") && Integer.parseInt(playerData.get("score")) >= 500)
                {
                    image = R.drawable.proswingup;
                }
                else
                {
                    image = R.drawable.noobswingup;
                }
            } else if (gravity* direct < 0) {
                if(set.equals("god") && Integer.parseInt(playerData.get("score")) >= 1000)
                {
                    image = R.drawable.swingd;
                }
                else if(set.equals("pro") && Integer.parseInt(playerData.get("score")) >= 500)
                {
                    image = R.drawable.proswingdown;
                }
                else
                {
                    image = R.drawable.noobswingdown;
                }

            }
        }
        if(mode.equals("wave"))
        {

            if(holdThread != null)
            {
                if(direct == 1)
                {
                    if(set.equals("god") && Integer.parseInt(playerData.get("score")) >= 1000)
                    {
                        image = R.drawable.waveu;
                    }
                    else if(set.equals("pro") && Integer.parseInt(playerData.get("score")) >= 500)
                    {
                        image = R.drawable.prowaveup;
                    }
                    else
                    {
                        image = R.drawable.noobwaveup;
                    }
                }
                else
                {
                    if(set.equals("god") && Integer.parseInt(playerData.get("score")) >= 1000)
                    {
                        image = R.drawable.waved;
                    }
                    else if(set.equals("pro") && Integer.parseInt(playerData.get("score")) >= 500)
                    {
                        image = R.drawable.prowavedown;
                    }
                    else
                    {
                        image = R.drawable.noobwavedown;
                    }
                }
            }
            else
            {
                if(direct == 1)
                {
                    if(set.equals("god") && Integer.parseInt(playerData.get("score")) >= 1000)
                    {
                        image = R.drawable.waved;
                    }
                    else if(set.equals("pro") && Integer.parseInt(playerData.get("score")) >= 500)
                    {
                        image = R.drawable.prowavedown;
                    }
                    else
                    {
                        image = R.drawable.noobwavedown;
                    }
                }
                else
                {
                    if(set.equals("god") && Integer.parseInt(playerData.get("score")) >= 1000)
                    {
                        image = R.drawable.waveu;
                    }
                    else if(set.equals("pro") && Integer.parseInt(playerData.get("score")) >= 500)
                    {
                        image = R.drawable.prowaveup;
                    }
                    else
                    {
                        image = R.drawable.noobwaveup;
                    }
                }
            }
        }
        if(mode.equals("ufo"))
        {
            if(direct == 1)
            {

                if(set.equals("god") && Integer.parseInt(playerData.get("score")) >= 1000)
                {
                    image = R.drawable.playerufo;
                }
                else if(set.equals("pro") && Integer.parseInt(playerData.get("score")) >= 500)
                {
                    image = R.drawable.proufoup;
                }
                else
                {
                    image = R.drawable.noobufoup;
                }
            }
            else
            {
                if(set.equals("god") && Integer.parseInt(playerData.get("score")) >= 1000)
                {
                    image = R.drawable.playerufod;
                }
                else if(set.equals("pro") && Integer.parseInt(playerData.get("score")) >= 500)
                {
                    image = R.drawable.proufodown;
                }
                else
                {
                    image = R.drawable.noobufodown;
                }
            }
        }
        ball = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), image),
                200, 200, false);
    }
    public static void setIcons(String s) {
        set = s;
    }


    public class HoldThread extends Thread{

        private volatile boolean stopped = false;

        @Override
        public void run(){
            super.run();
            while(!stopped){
                holdMode();
            }
        }

        public boolean isStopped() {
            return stopped;
        }

        public void stopHold(){
            stopped = true;
            yVelocity=-10*direct;
        }

    }
}
