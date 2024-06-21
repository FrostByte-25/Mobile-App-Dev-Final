package com.example.mobileappdevfinalproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomAdapter extends ArrayAdapter<String> {
    List list;
    ArrayList<Map<String, Object>> players;

    Context context;
    int xmlResource;
    public CustomAdapter(Context context, int resource, ArrayList<String> objects, ArrayList<Map<String, Object>> p) {
        super(context, resource, objects);
        xmlResource = resource;
        list = objects;
        this.context = context;
        players = p;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(xmlResource, null);

        TextView name = adapterLayout.findViewById(R.id.textViewUser);
        TextView score = adapterLayout.findViewById(R.id.textViewScore);

        name.setText((String)(players.get(position).get("username")));
        score.setText(""+(String)(players.get(position).get("score"))+" pts");

        return adapterLayout;
    }
}
