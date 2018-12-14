package com.example.abdul.testground.Organizations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.example.abdul.testground.R;

public class InvListFragment extends Fragment {
    ListView listViewInv;
    LayoutInflater inflater;
    ViewGroup container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        View v = inflater.inflate(R.layout.fragment_inv, container, false);
        listViewInv = (ListView) v.findViewById(R.id.listview_inv);
        return v;
    }

    public void setInvAdaptor(InvAdapter adaptor) {
        listViewInv.setAdapter(adaptor);
    }
}