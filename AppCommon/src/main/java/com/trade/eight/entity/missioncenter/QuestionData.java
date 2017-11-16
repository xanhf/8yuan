package com.trade.eight.entity.missioncenter;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/3.
 * 题目数据
 */

public class QuestionData implements Serializable{
    private int queSeqNum	;//	题目序号
    private String queDesc	;//	题目内容
    private String queExplain	;//	题目答案解释
    private int queAnsSeqNum	;//	题目正确答案的序号
    private List<AnswerData> queAnsList	;//	答案列表

    public class AnswerData {
        private int queAnsSeqNum;//	答案序号
        private String queAnsDesc;//	答案内容

        public int getQueAnsSeqNum() {
            return queAnsSeqNum;
        }

        public void setQueAnsSeqNum(int queAnsSeqNum) {
            this.queAnsSeqNum = queAnsSeqNum;
        }

        public String getQueAnsDesc() {
            return queAnsDesc;
        }

        public void setQueAnsDesc(String queAnsDesc) {
            this.queAnsDesc = queAnsDesc;
        }
    }

    public int getQueSeqNum() {
        return queSeqNum;
    }

    public void setQueSeqNum(int queSeqNum) {
        this.queSeqNum = queSeqNum;
    }

    public String getQueDesc() {
        return queDesc;
    }

    public void setQueDesc(String queDesc) {
        this.queDesc = queDesc;
    }

    public String getQueExplain() {
        return queExplain;
    }

    public void setQueExplain(String queExplain) {
        this.queExplain = queExplain;
    }

    public int getQueAnsSeqNum() {
        return queAnsSeqNum;
    }

    public void setQueAnsSeqNum(int queAnsSeqNum) {
        this.queAnsSeqNum = queAnsSeqNum;
    }

    public List<AnswerData> getQueAnsList() {
        return queAnsList;
    }

    public void setQueAnsList(List<AnswerData> queAnsList) {
        this.queAnsList = queAnsList;
    }
}
