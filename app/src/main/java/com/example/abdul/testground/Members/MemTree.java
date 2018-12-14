package com.example.abdul.testground.Members;

import android.database.Cursor;
import android.util.Log;

import com.example.abdul.testground.Database.DBProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by aabdu on 7/2/2018.
 */


public class MemTree {
    int org_id;
    ArrayList<ArrayList<Integer>> childs;
    ArrayList<String> names;
    ArrayList<Integer> ids;
    ArrayList<Integer> parents;
    ArrayList<Integer> depth;
    Map<Integer, Integer> map;
    List<member> list;
    int root;

    public MemTree(int org, Cursor c) {
        org_id = org;
        update(c);
    }

    public void update(Cursor c) {
        int num = c.getCount()+1;
        childs = new ArrayList<ArrayList<Integer>>(num);
        names = new ArrayList<String>(num);
        ids = new ArrayList<Integer>(num);
        parents = new ArrayList<Integer>(num);
        depth = new ArrayList<Integer>(num);
        for(int i=0; i<num; i++){
            childs.add(new ArrayList<Integer>());
            names.add("0");
            ids.add(0);
            parents.add(0);
            depth.add(0);
        }
        map = new HashMap<Integer, Integer>();
        int cnt = 0;

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                Log.d("myTag" , Integer.toString(cnt));
                int a = c.getInt(c.getColumnIndex("man_id"));
                int b = c.getInt(c.getColumnIndex("usr_id"));
                if (!map.containsKey(a))
                    map.put(a, cnt++);
                if (!map.containsKey(b))
                    map.put(b, cnt++);
                if (a == b)
                    root = map.get(a);
                else {
                    childs.get(map.get(a)).add(map.get(b));
                    parents.set(map.get(b), map.get(a));
                }
                names.set(map.get(b), c.getString(c.getColumnIndex("usr_name")));
                ids.set(map.get(b), b);
            }
        } finally {
            //c.close();
        }
        setDepth();
    }

    void setDepth() {
        Queue<Integer> q = new LinkedList<>();
        q.add(root);
        int OO = 1000000000;
        for (int i = 0; i < depth.size(); i++)
            depth.set(i, OO);

        int dep = 0, cur , sz = 1;
        for (; q.size() > 0; ++dep, sz = q.size()) {
            while (sz-- > 0) {
                cur = q.peek();
                q.poll();
                for (int i = 0; i < childs.get(cur).size(); i++)
                    if (depth.get(childs.get(cur).get(i)) == OO) {
                        q.add(childs.get(cur).get(i));
                        depth.set(childs.get(cur).get(i), dep + 1);
                    }
            }
        }

        list = new ArrayList<member>();
        for (int i=0; i<ids.size(); i++)
            list.add(new member(ids.get(i),i, names.get(i) , depth.get(i)));
        Collections.sort(list, new Comparator<member>() {
            @Override
            public int compare(member o1, member o2) {
                return o1.depth - o2.depth;
            }
        });
    }

    public class member {
        public int id,index,depth;
        public String name;
        public member (int id,int index , String name, int depth){
            this.id = id;
            this.index = index;
            this.name = name;
            this.depth = depth;
        }
    }

    public List<member> getList(int usr_id) {
        return  list;
    }

    public List<member> getLowerList(int usr_id) {
        List<member> theList = new ArrayList<member>();;
        Queue<Integer> q = new LinkedList<>();
        q.add(map.get(usr_id));
        int OO = 1000000000;
        ArrayList<Integer> len = new ArrayList<Integer>();
        Log.d("test",Integer.toString(len.size()));
        for (int i = 0; i < names.size(); i++)
            len.add(OO);

        int dep = 0, cur , sz = 1;
        for (; q.size() > 0; ++dep, sz = q.size()) {
            while (sz-- > 0) {
                cur = q.peek();
                q.poll();
                for (int i = 0; i < childs.get(cur).size(); i++)
                    if (len.get(childs.get(cur).get(i)) == OO) {
                        q.add(childs.get(cur).get(i));
                        len.set(childs.get(cur).get(i), dep + 1);
                        theList.add(new member(ids.get(childs.get(cur).get(i)),childs.get(cur).get(i), names.get(childs.get(cur).get(i)) , dep + 1));
                    }
            }
        }
        return theList;
    }

}
