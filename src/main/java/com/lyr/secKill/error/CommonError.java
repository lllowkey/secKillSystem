package com.lyr.secKill.error;

/**
 * Created by WIN10 on 2019/10/10.
 */
public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);


}
