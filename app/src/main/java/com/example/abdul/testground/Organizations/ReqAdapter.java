package com.example.abdul.testground.Organizations;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abdul.testground.Database.DBProvider;
import com.example.abdul.testground.R;

/**
 * Created by abdul on 4/16/2017.
 */
public class ReqAdapter extends CursorAdapter {

    private LayoutInflater cursorInflater;
    Context con;
    final DBProvider DBP;
    int org_id;

    // Default constructor
    public ReqAdapter(Context context, Cursor cursor, int ID, int flags) {
        super(context, cursor, flags);
        con = context;
        org_id = ID;
        DBP = new DBProvider(context);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, final Context context, Cursor cursor) {
        //get ID
        final int sendId = cursor.getInt(cursor.getColumnIndex("send_id"));
        final int receiveId = cursor.getInt(cursor.getColumnIndex("receive_id"));
        final int type = cursor.getInt(cursor.getColumnIndex("inv_type"));

        final int usrId = cursor.getInt(cursor.getColumnIndex("send_id"));
        String usrName = cursor.getString(cursor.getColumnIndex("usr_name"));
        String usrEmail = cursor.getString(cursor.getColumnIndex("usr_email"));
        //Long invTime = Long.parseLong(cursor.getString(cursor.getColumnIndex("inv_date")));

        TextView textViewName = (TextView) view.findViewById(R.id.list_item_name);
        TextView textViewMail = (TextView) view.findViewById(R.id.list_item_email);

        Button buttonAccept = (Button) view.findViewById(R.id.btn_accept);
        Button buttonDelete = (Button) view.findViewById(R.id.btn_delete);

        textViewName.setText(usrName);
        textViewMail.setText(usrEmail);

        /*
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show user profile
            }
        });
        */

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBP.open();
                DBP.removeInvite(sendId,receiveId,type);
                DBP.addMember(usrId,org_id);
                DBP.close();
                refresh();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBP.open();
                DBP.removeInvite(sendId,receiveId,type);
                DBP.close();
                refresh();
            }
        });
    }
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.list_item_req, parent, false);
    }

    public void refresh() {
        DBP.open();
        Cursor newData = DBP.getOrgRequests(org_id);
        DBP.close();
        this.swapCursor(newData);
    }
}