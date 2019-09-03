package com.mm87.android.lib.base.tool;

import android.os.AsyncTask;

/**
 * Created by mendoza on 01/12/2018.
 */
public class ExecuteAsyncTask {

    public static void exec(Task task){
        new AsyncTask<Task, Integer, Void>(){

            @Override
            protected Void doInBackground(Task... tasks) {
                tasks[0].execute();
                return null;
            }
        }.execute(task);
    }

}
