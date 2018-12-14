package com.example.abdul.testground.Organizations;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.abdul.testground.Database.DBProvider;
import com.example.abdul.testground.MainActivity;
import com.example.abdul.testground.Members.MemAdapter;
import com.example.abdul.testground.Members.MemListFragment;
import com.example.abdul.testground.R;

import java.util.HashMap;

public class OrgDetailFragment extends Fragment {
    LayoutInflater inflater;
    ViewGroup container;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        v = inflater.inflate(R.layout.fragment_orgdetail, container, false);
        return v;
    }

    public void joinbtn(final DBProvider DBP , final int id){
        //handle join button 4 states
        final Button join = (Button) v.findViewById(R.id.orgbtn_join);
        DBP.open();
        if (DBP.aMemberIn(MainActivity.usr_id, id)) {
            //Leave organization button
            join.setText("Leave");
            join.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBP.open();
                    DBP.deleteMember(MainActivity.usr_id, id);
                    DBP.close();
                    joinbtn(DBP,id);
                }
            });
        }else if(DBP.aInviteFromTo(MainActivity.usr_id, id,0)){
            //Cancel request
            join.setText("Cancel");
            join.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBP.open();
                    DBP.removeInvite(MainActivity.usr_id, id, 0);
                    DBP.close();
                    joinbtn(DBP,id);
                }
            });
        }else if(DBP.aInviteFromTo(id , MainActivity.usr_id,1)) {
            //Accept Invite
            join.setText("Accept");
            join.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBP.open();
                    DBP.removeInvite(id, MainActivity.usr_id, 1);
                    DBP.addMember(MainActivity.usr_id, id);
                    DBP.close();
                    joinbtn(DBP,id);
                }
            });
        }else{
            //Apply to join
            join.setText("Apply");
            join.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBP.open();
                    DBP.insertInvite(MainActivity.usr_id, id, 0);
                    DBP.close();
                    joinbtn(DBP,id);
                }
            });
        }
        DBP.close();
    }

    public void setVales(final Context con, final int id) {
        //getData
        final DBProvider DBP = new DBProvider(con);
        DBP.open();
        final Cursor cursor = DBP.getOrgDetails(id);
        DBP.close();

        //get item elements
        ImageView icon = (ImageView) v.findViewById(R.id.orgIcon);

        TextView name = (TextView) v.findViewById(R.id.orgName);
        TextView cate = (TextView) v.findViewById(R.id.orgCategory);
        TextView date = (TextView) v.findViewById(R.id.orgDate);
        TextView desc = (TextView) v.findViewById(R.id.orgDesc);

        View background = v.findViewById(R.id.orgBackground);

        Button members = (Button) v.findViewById(R.id.orgbtn_members);
        ImageButton edit = (ImageButton) v.findViewById(R.id.orgbtn_edit);

        //show data
        name.setText(cursor.getString(cursor.getColumnIndex("org_name")));
        cate.setText(cursor.getString(cursor.getColumnIndex("org_cat")));
        long timest =  cursor.getLong(cursor.getColumnIndex("org_date"));
        String Date = DateFormat.format("dd/MM/yyyy", timest).toString();
        date.setText("since : " + Date);
        desc.setText(cursor.getString(cursor.getColumnIndex("org_desc")));
        String iconName = cursor.getString(cursor.getColumnIndex("org_icon"));
        String orgcolor = "color"+ cursor.getString(cursor.getColumnIndex("org_color"));
        int resID = con.getResources().getIdentifier(iconName, "drawable", con.getPackageName());
        icon.setImageResource(resID);
        background.setBackgroundResource(con.getResources().getIdentifier(orgcolor, "color", con.getPackageName()));

        joinbtn(DBP,id);

        //show members button
        members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                MemListFragment fra = new MemListFragment();
                transaction.replace(R.id.contentFragment, fra);
                //transaction.addToBackStack(null);

                transaction.commit();
                fm.executePendingTransactions();
                DBP.open();
                Cursor cursor1 = DBP.getOrgMembers(id);
                DBP.close();
                MemAdapter mem = new MemAdapter(con, cursor1, id, 0);
                fra.setMemAdaptor(mem);
                fra.setBtnAction(con,id);
            }
        });


        //check for user privilege to edit this organization data
        DBP.open();
        HashMap<String,Boolean> pri = DBP.getUserPrivileges(id);
        DBP.close();

        if(!pri.get("invite")) {
            edit.setVisibility(View.GONE);
            return;
        }

        //set edit btn
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddOrgFragment newOrg = new AddOrgFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();

                transaction.replace(R.id.contentFragment, newOrg);
                //transaction.addToBackStack(null);

                transaction.commit();
                fm.executePendingTransactions();
                newOrg.setValues(id);
            }
        });
    }


}