package com.example.abdul.testground.Database;

import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.CursorAdapter;

import com.example.abdul.testground.MainActivity;

public class DBProvider {
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DBHelper mDbHelper;

    public DBProvider(Context context) {
        this.mContext = context;
        mDbHelper = new DBHelper(mContext);
    }

    public DBProvider createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DBProvider open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public int CheckUser(String email, String name) {
        try {
            String sql = "SELECT usr_id , usr_email , usr_name from User where usr_email = '" + email + "' or usr_name = '" + name + "'";

            Cursor mCur = mDb.rawQuery(sql, null);
            int ret = 0;
            if (mCur != null) {
                for (mCur.moveToFirst(); !mCur.isAfterLast(); mCur.moveToNext()) {
                    if (mCur.getString(1).equals(email))
                        ret += 1;
                    if (mCur.getString(2).equals(name))
                        ret += 2;
                }
            }
            mCur.close();
            return ret;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public int Login(String email, String pass) {
        try {
            String sql = "SELECT usr_id from User where usr_email = '" + email + "' and usr_pass = '" + pass + "'";

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                for (mCur.moveToFirst(); !mCur.isAfterLast(); mCur.moveToNext()) {
                    int id = mCur.getInt(0);
                    mCur.close();
                    return id;
                }
            }
            mCur.close();
            return 0;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public boolean Register(String name, String email, String pass) {
        try {
            String sql = "insert into User ( usr_name , usr_email , usr_pass ) values( '" + name + "' , '" + email + "' , '" + pass + "' )";
            mDb.execSQL(sql);

            return true;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void updateUser(int usr_id, String name, String email, String pass, String firstName, String lastName, String status, String phone) {
        try {
            String sql = "update User set usr_name = '" + name + "' , usr_email = '" + email + "' , usr_pass = '" + pass + "' , usr_first_name = '" + firstName + "' , usr_last_name = '" + lastName + "' , usr_status = '" + status + "' , usr_phone = '" + phone + "' where usr_id =  " + usr_id;
            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }


    public Cursor getUserOrg(int user) {
        try {
            String sql = "SELECT O.org_id as _id , org_name , org_desc , org_date ,org_cat FROM Organization O join   Member M where O.org_id = M.org_id and usr_id = " + user;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getUserROrg(int user) {
        try {
            String sql = "SELECT O.org_id as _id , org_name , org_desc , org_date ,org_cat FROM Organization O where (select count(org_id) from Member where org_id = O.org_id and usr_id = " + user + ") == 0";

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getSearchOrg(String search) {
        try {
            String sql = "SELECT org_id as _id , org_name , org_desc , org_date ,org_cat FROM Organization where org_name like '" + search + "%' or org_name like '% " + search + "%'";

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getOrgDetails(int org) {
        try {
            String sql = "SELECT * FROM Organization where org_id = " + org;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public int insertOrg(String name, String category, String desc, String color, String icon) {
        try {
            String sql = "insert into Organization ( org_name , org_cat , org_desc , org_color , org_icon , org_date , org_root ) values( '" + name + "' , '" + category + "' , '" + desc + "' , '" + color + "' , '" + icon + "' , " + System.currentTimeMillis() + " ," + MainActivity.usr_id + ") ";
            mDb.execSQL(sql);
            sql = "select Max(org_id) from Organization";
            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
                int ans = mCur.getInt(0);
                mCur.close();
                return ans;
            }
            return 0;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void updateOrg(int id, String name, String category, String desc, String color, String icon) {
        try {
            String sql = "update Organization set org_name = '" + name + "' , org_cat =  '" + category + "' , org_desc =  '" + desc + "' , org_color = '" + color + "'  , org_icon = '" + icon + "' where org_id = " + id;
            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getOrgUser(int org, int user) {
        try {
            String sql = "SELECT * FROM Member where org_id = " + org + " and usr_id = " + user;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getOrgMembers(int org) {
        try {
            String sql = "SELECT Member.usr_id as _id , * FROM Member join User on Member.usr_id = User.usr_id where org_id = " + org;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public boolean aMemberIn(int usr, int org) {
        try {
            String sql = "SELECT * FROM Member where org_id = " + org + " and usr_id = " + usr;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null && mCur.getCount() > 0) {
                mCur.close();
                return true;
            }
            mCur.close();
            return false;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public boolean aInviteFromTo(int s_id, int r_id, int type) {
        try {
            String sql = "SELECT * FROM Invite where send_id = " + s_id + " and receive_id = " + r_id + " and inv_type = " + type;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null && mCur.getCount() > 0) {
                mCur.close();
                return true;
            }
            mCur.close();
            return false;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public HashMap<String, Boolean> getUserPrivileges(int org_id) {
        HashMap<String, Boolean> pri = new HashMap<>();
        pri.put("invite", false);
        pri.put("edit", false);
        pri.put("organization", false);
        try {
            String sql = "SELECT * FROM Member where usr_id = " + MainActivity.usr_id + " and org_id = " + org_id;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                for (mCur.moveToFirst(); !mCur.isAfterLast(); mCur.moveToNext()) {
                    pri.put("invite", mCur.getInt(mCur.getColumnIndex("pr_invite")) == 1 ? true : false);
                    pri.put("edit", mCur.getInt(mCur.getColumnIndex("pr_edit")) == 1 ? true : false);
                    pri.put("organization", mCur.getInt(mCur.getColumnIndex("pr_org")) == 1 ? true : false);
                }
            }
            mCur.close();
            return pri;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void addMember(int usr_id, int org_id) {
        try {
            Long tsLong = System.currentTimeMillis();
            String sql = "insert into Member values( " + org_id + " , " + usr_id + " , (select org_root from Organization where org_id = " + org_id + ") , 'new member' , 0 , 0 , 0 , " + tsLong + " )";

            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void addRoot(int usr_id, int org_id) {
        try {
            Long tsLong = System.currentTimeMillis();
            String sql = "insert into Member values( " + org_id + " , " + usr_id + " , (select org_root from Organization where org_id = " + org_id + ") , 'owner' , 1 , 1 , 1 , " + tsLong + " )";

            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void addMember(int usr_id, int org_id, int man_id) {
        try {
            Long tsLong = System.currentTimeMillis();
            String sql = "insert into Member values( " + org_id + " , " + usr_id + " , " + man_id + " , 'new member' , 0 , 0 , 0 , " + tsLong + " )";

            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void updateMember(int usr_id, int org_id, int man_id, String title, boolean pr1, boolean pr2, boolean pr3) {
        try {
            String sql = "update Member set man_id = " + man_id + " , mem_title = '" + title + "' , pr_invite = " + (pr1 ? 1 : 0) + " , pr_org = " + (pr2 ? 1 : 0) + " , pr_edit = " + (pr3 ? 1 : 0) + " where org_id = " + org_id + " and usr_id =  " + usr_id;
            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void deleteMember(int usr_id, int org_id) {
        try {
            String sql = "delete from Member where org_id = " + org_id + " and usr_id =  " + usr_id;
            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }


    public Cursor getTestData(int user) {
        try {
            String sql = "SELECT tsk_id as _id , tsk_title , tsk_desc , tsk_time ,par_id , tsk_status FROM Task where usr_id = " + user;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }

            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public int checkTask(int id) {
        try {
            String sql = "SELECT usr_id FROM Task where tsk_id = " + id;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }

            return mCur.getInt(0);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }


    public Cursor getSubTaskData(int id) {
        try {
            String sql = "SELECT tsk_id as _id , tsk_title , tsk_desc , tsk_time , tsk_status FROM Task where par_id = " + id;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }

            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void deleteData(int id) {
        try {
            String sql = "delete from Task where tsk_id = " + id;
            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void updateData(int id, String title, String desc, Long time) {
        try {
            String sql = "update Task set tsk_title = '" + title + "' , tsk_desc = '" + desc + "' ,tsk_time = " + time + " where tsk_id = " + id;
            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void setStatus(int id, int stat) {
        try {
            String sql = "update Task set tsk_Status = " + stat + " where tsk_id = " + id;
            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void insetData(String title, String desc, Long time) {
        try {
            String sql = "insert into Task (usr_id , tsk_title , tsk_desc , tsk_time ) values( " + MainActivity.usr_id + " , '" + title + "' , '" + desc + "' ," + time + ") ";
            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void insetSubData(String title, String desc, Long time, int par) {
        try {
            String sql = "insert into Task ( tsk_title , tsk_desc , tsk_time , par_id) values( '" + title + "' , '" + desc + "' ," + time + " , " + par + ") ";
            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void assignSubTask(int tsk_id, int org_id, int usr_id) {
        try {
            String sql = "update Task set usr_id = " + usr_id + " , tsk_type = " + org_id + " where tsk_id = " + tsk_id;
            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void removeInvite(int s_id, int r_id, int type) {
        try {
            String sql = "delete from Invite where send_id = " + s_id + " and receive_id = " + r_id + " and inv_type = " + type;
            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void insertInvite(int s_id, int r_id, int type) {
        Long tsLong = System.currentTimeMillis();
        try {
            String sql = "insert into Invite values( " + s_id + " , " + r_id + " ," + type + " , " + tsLong + ") ";
            mDb.execSQL(sql);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getUsrInvites(int id) {
        try {
            String sql = "SELECT send_id as _id ,* FROM Invite Join Organization on send_id = org_id where inv_type = 1 and receive_id = " + id;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }

            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getOrgInvites(int id) {
        try {
            String sql = "SELECT receive_id as _id ,* FROM Invite Join User on receive_id = usr_id where inv_type = 1 and send_id = " + id;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }

            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getOrgRequests(int id) {
        try {
            String sql = "SELECT send_id as _id ,* FROM Invite Join User on send_id = usr_id where inv_type = 0 and receive_id = " + id;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }

            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }


}