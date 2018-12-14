package com.example.abdul.testground.Organizations;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
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
public class InvAdapter extends CursorAdapter {

    private LayoutInflater cursorInflater;
    Context con;
    final DBProvider DBP;
    int usr_id;

    // Default constructor
    public InvAdapter(Context context, Cursor cursor,int usrID, int flags) {
        super(context, cursor, flags);
        con = context;
        usr_id = usrID;
        DBP = new DBProvider(context);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, final Context context, Cursor cursor) {
        //get ID
        final int sendId = cursor.getInt(cursor.getColumnIndex("send_id"));
        final int receiveId = cursor.getInt(cursor.getColumnIndex("receive_id"));
        final int type = cursor.getInt(cursor.getColumnIndex("inv_type"));

        final int orgId = cursor.getInt(cursor.getColumnIndex("send_id"));
        String orgName = cursor.getString(cursor.getColumnIndex("org_name"));
        Long invTime = Long.parseLong(cursor.getString(cursor.getColumnIndex("inv_date")));

        TextView textViewName = (TextView) view.findViewById(R.id.list_item_name);
        TextView textViewDate = (TextView) view.findViewById(R.id.list_item_date);

        Button buttonAccept = (Button) view.findViewById(R.id.btn_accept);
        Button buttonDelete = (Button) view.findViewById(R.id.btn_delete);

        ImageView orgIcon = (ImageView) view.findViewById(R.id.list_item_icon);

        textViewName.setText(orgName);
        textViewDate.setText(DateFormat.format("MM/dd", invTime).toString());



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrgDetailFragment orgdetail = new OrgDetailFragment();

                FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();

                transaction.replace(R.id.contentFragment, orgdetail);
                //transaction.addToBackStack(null);

                transaction.commit();
                fm.executePendingTransactions();
                orgdetail.setVales(context ,orgId);
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBP.open();
                DBP.removeInvite(sendId,receiveId,type);
                DBP.addMember(usr_id,orgId);
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
        return cursorInflater.inflate(R.layout.list_item_inv, parent, false);
    }

    public void refresh() {
        DBP.open();
        Cursor newData = DBP.getUsrInvites(usr_id);
        DBP.close();
        this.swapCursor(newData);
    }
}