package com.example.mobileappdevfinalproject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class BotFragment extends Fragment {

    GameSurface gameSurface;
    String user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_bot, container, false);

        if (ContextCompat.checkSelfPermission(BotFragment.this.getContext(), android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(BotFragment.this.getContext(), android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BotFragment.this.getActivity(), new String[]{android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_NETWORK_STATE}, 100);
        }

        MainActivity activity = (MainActivity) getActivity();
        user = activity.getUsername();
        Log.d("data to frag test", user);

        gameSurface = new GameSurface(BotFragment.this.getContext(), user);
        return gameSurface;
    }
    @Override
    public void onResume() {
        super.onResume();
        gameSurface.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        gameSurface.pause();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}
