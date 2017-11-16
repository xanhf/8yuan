package com.trade.eight.entity.sharejoin;

import java.io.Serializable;

/**
 * Created by fangzhu on 2017/1/20.
 * 邀请好友代金券数据
 */

public class JoinVouchObj implements Serializable{
    private int regVoucherNum;
    private int inviteVoucherNum;

    public int getRegVoucherNum() {
        return regVoucherNum;
    }

    public void setRegVoucherNum(int regVoucherNum) {
        this.regVoucherNum = regVoucherNum;
    }

    public int getInviteVoucherNum() {
        return inviteVoucherNum;
    }

    public void setInviteVoucherNum(int inviteVoucherNum) {
        this.inviteVoucherNum = inviteVoucherNum;
    }
}
