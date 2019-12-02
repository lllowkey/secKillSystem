package com.lyr.secKill.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * Created by WIN10 on 2019/11/28.
 */
public interface CacheService {
    //存方法
    void setCommonCache(String key, Object value);

    //取方法
    Object getFromCommonCache(String key);


}
