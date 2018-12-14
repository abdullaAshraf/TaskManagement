package com.example.abdul.testground.Tasks;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.abdul.testground.Database.DBProvider;
import com.example.abdul.testground.MainActivity;
import com.example.abdul.testground.Members.MemTree;
import com.example.abdul.testground.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by abdul on 7/11/2017.
 */


public class ExpandableTaskAdaptor extends BaseExpandableListAdapter {
    private Context context;
    private Cursor cursor;
    private List<Task> ListDataHeader;
    private HashMap<Integer, List<Task>> ListHashMap;
    private ExpandableListView listView;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;


    public ExpandableTaskAdaptor(Context context, Cursor cursor, ExpandableListView listView) {
        this.context = context;
        this.cursor = cursor;
        this.listView = listView;
        update(cursor);
    }

    public void update(Cursor cursor) {
        final DBProvider TBD = new DBProvider(context);
        TBD.createDatabase();
        TBD.open();
        ListDataHeader = new ArrayList<Task>();
        ListHashMap = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                int pr = cursor.getInt(cursor.getColumnIndex("par_id"));

                if (cursor.getInt(cursor.getColumnIndex("tsk_status")) < 2 && (pr == 0 || TBD.checkTask(pr) != 1)) {
                    Task t = new Task(id, pr, cursor.getString(cursor.getColumnIndex("tsk_title")), cursor.getString(cursor.getColumnIndex("tsk_desc")), cursor.getLong(cursor.getColumnIndex("tsk_time")), cursor.getInt(cursor.getColumnIndex("tsk_status")));
                    ListDataHeader.add(t);
                }
            } while (cursor.moveToNext());
        }
        for (Task it : ListDataHeader) {
            Cursor s = TBD.getSubTaskData(it.id);
            List<Task> subs = new ArrayList<Task>();
            if (s.moveToFirst()) {
                do {
                    int id = s.getInt(s.getColumnIndex("_id"));
                    int pr = it.id;
                    Task t = new Task(id, pr, s.getString(s.getColumnIndex("tsk_title")), s.getString(s.getColumnIndex("tsk_desc")), s.getLong(s.getColumnIndex("tsk_time")), s.getInt(s.getColumnIndex("tsk_status")));
                    subs.add(t);
                } while (s.moveToNext());
            }
            Task addNew = new Task(0, it.id, "Add SubTask", "press here", 0, 0);
            subs.add(addNew);
            ListHashMap.put(it.id, subs);
        }
        TBD.close();
        sort(0);
    }


    public void sort(final int i) {
        Collections.sort(ListDataHeader, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                if (o1.status < o2.status)
                    return -1;
                else if (o1.status > o2.status)
                    return 1;
                else {
                    if (i == 0)
                        return o1.title.compareTo(o2.title);
                    else if (i == 1)
                        return o2.title.compareTo(o1.title);
                    else if (i == 2)
                        return o1.time < o2.time ? -1
                                : o1.time > o2.time ? 1
                                : 0;
                    else if (i == 3)
                        return o1.time < o2.time ? 1
                                : o1.time > o2.time ? -1
                                : 0;
                    else
                        return o1.id < o2.id ? -1
                                : o1.id > o2.id ? 1
                                : 0;
                }
            }
        });

    }

    @Override
    public int getGroupCount() {
        return ListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return ListHashMap.get(ListDataHeader.get(i).id).size();
    }

    @Override
    public Object getGroup(int i) {
        return ListDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return ListHashMap.get(ListDataHeader.get(i).id).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        final Task Ts = (Task) getGroup(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_task, null);
        }
        final TextView textViewTitle = (TextView) view.findViewById(R.id.list_item_title_textview);
        textViewTitle.setText(Ts.title);
        final TextView textViewDesc = (TextView) view.findViewById(R.id.list_item_task_textview);
        textViewDesc.setText(Ts.desc);
        final TextView date = (TextView) view.findViewById(R.id.list_item_high_textview);
        final TextView time = (TextView) view.findViewById(R.id.list_item_low_textview);
        final Long timest = Ts.time;
        date.setText(DateFormat.format("MM/dd", timest).toString());
        time.setText(DateFormat.format("h:mm a", timest).toString());

        ImageButton button = (ImageButton) view.findViewById(R.id.buttonShowEditDialog);

        if (Ts.status == 1)
            view.setBackgroundColor(0xFF686c72);

        final DBProvider TBD = new DBProvider(context);
        TBD.createDatabase();

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;

                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            TBD.open();
                            // Left to Right swipe action
                            if (x2 > x1) {
                                TBD.setStatus(Ts.id, Ts.status + 1);
                            }
                            // Right to left swipe action
                            else {
                                TBD.setStatus(Ts.id, 0);
                            }
                            TBD.close();
                            refresh();
                        } else {
                            if (listView.isGroupExpanded(i)) {
                                listView.collapseGroup(i);
                            } else
                                listView.expandGroup(i);
                        }
                        break;
                }
                return true;
            }


        });
        // add button listener
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.task_edit_dialog);
                dialog.setTitle("Edit Task");

                EditText T = (EditText) dialog.findViewById(R.id.editText);
                EditText D = (EditText) dialog.findViewById(R.id.editText1);
                DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker);
                TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker);

                T.setText(Ts.title);
                D.setText(Ts.desc);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timest);
                dp.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                tp.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                tp.setCurrentMinute(calendar.get(Calendar.MINUTE));


                Button dialogButton = (Button) dialog.findViewById(R.id.btn_cancel);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                Button DoneButton = (Button) dialog.findViewById(R.id.btn_done);
                DoneButton.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        String T = ((EditText) dialog.findViewById(R.id.editText)).getText().toString();
                        String D = ((EditText) dialog.findViewById(R.id.editText1)).getText().toString();
                        DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker);
                        TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(dp.getYear(), dp.getMonth() - 1, dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());


                        TBD.open();
                        TBD.updateData(Ts.id, T, D, (long) calendar.getTimeInMillis());
                        TBD.close();


                        refresh();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return view;
    }

    @Override
    public View getChildView(final int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final DBProvider TBD = new DBProvider(context);
        TBD.createDatabase();
        final Task Ts = (Task) getChild(i, i1);
        //handle add new subtask
        if (Ts.id == 0) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.add_sub_task, null);
            final TextView btn_add = (TextView) view.findViewById(R.id.textView2);
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DBProvider tskD = new DBProvider(context);
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.task_edit_dialog);
                    dialog.setTitle("Add SubTask");

                    EditText T = (EditText) dialog.findViewById(R.id.editText);
                    EditText D = (EditText) dialog.findViewById(R.id.editText1);
                    DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker);
                    TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker);

                    Calendar calendar = Calendar.getInstance();
                    dp.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                    tp.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                    tp.setCurrentMinute(calendar.get(Calendar.MINUTE));


                    Button dialogButton = (Button) dialog.findViewById(R.id.btn_cancel);
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                    Button DoneButton = (Button) dialog.findViewById(R.id.btn_done);
                    DoneButton.setOnClickListener(new View.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            String T = ((EditText) dialog.findViewById(R.id.editText)).getText().toString();
                            String D = ((EditText) dialog.findViewById(R.id.editText1)).getText().toString();
                            DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker);
                            TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(dp.getYear(), dp.getMonth() - 1, dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());

                            tskD.createDatabase();
                            tskD.open();
                            final Task Ta = (Task) getGroup(i);
                            tskD.insetSubData(T, D, (long) calendar.getTimeInMillis(), Ta.id);
                            tskD.close();

                            refresh();

                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
            return view;
        }

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_item_subtask, null);

        final TextView textViewTitle = (TextView) view.findViewById(R.id.sublist_item_title_textview);
        textViewTitle.setText(Ts.title);
        final TextView textViewDesc = (TextView) view.findViewById(R.id.sublist_item_task_textview);
        textViewDesc.setText(Ts.desc);
        final TextView date = (TextView) view.findViewById(R.id.sublist_item_high_textview);
        final TextView time = (TextView) view.findViewById(R.id.sublist_item_low_textview);
        final Long timest = Ts.time;
        date.setText(DateFormat.format("MM/dd", timest).toString());
        time.setText(DateFormat.format("h:mm a", timest).toString());

        ImageButton edit = (ImageButton) view.findViewById(R.id.subbuttonShowEditDialog);
        ImageButton assign = (ImageButton) view.findViewById(R.id.subbuttonAssignTask);

        // add button listener
        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.task_edit_dialog);
                dialog.setTitle("Edit Task");

                EditText T = (EditText) dialog.findViewById(R.id.editText);
                EditText D = (EditText) dialog.findViewById(R.id.editText1);
                DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker);
                TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker);

                T.setText(Ts.title);
                D.setText(Ts.desc);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timest);
                dp.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                tp.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                tp.setCurrentMinute(calendar.get(Calendar.MINUTE));


                Button dialogButton = (Button) dialog.findViewById(R.id.btn_cancel);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                Button DoneButton = (Button) dialog.findViewById(R.id.btn_done);
                DoneButton.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        String T = ((EditText) dialog.findViewById(R.id.editText)).getText().toString();
                        String D = ((EditText) dialog.findViewById(R.id.editText1)).getText().toString();
                        DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker);
                        TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(dp.getYear(), dp.getMonth() - 1, dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());

                        TBD.open();
                        TBD.updateData(Ts.id, T, D, (long) calendar.getTimeInMillis());
                        TBD.close();

                        refresh();

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        // add assign listener
        assign.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.task_assign_dialog);
                dialog.setTitle("Assign Task");

                final Spinner org = (Spinner) dialog.findViewById(R.id.dialog_org);
                final Spinner member = (Spinner) dialog.findViewById(R.id.dialog_mem);
                Button dialogButton = (Button) dialog.findViewById(R.id.btn_cancel);
                Button DoneButton = (Button) dialog.findViewById(R.id.btn_done);

                TBD.open();
                Cursor orgList = TBD.getUserOrg(MainActivity.usr_id);
                TBD.close();

                List<String> org_list = new ArrayList<String>();
                final List<Integer> org_list_id = new ArrayList<Integer>();

                final List<String> mem_list = new ArrayList<String>();
                final List<Integer> mem_list_id = new ArrayList<Integer>();

                Log.d("log",Integer.toString(orgList.getCount()));

                if(orgList != null)
                for (orgList.moveToFirst(); !orgList.isAfterLast(); orgList.moveToNext()) {
                    org_list.add(orgList.getString(orgList.getColumnIndex("org_name")));
                    org_list_id.add(orgList.getInt(orgList.getColumnIndex("_id")));
                }

                if(org_list.isEmpty())
                    org_list.add("No Organizations available");

                //add data to spinners
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, org_list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                org.setAdapter(dataAdapter);

                org.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(org.getSelectedItem().toString().equals("No Organizations available"))
                            return;
                        int orgId = org_list_id.get(org.getSelectedItemPosition());
                        TBD.open();
                        Cursor memList = TBD.getOrgMembers(orgId);
                        TBD.close();
                        MemTree mem = new MemTree(orgId, memList);
                        List<MemTree.member> theList = mem.getLowerList(MainActivity.usr_id);

                        mem_list.clear();
                        mem_list_id.clear();

                        for (MemTree.member m : theList) {
                            mem_list.add(m.name);
                            mem_list_id.add(m.id);
                        }

                        if(mem_list.isEmpty())
                            mem_list.add("No Members available");

                        //add data to spinners
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                                android.R.layout.simple_spinner_item, mem_list);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        member.setAdapter(dataAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                DoneButton.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        if(member.getSelectedItem().toString().equals("No Members available"))
                            return;
                        int org_id = org_list_id.get(org.getSelectedItemPosition());
                        int mem_id = mem_list_id.get(member.getSelectedItemPosition());
                        TBD.open();
                        TBD.assignSubTask(Ts.id, org_id, mem_id);
                        TBD.close();

                        refresh();

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    public void refresh() {
        final DBProvider TBD = new DBProvider(context);
        TBD.createDatabase();
        TBD.open();
        Cursor testdata = TBD.getTestData(MainActivity.usr_id);
        TBD.close();

        update(testdata);
        listView.setAdapter(this);
    }
}
