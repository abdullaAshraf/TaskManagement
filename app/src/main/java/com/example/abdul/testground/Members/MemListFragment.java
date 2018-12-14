package com.example.abdul.testground.Members;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.example.abdul.testground.Database.DBProvider;
import com.example.abdul.testground.MainActivity;
import com.example.abdul.testground.Organizations.InvAdapter;
import com.example.abdul.testground.Organizations.InvListFragment;
import com.example.abdul.testground.Organizations.OrgAdapter;
import com.example.abdul.testground.Organizations.ReqAdapter;
import com.example.abdul.testground.Organizations.ReqListFragment;
import com.example.abdul.testground.R;

import java.util.HashMap;

import static com.example.abdul.testground.MainActivity.usr_id;

public class MemListFragment extends Fragment {
    ListView listViewMem;
    LayoutInflater inflater;
    ViewGroup container;
    FloatingActionButton req;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        return toMem();
    }

    public View toMem() {
        View v = inflater.inflate(R.layout.fragment_mem, container, false);
        listViewMem = (ListView) v.findViewById(R.id.listview_mem);
        req = (FloatingActionButton) v.findViewById(R.id.req);
        return v;
    }

    public void setBtnAction(final Context con, final int id) {
        final DBProvider DBP = new DBProvider(con);

        DBP.open();
        HashMap<String,Boolean> pri = DBP.getUserPrivileges(id);
        DBP.close();

        if(!pri.get("invite")) {
            req.hide();
            return;
        }

        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReqListFragment fra = new ReqListFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.contentFragment, fra);
                //transaction.addToBackStack(null);
                transaction.commit();
                fm.executePendingTransactions();

                DBP.open();
                Cursor data = DBP.getOrgRequests(id);
                DBP.close();

                ReqAdapter adapter = new ReqAdapter(con, data, id, 0);
                fra.setReqAdaptor(adapter);
            }
        });
    }

    public void setMemAdaptor(MemAdapter tsk) {
        listViewMem.setAdapter(tsk);
    }
}