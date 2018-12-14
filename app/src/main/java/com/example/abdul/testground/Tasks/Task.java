package com.example.abdul.testground.Tasks;

/**
 * Created by abdul on 7/20/2017.
 */

public class Task {
    int id,par,status;
    String title,desc;
    long time;
    Task (int id,int par, String title,String desc , long time , int status){
        this.id = id;
        this.par = par;
        this.time = time;
        this.title = title;
        this.desc = desc;
        this.status = status;
    }
}
