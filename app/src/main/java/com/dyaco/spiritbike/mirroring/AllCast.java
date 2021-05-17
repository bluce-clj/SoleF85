package com.dyaco.spiritbike.mirroring;

import android.content.Context;
import android.util.Log;

import com.hpplay.sdk.sink.api.IAPI;
import com.hpplay.sdk.sink.api.IMiniProgramQRListener;
import com.hpplay.sdk.sink.api.IQRListener;
import com.hpplay.sdk.sink.api.IServerListener;
import com.hpplay.sdk.sink.api.LelinkCast;
import com.hpplay.sdk.sink.api.Option;
import com.hpplay.sdk.sink.api.ServerInfo;
import com.hpplay.sdk.sink.dmp.DeviceBean;
import com.hpplay.sdk.sink.dmp.OnDMPListener;
import com.hpplay.sdk.sink.feature.IAuthCodeCallback;
import com.hpplay.sdk.sink.playercontrol.IPlayerActiveControl;

public class AllCast {
    private final static String TAG = "AllCast";
    private LelinkCast mLelinkCast;
    private IPlayerActiveControl iPlayerActiveControl;
    public AllCast(Context context, String appKey, String appSecret) {
        mLelinkCast = new LelinkCast(context, appKey, appSecret);
    }

    /**
     * 设置设备名称，服务发布前调用有效
     *
     * @param deviceName
     */
    public void setDeviceName(String deviceName) {
        setOption(IAPI.OPTION_DEIVCENAME, deviceName);
    }

    /**
     * 修改设备名称，服务发布后，可调用此接口修改设备名称
     *
     * @param deviceName 服务名称
     */
    public void changeDeviceName(String deviceName) {
        performAction(IAPI.ACTION_CHANGEDEVICENAME, deviceName);
    }

    /**
     * 开启投屏服务
     */
    public void startServer() {
        performAction(IAPI.ACTION_STARTSERVER);
    }

    /**
     * 停止投屏服务
     */
    public void stopServer() {
        performAction(IAPI.ACTION_STOPSERVER);
    }

    public IPlayerActiveControl getIPlayer() {
        return iPlayerActiveControl = getOption(Option.LEBO_OPTION_32, IPlayerActiveControl.class);
    }

    /**
     * 镜像 是否显示帧率
     *
     * @param show
     */
    public void setDisplayFrameRateSwitch(boolean show) {
        setOption(IAPI.OPTION_SHOWFPS, show);
    }

    /**
     * 镜像 分辨率
     *
     * @param resolution
     */
    public void setMirrorResolution(String resolution) {
        setOption(IAPI.OPTION_RESOLUTION, resolution);
    }

    /**
     * 镜像 最大帧率
     *
     * @param fps 30 或 60 或 0，0表示自动
     */
    public void setMaxFps(int fps) {
        setOption(IAPI.OPTION_MAXFPS, fps);
    }

    /**
     * 设置苹果助手状态
     *
     * @param status
     */
    public void setLelinkFpAssistant(int status) {
        setOption(IAPI.OPTION_LELINK_FP_ASSISTANT, status);
    }

    /**
     * 镜像同步模式
     *
     *@param type
     */
    public void setMirrorSync(int type) {
        setOption(IAPI.OPTION_MIRROR_AUDIO_VIDEO_SYNC, type);
    }

    /**
     * 设置二维码监听，二维码信息可用于设备扫码连接
     *
     * @param qrListener
     */
    public void setQRListener(IQRListener qrListener) {
        setOption(IAPI.OPTION_QRLISTENER, qrListener);
    }

    /**
     * 设置小程序二维码监听
     *
     * @param miniProgramQRListener
     */
    public void setMiniProgramQRListener(IMiniProgramQRListener miniProgramQRListener) {
        setOption(IAPI.OPTION_MINI_PROGRAM_QRLISTENER, miniProgramQRListener);
    }

    /**
     * 设置投屏模式，目前设置仅对公网投屏有效，支持的投屏模式有自由模式/防骚扰模式/独占模式。默认自由模式
     *
     * @param preemptModel {@link IAPI#PREEMPT_MODE_FREE,IAPI#PREEMPT_MODE_AVOID_HARASS,IAPI#PREEMPT_MODE_RESTRICTED}
     * @param netType      {@link IAPI#PREEMPT_CLOUD,IAPI#PREEMPT_LOCAL}
     */
    public void setPreemptModel(int preemptModel, int netType) {
        setOption(IAPI.OPTION_PREEMPTMODE, preemptModel, netType);
    }

    /**
     * 在服务启动前设置生效，服务启动后设置则下次生效
     * 设置投屏码模式,目前支持 无密码模式，固定密码模式，随机密码模式。默认 无密码模式
     *
     * @param authMode
     */
    public void setAuthMode(int authMode, String pwd) {
        setOption(IAPI.OPTION_AUTHMODE, authMode, pwd);
    }

