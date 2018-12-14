package com.example.abdul.testground.Organizations;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.abdul.testground.Database.DBHelper;
import com.example.abdul.testground.Database.DBProvider;
import com.example.abdul.testground.MainActivity;
import com.example.abdul.testground.R;

public class OrgListFragment extends Fragment {
    ListView listViewOrg;
    ListView listViewSearchOrg;
    LayoutInflater inflater;
    ViewGroup container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        return toOrg();
    }


    public View toOrg() {
        View v = inflater.inflate(R.layout.fragment_org, container, false);

        listViewOrg = (ListView) v.findViewById(R.id.listview_org);
        listViewSearchOrg = (ListView) v.findViewById(R.id.listview_rorg);
        final EditText searchOrg = (EditText) v.findViewById(R.id.search_field);
        View myLayout = v.findViewById(R.id.layout_my);
        View moreLayout = v.findViewById(R.id.layout_more);
        final ImageView myArrow = (ImageView) v.findViewById(R.id.expandArrow_my);
        final ImageView moreArrow = (ImageView) v.findViewById(R.id.expandArrow_more);
        final Button addButton = (Button) v.findViewById(R.id.btn_add_org) ;
        final View searchLayout = v.findViewById(R.id.search_area) ;

        myLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addButton.getVisibility() == View.GONE){
                    addButton.setVisibility(View.VISIBLE);
                    listViewOrg.setVisibility(View.VISIBLE);
                    myArrow.setRotation(90);
                }else{
                    addButton.setVisibility(View.GONE);
                    listViewOrg.setVisibility(View.GONE);
                    myArrow.setRotation(0);
                }
            }
        });

        moreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchLayout.getVisibility() == View.GONE){
                    searchLayout.setVisibility(View.VISIBLE);
                    listViewSearchOrg.setVisibility(View.VISIBLE);
                    moreArrow.setRotation(90);
                }else{
                    searchLayout.setVisibility(View.GONE);
                    listViewSearchOrg.setVisibility(View.GONE);
                    moreArrow.setRotation(0);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddOrgFragment newOrg = new AddOrgFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();

                transaction.replace(R.id.contentFragment, newOrg);
                //transaction.addToBackStack(null);

                transaction.commit();
                fm.executePendingTransactions();
            }
        });

        searchOrg.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                DBProvider DBP = new DBProvider(getContext());
                DBP.open();
                Cursor data;
                if (searchOrg.length() == 0)
                    data = DBP.getUserROrg(MainActivity.usr_id);
                else
                    data = DBP.getSearchOrg(searchOrg.getText().toString());
                DBP.close();
                OrgAdapter org = new OrgAdapter(getContext(), data, 0);
                setSearchOrgAdaptor(org);
            }

        });
        return v;
    }

    public void setOrgAdaptor(OrgAdapter adaptor) {
        listViewOrg.setAdapter(adaptor);
    }

    public void setSearchOrgAdaptor(OrgAdapter adaptor) {
        listViewSearchOrg.setAdapter(adaptor);
    }
}