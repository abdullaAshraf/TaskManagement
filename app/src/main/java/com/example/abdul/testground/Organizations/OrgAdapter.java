package com.example.abdul.testground.Organizations;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.abdul.testground.Database.DBProvider;
import com.example.abdul.testground.R;

/**
 * Created by abdul on 4/16/2017.
 */
public class OrgAdapter extends CursorAdapter {

    private LayoutInflater cursorInflater;

    // Default constructor
    public OrgAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, final Context context, Cursor cursor) {

        //get elements
        TextView date = (TextView) view.findViewById(R.id.list_item_high);
        TextView time = (TextView) view.findViewById(R.id.list_item_low);
        TextView textViewTitle = (TextView) view.findViewById(R.id.list_item_name);
        TextView textViewDesc = (TextView) view.findViewById(R.id.list_item_desc);

        //get data
        final int tsk_id = cursor.getInt(cursor.getColumnIndex("_id"));
        String title = cursor.getString(cursor.getColumnIndex("org_name"));
        String desc = cursor.getString(cursor.getColumnIndex("org_desc"));
        String cat = cursor.getString(cursor.getColumnIndex("org_cat"));
        Long timest = cursor.getLong(cursor.getColumnIndex("org_date"));


        //handle long descriptions
        String cleanString = desc.replaceAll("\r", " ").replaceAll("\n", " ");
        if(cleanString.length() > 15)
            cleanString = cleanString.substring(0,15) + "...";

        //assign data to elements
        textViewTitle.setText(title);
        textViewDesc.setText(cleanString);
        time.setText(DateFormat.format("dd/MM/yyyy", timest).toString());
        date.setText(cat);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrgDetailFragment orgdetail = new OrgDetailFragment();

                FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();

                transaction.replace(R.id.contentFragment, orgdetail);
                //transaction.addToBackStack(null);

                transaction.commit();
                fm.executePendingTransactions();
                orgdetail.setVales(context, tsk_id);
            }
        });
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.list_item_org, parent, false);
    }
}