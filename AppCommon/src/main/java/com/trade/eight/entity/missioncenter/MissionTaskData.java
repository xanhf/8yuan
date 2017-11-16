package com.trade.eight.entity.missioncenter;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/3.
 * 任务中心任务
 */

public class MissionTaskData implements Serializable{
    @DatabaseField
    private long taskId	;//	任务ID
    @DatabaseField
    private String taskIcon	;//	任务icon
    @DatabaseField
    private String taskTitle	;//	任务名称
    @DatabaseField
    private String taskDesc	;//	任务描述
    @DatabaseField
    private int userTaskStatus	;//	完成状态：1=完成，2=未完成
    @DatabaseField
    private int taskType	;//	任务跳转类型：1=答题，2=分享，3=协议跳转，4=H5跳转
    @DatabaseField
    private String taskLink	;//	跳转链接
    @DatabaseField
    private String taskLinkTitle	;//	跳转链接标题
    @DatabaseField
    private int taskVersion	;//	任务版本号
    @DatabaseField
    private int queTotalNum	;//	题目总数
    @DatabaseField
    private int queSucessNum	;//	已答题数量
    @DatabaseField
    private int queSucessPoints	;//	完成答题任务获取的积分值

    // 以下字段做数据库持久化使用
    @DatabaseField(generatedId = true)
    private int localId;//本地主键 delete删除时候需要
    @DatabaseField
    private int localqueSucessNum;// 本地已答题数量
    @DatabaseField
    private String  userId;// 本地已答题数量
    @DatabaseField
    private String questionData;//题目数据

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getLocalqueSucessNum() {
        return localqueSucessNum;
    }

    public void setLocalqueSucessNum(int localqueSucessNum) {
        this.localqueSucessNum = localqueSucessNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestionData() {
        return questionData;
    }

    public void setQuestionData(String questionData) {
        this.questionData = questionData;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskIcon() {
        return taskIcon;
    }

    public void setTaskIcon(String taskIcon) {
        this.taskIcon = taskIcon;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public int getUserTaskStatus() {
        return userTaskStatus;
    }

    public void setUserTaskStatus(int userTaskStatus) {
        this.userTaskStatus = userTaskStatus;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getTaskLink() {
        return taskLink;
    }

    public void setTaskLink(String taskLink) {
        this.taskLink = taskLink;
    }

    public String getTaskLinkTitle() {
        return taskLinkTitle;
    }

    public void setTaskLinkTitle(String taskLinkTitle) {
        this.taskLinkTitle = taskLinkTitle;
    }

    public int getTaskVersion() {
        return taskVersion;
    }

    public void setTaskVersion(int taskVersion) {
        this.taskVersion = taskVersion;
    }

    public int getQueTotalNum() {
        return queTotalNum;
    }

    public void setQueTotalNum(int queTotalNum) {
        this.queTotalNum = queTotalNum;
    }

    public int getQueSucessNum() {
        return queSucessNum;
    }

    public void setQueSucessNum(int queSucessNum) {
        this.queSucessNum = queSucessNum;
    }

    public int getQueSucessPoints() {
        return queSucessPoints;
    }

    public void setQueSucessPoints(int queSucessPoints) {
        this.queSucessPoints = queSucessPoints;
    }
}
