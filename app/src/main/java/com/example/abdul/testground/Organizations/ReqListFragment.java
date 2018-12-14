package com.example.abdul.testground.Organizations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.abdul.testground.R;

public class ReqListFragment extends Fragment {
    ListView listViewInv;
    LayoutInflater inflater;
    ViewGroup container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        View v = inflater.inflate(R.layout.fragment_req, container, false);
        listViewInv = (ListView) v.findViewById(R.id.listview_req);
        return v;
    }

    public void setReqAdaptor(ReqAdapter adaptor) {
        listViewInv.setAdapter(adaptor);
    }
}