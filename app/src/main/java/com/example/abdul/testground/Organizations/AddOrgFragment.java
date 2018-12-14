package com.example.abdul.testground.Organizations;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.abdul.testground.Database.DBProvider;
import com.example.abdul.testground.MainActivity;
import com.example.abdul.testground.Members.MemAdapter;
import com.example.abdul.testground.Members.MemListFragment;
import com.example.abdul.testground.R;

import java.util.ArrayList;
import java.util.List;

public class AddOrgFragment extends Fragment {
    LayoutInflater inflater;
    ViewGroup container;
    View v;
    static public int selectediconp = 8;
    static public ImageView selectedicon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        v = inflater.inflate(R.layout.add_organization, container, false);
        setChoices();
        return v;
    }

    public void setChoices() {
        final Context con = getActivity();
        //getData
        final DBProvider DBP = new DBProvider(con);

        //set icons list
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(con, LinearLayoutManager.HORIZONTAL, false);

        RecyclerView iconsList = (RecyclerView) v.findViewById(R.id.icons_list);
        iconsList.setLayoutManager(layoutManager);
        iconsList.setAdapter(new itemsAdapter(con, 15));

        //get item elements
        final EditText name = (EditText) v.findViewById(R.id.addOrg_name);
        final EditText desc = (EditText) v.findViewById(R.id.addOrg_desc);
        final Spinner cate = (Spinner) v.findViewById(R.id.addOrg_cate);
        final Spinner color = (Spinner) v.findViewById(R.id.addOrg_color);

        Button btnAccept = (Button) v.findViewById(R.id.addOrg_btn_accept);
        Button btnCancel = (Button) v.findViewById(R.id.addOrg_btn_cancel);

        selectedicon = (ImageView) v.findViewById(R.id.addOrg_icon);

        //initialize sample data
        List<String> category = new ArrayList<String>();
        category.add("Technology");
        category.add("Sports");
        category.add("Cars");
        category.add("Study");
        category.add("Charity");
        category.add("Books");
        category.add("Other");
        List<String> colors = new ArrayList<String>();
        colors.add("Red");
        colors.add("Blue");
        colors.add("Green");
        colors.add("Orange");
        colors.add("Purple");
        colors.add("Yellow");
        colors.add("Grey");
        colors.add("Indigo");

        //add data to spinners
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(con,
                android.R.layout.simple_spinner_item, category);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cate.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(con,
                android.R.layout.simple_spinner_item, colors);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        color.setAdapter(dataAdapter2);

        // limit max row count for description
        desc.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                // if enter is pressed start calculating
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_UP) {

                    // get EditText text
                    String text = ((EditText) v).getText().toString();

                    // find how many rows it cointains
                    int editTextRowCount = text.split("\\n").length;

                    // user has input more than limited - lets do something
                    // about that
                    if (editTextRowCount >= 20) {

                        // find the last break
                        int lastBreakIndex = text.lastIndexOf("\n");

                        // compose new text
                        String newText = text.substring(0, lastBreakIndex);

                        // add new text - delete old one and append new one
                        // (append because I want the cursor to be at the end)
                        ((EditText) v).setText("");
                        ((EditText) v).append(newText);

                    }
                }

                return false;
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getdata
                String org_name = name.getText().toString();
                String org_desc = desc.getText().toString();
                String org_cate = cate.getSelectedItem().toString();
                String org_color = color.getSelectedItem().toString();
                String org_icon = "icon" + Integer.toString(selectediconp);

                //add to data base
                DBP.open();
                int id = DBP.insertOrg(org_name, org_cate, org_desc, org_color, org_icon);
                DBP.addRoot(MainActivity.usr_id, id);
                DBP.close();

                if (id != 0) {
                    //open new Organization details
                    OrgDetailFragment orgdetail = new OrgDetailFragment();

                    FragmentManager fm = ((FragmentActivity) con).getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();

                    transaction.replace(R.id.contentFragment, orgdetail);
                    //transaction.addToBackStack(null);

                    transaction.commit();
                    fm.executePendingTransactions();
                    orgdetail.setVales(con, id);
                } else {
                    //handle problems
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrgListFragment fra = new OrgListFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.contentFragment, fra);
                //transaction.addToBackStack(null);
                transaction.commit();
                fm.executePendingTransactions();

                DBP.open();
                Cursor testdata = DBP.getUserOrg(MainActivity.usr_id);
                Cursor data = DBP.getUserROrg(MainActivity.usr_id);
                DBP.close();

                OrgAdapter org = new OrgAdapter(con, testdata, 0);
                OrgAdapter org2 = new OrgAdapter(con, data, 0);
                fra.setOrgAdaptor(org);
                fra.setSearchOrgAdaptor(org2);
            }
        });

    }

    public void setValues(final int id) {
        final Context con = getActivity();
        //getCursor
        final DBProvider DBP = new DBProvider(con);
        DBP.open();
        Cursor cursor = DBP.getOrgDetails(id);
        DBP.close();

        //get item elements
        final EditText name = (EditText) v.findViewById(R.id.addOrg_name);
        final EditText desc = (EditText) v.findViewById(R.id.addOrg_desc);
        final Spinner cate = (Spinner) v.findViewById(R.id.addOrg_cate);
        final Spinner color = (Spinner) v.findViewById(R.id.addOrg_color);

        Button btnAccept = (Button) v.findViewById(R.id.addOrg_btn_accept);
        Button btnCancel = (Button) v.findViewById(R.id.addOrg_btn_cancel);

        //initialize sample data
        List<String> category = new ArrayList<String>();
        category.add("Technology");
        category.add("Sports");
        category.add("Cars");
        category.add("Study");
        category.add("Charity");
        category.add("Books");
        category.add("Other");
        List<String> colors = new ArrayList<String>();
        colors.add("Red");
        colors.add("Blue");
        colors.add("Green");
        colors.add("Orange");
        colors.add("Purple");
        colors.add("Yellow");
        colors.add("Grey");
        colors.add("Indigo");

        //get data
        String org_name = cursor.getString(cursor.getColumnIndex("org_name"));
        String org_desc = cursor.getString(cursor.getColumnIndex("org_desc"));
        String org_cate = cursor.getString(cursor.getColumnIndex("org_cat"));
        String org_icon = cursor.getString(cursor.getColumnIndex("org_icon"));
        String org_color = cursor.getString(cursor.getColumnIndex("org_color"));
        int resID = con.getResources().getIdentifier(org_icon, "drawable", con.getPackageName());
        int colorp = 0, catep = 0, iconp = 0;

        for (int i = 0; i < category.size(); i++)
            if (category.get(i).equals(org_cate))
                catep = i;
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).equals(org_color))
                colorp = i;
        iconp = Integer.parseInt(org_icon.substring(4));

        //assign values
        name.setText(org_name);
        desc.setText(org_desc);
        cate.setSelection(catep);
        color.setSelection(colorp);
        selectedicon.setImageResource(resID);
        selectediconp = iconp;

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getdata
                String org_name = name.getText().toString();
                String org_desc = desc.getText().toString();
                String org_cate = cate.getSelectedItem().toString();
                String org_color = color.getSelectedItem().toString();
                String org_icon = "icon" + Integer.toString(selectediconp);

                //add to data base
                DBP.open();
                DBP.updateOrg(id, org_name, org_cate, org_desc, org_color, org_icon);
                DBP.close();

                //open new Organization details
                OrgDetailFragment orgdetail = new OrgDetailFragment();

                FragmentManager fm = ((FragmentActivity) con).getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();

                transaction.replace(R.id.contentFragment, orgdetail);
                //transaction.addToBackStack(null);

                transaction.commit();
                fm.executePendingTransactions();
                orgdetail.setVales(con, id);
            }
        });

    }

}