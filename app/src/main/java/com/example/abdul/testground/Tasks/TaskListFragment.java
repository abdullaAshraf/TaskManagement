package com.example.abdul.testground.Tasks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.abdul.testground.R;

public class TaskListFragment extends Fragment {
    ExpandableListView listView;
    LayoutInflater inflater;
    ViewGroup container;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        return toTsk();
    }

    public View toTsk(){
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ExpandableListView) v.findViewById(R.id.listview_task);
        return v;
    }

    public void setListAdaptor(ExpandableTaskAdaptor tsk) {
        listView.setAdapter(tsk);
    }

}