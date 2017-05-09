package com.screenbiz.screenbiztrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class HistoryFragment  extends Fragment {
    private ArrayList<String> ids = new ArrayList<>() ;
    private ArrayList<String> dates = new ArrayList<>() ;
    private ArrayList<String> dists = new ArrayList<>() ;
    private ArrayList<String> times = new ArrayList<>() ;
    private int t ;

    public HistoryFragment() {
        Bundle type = getArguments() ;
        if(type != null)
            t = type . getInt("type") ;
        else
            Log.v("Tracking" , "Null type") ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ids . add("SB") ;
        ids . add("SB") ;
        ids . add("SB") ;
        ids . add("SB") ;
        ids . add("SB") ;
        dates . add("Date - 30/04/2017") ;
        dates . add("Date - 30/04/2017") ;
        dates . add("Date - 30/04/2017") ;
        dates . add("Date - 30/04/2017") ;
        dates . add("Date - 30/04/2017") ;
        dists . add("Distance - 15 kms") ;
        dists . add("Distance - 15 kms") ;
        dists . add("Distance - 15 kms") ;
        dists . add("Distance - 15 kms") ;
        dists . add("Distance - 15 kms") ;
        times . add("Time - 5 hrs") ;
        times . add("Time - 5 hrs") ;
        times . add("Time - 5 hrs") ;
        times . add("Time - 5 hrs") ;
        times . add("Time - 5 hrs") ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history , container, false) ;
        ArrayList<String> history_title = new ArrayList<>() ;
        if(t == 1)
            history_title = dates ;
        else
            history_title = ids ;
        CustomListAdapter adapter = new CustomListAdapter(getActivity() , history_title , dists , times) ;
        ListView listview = (ListView) rootView.findViewById(R.id.listview_history) ;
        try {
            listview.setAdapter(adapter);
        } catch(Exception ex) {
            ex . printStackTrace() ;
        }
        listview . setOnItemClickListener(new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> adapterView , View view , int pos , long l) {
                Toast.makeText(getContext() , "Item clicked ..." , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity() , DetailedHistory.class) ;
                startActivity(intent);
            }
        } ) ;
        final SwipeRefreshLayout mySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh) ;
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mySwipeRefreshLayout . setRefreshing(false) ;
                    }
                }
        );
        return rootView ;
    }
}
