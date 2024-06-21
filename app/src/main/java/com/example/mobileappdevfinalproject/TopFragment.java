package com.example.mobileappdevfinalproject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class TopFragment extends Fragment {

    ListView listView;
    ArrayList<String> users;
    ArrayList<Map<String, Object>> list;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_top, container, false);

        listView = fragmentView.findViewById(R.id.list_id);

        list = new ArrayList<Map<String, Object>>();

        db.collection("Players")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("leaderboard data retrieval", document.getId() + " => " + document.getData());

                                list.add(document.getData());
                                Log.d("testing leaderboard data retrieval", ""+list.toString());

                                //Log.d("testing leaderboard data retrieval", ""+list.get(list.size()-1).get("score"));
                                //Log.d("testing leaderboard data retrieval", ""+list.get(list.size()-1).get("username"));

                            }
                            sortAndFilter();
                            Collections.reverse(list);

                            users = new ArrayList<String>();
                            for(int i = 0; i < list.size(); i++)
                            {
                                users.add((String) (list.get(i).get("username")));
                            }
                            Log.d("test", ""+users.toString());
                            Log.d("testing leaderboard data retrieval", ""+list.toString());
                            CustomAdapter adapter = new CustomAdapter(TopFragment.this.getContext(), R.layout.adapter_layout, users, list);
                            listView.setAdapter(adapter);
                        } else {
                            Log.d("leaderboard data retrieval", "Error getting documents: ", task.getException());
                        }
                    }
                });

        return fragmentView;
    }

    public void sortAndFilter()
    {
        Map<String, Object> temp;
        if (list.size()>=1)
        {
            for (int x=0; x<list.size()-1; x++) // bubble sort outer loop
            {
                for (int i=0; i < list.size()-x-1; i++) {
                    if (compareRanks(list.get(i), list.get(i+1)) > 0)
                    {
                        temp = list.get(i);
                        list.set(i,list.get(i+1) );
                        list.set(i+1, temp);
                    }

                }
            }
            while(list.size() > 10)
            {
                list.remove(0);
            }
        }
        Log.d("testing leaderboard data sort", ""+list.toString());
    }

    public int compareRanks(Map<String, Object> p1, Map<String, Object> p2)
    {
        int p1Score = Integer.parseInt(""+p1.get("score"));
        int p2Score = Integer.parseInt(""+p2.get("score"));

        return p1Score-p2Score;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
