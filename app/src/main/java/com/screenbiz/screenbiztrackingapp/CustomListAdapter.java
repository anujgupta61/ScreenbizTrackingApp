package com.screenbiz.screenbiztrackingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> history_title = new ArrayList<>() ;
    private ArrayList<String> distance = new ArrayList<>() ;
    private ArrayList<String> time = new ArrayList<>() ;

    public CustomListAdapter(Activity context, ArrayList<String> history_title , ArrayList<String> distance , ArrayList<String> time) {
        super(context, R.layout.list_item_history , history_title);
        this . context=context;
        this . history_title = history_title ;
        this . distance = distance ;
        this . time = time ;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item_history , null , true);
        TextView txt_history = (TextView) rowView.findViewById(R.id.history_title);
        TextView txt_distance = (TextView) rowView.findViewById(R.id.distance);
        TextView txt_time = (TextView) rowView . findViewById(R.id.time) ;
        txt_history . setText(history_title . get(position)) ;
        txt_distance . setText(distance . get(position)) ;
        txt_time . setText(time . get(position)) ;
        return rowView ;
    }
}
