package com.example.abdul.testground.Members;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.abdul.testground.Database.DBProvider;
import com.example.abdul.testground.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by abdul on 4/16/2017.
 */
public class MemAdapter extends CursorAdapter {

    final DBProvider TBD;
    int org_id;
    final MemTree mem;
    boolean edit;

    private LayoutInflater cursorInflater;

    // Default constructor
    public MemAdapter(Context context, Cursor cursor, int id, int flags) {
        super(context, cursor, flags);
        TBD = new DBProvider(context);
        TBD.open();
        HashMap<String,Boolean> pri = TBD.getUserPrivileges(id);
        TBD.close();
        edit = pri.get("edit");
        mem = new MemTree(id, cursor);
        org_id = id;
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, final Context context, Cursor cursor) {
        //get item elements
        final TextView textViewName = (TextView) view.findViewById(R.id.memlist_item_name_textview);
        final TextView textViewTitle = (TextView) view.findViewById(R.id.memlist_item_title_textview);
        ImageButton button = (ImageButton) view.findViewById(R.id.membuttonShowEditDialog);

        //get Data
        final int mem_id = cursor.getInt(cursor.getColumnIndex("usr_id"));
        final int man_id = cursor.getInt(cursor.getColumnIndex("man_id"));
        final String name = cursor.getString(cursor.getColumnIndex("usr_name"));
        final String title = cursor.getString(cursor.getColumnIndex("mem_title"));
        final boolean pri1 = cursor.getInt(cursor.getColumnIndex("pr_invite")) == 1 ? true : false;
        final boolean pri2 = cursor.getInt(cursor.getColumnIndex("pr_org")) == 1 ? true : false;
        final boolean pri3 = cursor.getInt(cursor.getColumnIndex("pr_edit")) == 1 ? true : false;

        //show data
        textViewName.setText(name);
        textViewTitle.setText(title);

        //if user is allowed to edit
        if(edit) {
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    // custom dialog
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.mem_edit_dialog);
                    dialog.setTitle("Edit Member Data");

                    final EditText etitle = (EditText) dialog.findViewById(R.id.editTitle);
                    final Spinner manger = (Spinner) dialog.findViewById(R.id.editManger);
                    final Switch pr1 = (Switch) dialog.findViewById(R.id.switch1);
                    final Switch pr2 = (Switch) dialog.findViewById(R.id.switch2);
                    final Switch pr3 = (Switch) dialog.findViewById(R.id.switch3);

                    etitle.setText(title);
                    final List<MemTree.member> list = mem.getList(mem_id);
                    List<String> namesList = new ArrayList<String>();
                    int cur = 0;
                    for (int i = 0; i < list.size(); i++) {
                        MemTree.member mem = list.get(i);
                        namesList.add(mem.name);
                        if (mem.id == man_id)
                            cur = i;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            context,
                            android.R.layout.simple_spinner_item,
                            namesList
                    );
                    manger.setAdapter(adapter);
                    manger.setSelection(cur);
                    pr1.setChecked(pri1);
                    pr2.setChecked(pri2);
                    pr3.setChecked(pri3);

                    Button cancelButton = (Button) dialog.findViewById(R.id.membtn_cancel);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                    Button doneButton = (Button) dialog.findViewById(R.id.membtn_done);
                    doneButton.setOnClickListener(new View.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            TBD.open();
                            TBD.updateMember(mem_id, org_id, list.get(manger.getSelectedItemPosition()).id, etitle.getText().toString(), pr1.isChecked(), pr2.isChecked(), pr3.isChecked());
                            TBD.close();

                            refresh();

                            dialog.dismiss();
                        }
                    });

                    Button kickButton = (Button) dialog.findViewById(R.id.membtn_kick);
                    kickButton.setOnClickListener(new View.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            TBD.open();
                            TBD.deleteMember(mem_id, org_id);
                            TBD.close();

                            refresh();

                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
        }else{
            button.setVisibility(View.GONE);
        }
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.list_item_mem, parent, false);
    }

    public void refresh() {
        TBD.createDatabase();
        TBD.open();
        Cursor testdata = TBD.getOrgMembers(org_id);
        TBD.close();
        mem.update(testdata);
        this.swapCursor(testdata);
    }
}