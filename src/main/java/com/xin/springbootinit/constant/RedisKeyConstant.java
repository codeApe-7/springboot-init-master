package com.xin.springbootinit.constant;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/6/9 12:40
 */
public interface RedisKeyConstant {


    /**
     * 用户信息缓存
     */
    String REDIS_USER_INFO_KEY = "springinit:user_info:";

    //用户上传文件记录
    String REDIS_USER_UPLOAD_FILE_KEY = "springinit:user_upload_file:";

    //30天的过期时间，分钟
    Long REDIS_USER_UPLOAD_FILE_EXPIRE_TIME = 30L * 24 * 60;



}
