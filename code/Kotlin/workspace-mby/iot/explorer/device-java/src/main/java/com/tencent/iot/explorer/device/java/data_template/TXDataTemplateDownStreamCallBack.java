package com.tencent.iot.explorer.device.java.data_template;

import org.json.JSONObject;

public abstract class TXDataTemplateDownStreamCallBack {
    public abstract void onReplyCallBack(String msg);

    public abstract void onGetStatusReplyCallBack(JSONObject data);

    public abstract JSONObject onControlCallBack(JSONObject msg);

    public abstract JSONObject onActionCallBack(String actionId, JSONObject params);

    /**
     * 用户在腾讯连连小程序或腾讯连连App删除设备时由云端发送给设备的通知消息，便于设备重置或网关类设备清除子设备数据。
     *
     * @param msg 用户在腾讯连连小程序或腾讯连连App删除设备时由云端发送给设备的通知消息
     */
    public abstract void onUnbindDeviceCallBack(String msg);

    /**
     * 用户在腾讯连连小程序或腾讯连连App绑定设备时由云端发送给设备的通知消息，设备接收后可根据业务需求自行处理
     *
     * @param msg 用户在腾讯连连小程序或腾讯连连App绑定设备时由云端发送给设备的通知消息
     */
    public abstract void onBindDeviceCallBack(String msg);
}
