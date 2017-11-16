package com.trade.eight.entity.unifypwd;

/**
 * 作者：Created by ocean
 * 时间：on 16/11/24.
 * 检查交易所密码是否正确(及设置统一交易)
 */

public class AccountCheckExchangePasswordData {
    /*检查交易所密码是否正确 start***********/
    private String token;//	登录token信息，如果检测成功，需要更新检查交易所的token信息（最后取消绑定不需要重新登录）
    private int exchangeId;//	交易所ID
    private String excode;//	交易所编号
    private String errorCode;//	交易所登录失败时的错误编码
    private String errorInfo;//	交易所登录失败时的错误信息
    /*检查交易所密码是否正确 end***********/

    /*设置统一交易密码 冗余字段start******************/
    private boolean success;
    /*设置统一交易密码 冗余字段end******************/

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
