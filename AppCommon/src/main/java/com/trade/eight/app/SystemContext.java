package com.trade.eight.app;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.trade.eight.dao.DatabaseHelper;

public class SystemContext {

    private static SystemContext instance;

    private static final String SYNC_KEY = "SYNC_KEY";

    private Context context;

    private DatabaseHelper databaseHelper;


    private SystemContext(Context context) {
        this.context = context;
    }

    public static SystemContext getInstance(Context context) {
        synchronized (SYNC_KEY) {
            if (instance == null)
                instance = new SystemContext(context);
            else
                instance.context = context;

            return instance;
        }
    }

    /**
     * open Database
     */
    public void openDB() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
    }

    /**
     * close Database
     * 方法废弃了,不需要每次都关闭数据库
     */
    public void closeDB() {
//        if (databaseHelper != null) {
//            OpenHelperManager.releaseHelper();
//            databaseHelper = null;
//        }
    }

    /**
     * 程序退出的时候才关闭数据库
     */
    public void close() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

}
