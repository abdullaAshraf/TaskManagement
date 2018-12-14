package com.example.abdul.testground;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TimePicker;

import com.example.abdul.testground.Database.DBHelper;
import com.example.abdul.testground.Database.DBProvider;
import com.example.abdul.testground.Database.JSONfunctions;
import com.example.abdul.testground.Members.MemAdapter;
import com.example.abdul.testground.Members.MemListFragment;
import com.example.abdul.testground.Members.MemTree;
import com.example.abdul.testground.Organizations.InvAdapter;
import com.example.abdul.testground.Organizations.InvListFragment;
import com.example.abdul.testground.Organizations.OrgAdapter;
import com.example.abdul.testground.Organizations.OrgListFragment;
import com.example.abdul.testground.Tasks.ExpandableTaskAdaptor;
import com.example.abdul.testground.Tasks.TaskListFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    final Context context = this;
    DBProvider DBP;
    TaskListFragment fragment;
    ExpandableTaskAdaptor tsk;
    int nav_cur = 1;
    public static int usr_id = 1;

    public void onStart() {
        super.onStart();
        //JSONfunctions js = new JSONfunctions();
        //js.updateData();
        DBP = new DBProvider(context);
        DBP.createDatabase();
        DBP.open();
        Cursor testdata = DBP.getTestData(usr_id);
        DBP.close();

        fragment = new TaskListFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment).commit();
        fm.executePendingTransactions();

        tsk = new ExpandableTaskAdaptor(context, testdata, (ExpandableListView) findViewById(R.id.listview_task));
        fragment.setListAdaptor(tsk);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if(b != null)
            usr_id = b.getInt("key");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DBProvider tskD = new DBProvider(context);
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.task_edit_dialog);
                dialog.setTitle("Add Task");

                EditText T = (EditText) dialog.findViewById(R.id.editText);
                EditText D = (EditText) dialog.findViewById(R.id.editText1);
                DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker);
                TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker);

                Calendar calendar = Calendar.getInstance();
                dp.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
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

                        tskD.open();
                        tskD.insetData(T, D, (long) calendar.getTimeInMillis());
                        tskD.close();

                        refresh();

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        //show invites
        final FloatingActionButton inv = (FloatingActionButton) findViewById(R.id.inv);
        inv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.hide();
                inv.hide();
                InvListFragment fra = new InvListFragment();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.contentFragment, fra);
                //transaction.addToBackStack(null);
                transaction.commit();
                fm.executePendingTransactions();

                DBP.open();
                Cursor data = DBP.getUsrInvites(usr_id);
                DBP.close();

                InvAdapter adapter = new InvAdapter(context, data,  usr_id , 0);
                fra.setInvAdaptor(adapter);
            }
        });
    }

    public void refresh(){
        DBP.open();
        Cursor testdata = DBP.getTestData(usr_id);
        DBP.close();

        tsk.update(testdata);
        tsk.sort(0);
        fragment.setListAdaptor(tsk);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            Bundle b = new Bundle();
            b.putInt("logout", usr_id); //Your id
            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton inv = (FloatingActionButton) findViewById(R.id.inv);
        int id = item.getItemId();

        if (id == R.id.nav_az) {
            if(nav_cur == 0) {
                tsk.sort(1);
                nav_cur = 1;
            }
            else {
                tsk.sort(0);
                nav_cur = 0;
            }
            fragment.setListAdaptor(tsk);
        } else if (id == R.id.nav_time) {
            if(nav_cur == 2) {
                tsk.sort(3);
                nav_cur = 3;
            }
            else {
                tsk.sort(2);
                nav_cur = 2;
            }
            fragment.setListAdaptor(tsk);
        } else if (id == R.id.nav_cat) {
            if(nav_cur == 4) {
                tsk.sort(5);
                nav_cur = 5;
            }
            else {
                tsk.sort(4);
                nav_cur = 4;
            }
            fragment.setListAdaptor(tsk);
        } else if (id == R.id.nav_task) {
            fab.show();
            inv.show();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.contentFragment, fragment);
            //transaction.addToBackStack(null);
            transaction.commit();
            fm.executePendingTransactions();
            refresh();
        } else if (id == R.id.nav_org) {
            fab.hide();
            inv.hide();
            OrgListFragment fra = new OrgListFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.contentFragment, fra);
            //transaction.addToBackStack(null);
            transaction.commit();
            fm.executePendingTransactions();

            DBP.open();
            Cursor testdata = DBP.getUserOrg(usr_id);
            Cursor data = DBP.getUserROrg(usr_id);
            DBP.close();

            OrgAdapter org = new OrgAdapter(context, testdata, 0);
            OrgAdapter org2 = new OrgAdapter(context, data, 0);
            fra.setOrgAdaptor(org);
            fra.setSearchOrgAdaptor(org2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

