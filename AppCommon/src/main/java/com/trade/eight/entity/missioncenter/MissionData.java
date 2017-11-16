package com.trade.eight.entity.missioncenter;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/1.
 * 任务
 */

public class MissionData implements Serializable{
    private List<MissionBannerData> bannerList;
    private List<MissionTaskData> taskList;

    public List<MissionBannerData> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<MissionBannerData> bannerList) {
        this.bannerList = bannerList;
    }

    public List<MissionTaskData> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<MissionTaskData> taskList) {
        this.taskList = taskList;
    }
}
