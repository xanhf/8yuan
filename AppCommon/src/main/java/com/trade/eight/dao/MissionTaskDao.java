package com.trade.eight.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.trade.eight.app.SystemContext;
import com.trade.eight.entity.missioncenter.MissionTaskData;
import com.trade.eight.tools.Log;

import java.sql.SQLException;
import java.util.List;

public class MissionTaskDao extends BaseDao {
    private String TAG = "MissionTaskDao";
    private Context context;

    public MissionTaskDao(Context context) {
        this.context = context;
    }

    public boolean addOrUpdate(MissionTaskData missionTaskData) {
        if (missionTaskData == null)
            return false;
        SystemContext.getInstance(context).openDB();
        int count = -1;
        try {
            Dao<MissionTaskData, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getMissionTaskDataDao();
            List<MissionTaskData> list = dao.queryBuilder().query();
            if (list != null && !list.isEmpty()) {
                for (MissionTaskData missionTaskData1 : list) {
                    if (missionTaskData1.getTaskId() == missionTaskData.getTaskId() && missionTaskData1.getUserId() != null && missionTaskData1.getUserId().equals(missionTaskData.getUserId())) {
                        count = dao.update(missionTaskData);
                        Log.e(TAG, "update missionData");

                    }
                }
                if (count == -1) {
                    count = dao.create(missionTaskData);
                    Log.e(TAG, "add missionData");
                }
            } else {
                count = dao.create(missionTaskData);
                Log.e(TAG, "add missionData");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return count > 0;
    }

    public MissionTaskData queryMissionTaskData(long taskId, String userId) {
        SystemContext.getInstance(context).openDB();
        try {
            Dao<MissionTaskData, Integer> dao = SystemContext
                    .getInstance(context).getDatabaseHelper().getMissionTaskDataDao();
            List<MissionTaskData> list = dao.queryBuilder().where().eq("taskId", taskId).and().eq("userId", userId).query();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SystemContext.getInstance(context).closeDB();
        }
        return null;
    }
}
