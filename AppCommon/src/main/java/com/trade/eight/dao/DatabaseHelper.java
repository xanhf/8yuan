package com.trade.eight.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.missioncenter.MissionTaskData;
import com.trade.eight.entity.trude.UserInfo;

import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "datacache.db";

    // 改变数据库版本
    private static final int DATABASE_VERSION = 9;

    // the DAO object we use to access the SimpleData table
    private Dao<Optional, Integer> optionalDao = null;
    private Dao<UserInfo, Integer> userInfoDao = null;
    private Dao<MissionTaskData, Integer> missionTaskDataDao = null;

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * This is called when the database is first created. Usually you should
     * call createTable statements here to create the tables that will store
     * your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        Log.i(DatabaseHelper.class.getName(), "onCreate");
        createTable();

    }

    private void createTable() {
        try {
            TableUtils.createTableIfNotExists(connectionSource,
                    Optional.class);

            TableUtils.createTableIfNotExists(connectionSource,
                    UserInfo.class);

            TableUtils.createTableIfNotExists(connectionSource,
                    MissionTaskData.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * This is called when your application is upgraded and it has a higher
     * version number. This allows you to adjust the various data to match the
     * new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Optional.class, true);
            TableUtils.dropTable(connectionSource, UserInfo.class, true);
            onCreate(db, connectionSource);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        optionalDao = null;
        userInfoDao = null;
        missionTaskDataDao = null;
    }

    /**
     * Returns the Database Access Object (DAO)
     */
    public Dao<Optional, Integer> getOptionalDao() throws SQLException {
        if (optionalDao == null) {
            optionalDao = getDao(Optional.class);
        }
        return optionalDao;
    }

    /**
     * Returns the Database Access Object (DAO)
     */
    public Dao<UserInfo, Integer> getUserInfoDao() throws SQLException {
        if (userInfoDao == null) {
            userInfoDao = getDao(UserInfo.class);
        }
        return userInfoDao;
    }

    public Dao<MissionTaskData, Integer> getMissionTaskDataDao() throws SQLException {
        if (missionTaskDataDao == null) {
            missionTaskDataDao = getDao(MissionTaskData.class);
        }
        return missionTaskDataDao;
    }


}
