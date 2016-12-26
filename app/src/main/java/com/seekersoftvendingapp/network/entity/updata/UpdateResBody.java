package com.seekersoftvendingapp.network.entity.updata;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/12/26.
 */

public class UpdateResBody implements Serializable {
    public int status;//200
    public String message;
    public Update data = new Update();
    public String server_time;

    public class Update implements Serializable {
        public boolean result;
        public String deviceId;
        public String url;
        public int version;
        public String objectId;
        public String createdAt;
        public String updateAt;
    }

}