    /**
     * 设置投屏码回调监听
     *
     * @param authCallback
     */
    public void setAuthCodeCallback(IAuthCodeCallback authCallback) {
        setOption(IAPI.OPTION_AUTHCALLBACK, authCallback);
    }

    /**
     * 服务发布成功可调用
     *
     * @param authMode authMode
     * @param pwd
     */
    public void changeAuthMode(int authMode, String pwd) {
        performAction(IAPI.ACTION_CHANGEAUTHMODE, authMode, pwd);
    }

    /**
     * 显示防骚扰模式下记录的发送端设备列表
     *
     * @param netType
     */
    public void showDeviceList(int netType) {
        performAction(IAPI.ACTION_USER_MANAGER, netType);
    }

    /**
     * 设置 SDK 内部语言版本 支持：中、英、繁
     *
     * @param language
     */
    public void setLanguage(int language) {
        setOption(IAPI.OPTION_LANGUAGE, language);
    }

    public void choosePlayer(int type) {
        setOption(IAPI.OPTION_PLAYER_TYPE, type);
    }

    public void setSurfaceType(int surfaceType) {
        setOption(IAPI.OPTION_SURFACE_TYPE, surfaceType);
    }

    /**
     * 镜像旋转时，是否强制重置播放器。
     * 部分芯片，在镜像旋转时需要强制重置播放器，当镜像旋转屏幕出现异常时，可调用此接口，在镜像旋转时，强制重置播放器
     *
     * @param mode
     */
    public void resetPlayerWhenMirror(int mode) {
        setOption(IAPI.OPTION_RESETPLAYERWHENMIRROR, mode);
    }

    /**
     * 设置显示模式
     * 正常模式、全屏模式（拉伸）、全屏裁剪（填充）
     *
     */
    public void setDisplayMode(int mode) {
        setOption(IAPI.OPTION_DISPLAYMODE, mode);
    }

    /**
     * 投屏服务发状态回调
     *
     * @param serverListener
     */
    public void setServerListener(IServerListener serverListener) {
        setOption(IAPI.OPTION_SERVERLISTENER, serverListener);
    }

    public int setOption(int option, Object... values) {
        Object result = mLelinkCast.setOption(option, values);
        int callResult = Integer.parseInt(result.toString());
        if (callResult == IAPI.INVALID_CALL) {
            Log.w(TAG, "setOption invalid call, option: " + option);
        }
        return callResult;
    }

    public int performAction(int action, Object... values) {
        Object result = mLelinkCast.performAction(action, values);
        int callResult = Integer.parseInt(result.toString());
        if (callResult == IAPI.INVALID_CALL) {
            Log.w(TAG, "performAction invalid call, action: " + action);
        }
        return callResult;
    }

    /**
     * 获取投屏服务发布信息，在服务发布成功之后可通过此接口获取服务信息
     *
     * @return
     */
    public ServerInfo getServerInfo() {
        return getOption(IAPI.OPTION_SERVERINFO, ServerInfo.class);
    }

    /*****************  DMP 相关 start *********************/

    /**
     * 启动DMP服务
     */
    public void startDMPServer() {
        performAction(IAPI.ACTION_DMP_START);
    }

    /**
     * 停止DMP服务
     */
    public void stopDMPServer() {
        performAction(IAPI.ACTION_DMP_STOP);
    }

    /**
     * 搜索DMP设备
     */
    public void searchDMPDevices() {
        performAction(IAPI.ACTION_DMP_SEARCH);
    }

    /**
     * 浏览DMP设备根目录
     *
     * @param deviceBean
     */
    public void browseDMPDevice(DeviceBean deviceBean) {
        performAction(IAPI.ACTION_DMP_BROWSEDEVICE, deviceBean);
    }

    /**
     * 浏览文件夹
     *
     * @param actionUrl
     * @param folderId
     */
    public void browseFolder(String actionUrl, String folderId) {
        performAction(IAPI.ACTION_DMP_BROWSEFOLDER, actionUrl, folderId);
    }

    /**
     * 设置DMP回调
     *
     * @param dmpListener
     */
    public void setDMPListener(OnDMPListener dmpListener) {
        mLelinkCast.setOption(IAPI.OPTION_DMPLISTENER, dmpListener);
    }

    /*****************  DMP 相关 end *********************/

    public <T> T getOption(int option, Class<T> classOfT) {
        if (mLelinkCast != null) {
            Object object = mLelinkCast.getOption(option);
            try {
                return classOfT.cast(object);
            } catch (Exception e) {
                Log.w(TAG, e);
            }
        }
        return null;
    }

}
