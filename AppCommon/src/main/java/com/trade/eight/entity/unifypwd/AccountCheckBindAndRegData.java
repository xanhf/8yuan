package com.trade.eight.entity.unifypwd;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 16/11/24.
 * 是否统一交易密码以及交易所注册情况
 */

public class AccountCheckBindAndRegData implements Serializable {
    private boolean bind;// 是否设置统一交易密码
    private List<ExchangeRegInfo> exchanges;

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public List<ExchangeRegInfo> getExchanges() {
        return exchanges;
    }

    public void setExchanges(List<ExchangeRegInfo> exchanges) {
        this.exchanges = exchanges;
    }

    public class ExchangeRegInfo implements Serializable {
        private boolean reg;//是否注册交易所
        private boolean bind;//是否绑定（目前需求这个返回值没用）
        private int exchangeId;//交易所ID,1:广贵、2:哈贵，3：农交所
        private String excode;//交易所编号

        public boolean isReg() {
            return reg;
        }

        public void setReg(boolean reg) {
            this.reg = reg;
        }

        public boolean isBind() {
            return bind;
        }

        public void setBind(boolean bind) {
            this.bind = bind;
        }

        public int getExchangeId() {
            return exchangeId;
        }

        public void setExchangeId(int exchangeId) {
            this.exchangeId = exchangeId;
        }

        public String getExcode() {
            return excode;
        }

        public void setExcode(String excode) {
            this.excode = excode;
        }
    }

    /**
     * 是否为新用户 只要注册过一家  视为老用户
     *
     * @return
     */
    public boolean isNewAccount() {
        if (exchanges != null && exchanges.size() > 0) {
            for (ExchangeRegInfo exchangeRegInfo : exchanges) {
                if (exchangeRegInfo.isReg()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 根据交易所 获取交易所注册状态
     *
     * @param source
     * @return
     */
    public ExchangeRegInfo getExchangeRegInfoBySource(String source) {
        for (ExchangeRegInfo exchangeRegInfo : exchanges) {
            if (exchangeRegInfo.getExcode().equals(source)) {
                return exchangeRegInfo;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public String getExchangePwdInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("已设置");
        int regCount = 0;
        for (ExchangeRegInfo exchangeRegInfo : exchanges) {
            if (exchangeRegInfo.isReg()) {
                regCount++;
            }
        }

        if (regCount == 1) {
            for (ExchangeRegInfo exchangeRegInfo : exchanges) {
                if (exchangeRegInfo.isReg()) {
                    switch (exchangeRegInfo.getExchangeId()) {
                        case 1:
                            sb.append("广贵所");
                            break;
                        case 2:
                            sb.append("哈贵所");
                            break;
                        case 3:
                            sb.append("农交所");
                            break;
                    }
                }
            }
            sb.append("交易密码");
        } else {
            int index = 0;
            for (ExchangeRegInfo exchangeRegInfo : exchanges) {
                if (exchangeRegInfo.isReg()) {
                    switch (exchangeRegInfo.getExchangeId()) {
                        case 1:
                            if (index == 0) {
                                sb.append("广贵");
                            } else {
                                sb.append("|广贵");
                            }
                            break;
                        case 2:
                            if (index == 0) {
                                sb.append("哈贵");
                            } else {
                                sb.append("|哈贵");
                            }
                            break;
                        case 3:
                            if (index == 0) {
                                sb.append("农交所");
                            } else {
                                sb.append("|农交所");
                            }
                            break;
                    }
                    index++;
                }
            }
            sb.append("所交易密码");
        }
        return sb.toString();
    }
}
