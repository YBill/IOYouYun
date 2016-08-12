package com.ioyouyun.contacts.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.contacts.ContactDetailActivity;
import com.ioyouyun.contacts.biz.ContactRequest;
import com.ioyouyun.contacts.biz.ContactRequestImpl;
import com.ioyouyun.contacts.biz.OnContactListener;
import com.ioyouyun.contacts.model.NearbyUserEntity;
import com.ioyouyun.contacts.view.ContactView;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.LocationUtils;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.utils.ParseJson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 卫彪 on 2016/7/5.
 */
public class ContactsPresenter extends BasePresenter<ContactView> implements AMapLocationListener {

    private ContactRequest request;
    private List<NearbyUserEntity> nearbyUserEntityList = new ArrayList<>();
    private Activity activity;
    private static final int SUCCESS = 1000;
    private static final int FAILD = 1001;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    public ContactsPresenter(Activity activity) {
        this.activity = activity;
        request = new ContactRequestImpl();

        locationClient = new AMapLocationClient(activity.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位监听
        locationClient.setLocationListener(this);
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置为单次定位
        locationOption.setOnceLocation(true);
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(false);
        // 设置是否开启缓存
//        locationOption.setLocationCacheEnable(true);
        //设置是否强制刷新WIFI，默认为强制刷新
//        locationOption.setOnceLocationLatest(true);
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
//        locationOption.setWifiActiveScan(true);
        /**
         *  设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
         *  只有持续定位设置定位间隔才有效，单次定位无效
         */
//        locationOption.setInterval(2000);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation) {
            Message msg = mHandler.obtainMessage();
            msg.obj = aMapLocation;
            msg.what = LocationUtils.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }
    }

    Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case LocationUtils.MSG_LOCATION_START:
                    Logger.v("正在定位...");
                    break;
                //定位完成
                case LocationUtils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    AMapLocation location = LocationUtils.getLocation(loc);
                    if (location != null) {
                        Logger.v("定位成功");
                        // 停止定位
                        locationClient.stopLocation();
                        mHandler.sendEmptyMessage(LocationUtils.MSG_LOCATION_STOP);

                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        getNearbyUsers(longitude, latitude, 1000);
                    }else{
                        FunctionUtil.toastMessage("定位失败");
                        if(mView != null)
                            mView.hideLoading();
                    }
                    break;
                case LocationUtils.MSG_LOCATION_STOP:
                    Logger.v("定位停止");
                    break;
                case SUCCESS:
                    if (mView != null) {
                        mView.hideLoading();
                        mView.setListView(nearbyUserEntityList);
                    }
                    break;
                case FAILD:
                    FunctionUtil.toastMessage("获取联系人失败");
                    if(mView != null)
                        mView.hideLoading();
                    break;
                default:
                    break;
            }
        }
    };

    public List<NearbyUserEntity> getNearbyUserEntityList() {
        return nearbyUserEntityList;
    }

    public void setNearbyUserEntityList(List<NearbyUserEntity> nearbyUserEntityList) {
        this.nearbyUserEntityList = nearbyUserEntityList;
    }

    /**
     * 定位
     */
    public void getLocation() {
        if(mView != null){
            mView.showLoading();
        }

        // 启动定位
        locationClient.startLocation();
        mHandler.sendEmptyMessage(LocationUtils.MSG_LOCATION_START);
    }

    /**
     * 注销
     */
    public void onDestory() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    public void onItemClick(int position) {
        Intent intent = new Intent(activity, ContactDetailActivity.class);
        intent.putExtra("toUid", nearbyUserEntityList.get(position).getId());
        intent.putExtra("nickName", nearbyUserEntityList.get(position).getNickname());
        activity.startActivity(intent);
    }

    /**
     * 获取附近的人,会上报自己的位置
     *
     * @param longitude
     * @param latitude
     * @param range
     */
    private void getNearbyUsers(double longitude, double latitude, long range) {
        request.getNearbyUsers(FunctionUtil.uid, longitude, latitude, range, new OnContactListener() {
            @Override
            public void onSuccess(String response) {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject obj = ParseJson.parseCommonObject(response);
                    JSONArray array = null;
                    if (obj != null) {
                        array = obj.optJSONArray("users");
                    }
                    nearbyUserEntityList.clear();
                    List<NearbyUserEntity> list = ParseJson.parseJson2ListT(array, NearbyUserEntity.class);
                    // 定位移除自己
                    for (int i = 0; i < list.size(); i++){
                        if(FunctionUtil.uid.equals(list.get(i).getId())){
                            list.remove(i);
                            break;
                        }
                    }
                    if (list != null)
                        nearbyUserEntityList.addAll(list);
                    mHandler.sendEmptyMessage(SUCCESS);
                }
            }

            @Override
            public void onFaild() {
                mHandler.sendEmptyMessage(FAILD);
            }
        });
    }

    /**
     * 向服务器上报位置
     *
     * @param longitude
     * @param latitude
     */
    private void addLocation(double longitude, double latitude) {
        request.addLocation(FunctionUtil.uid, longitude, latitude, 0, new OnContactListener() {
            @Override
            public void onSuccess(String response) {
                boolean result = ParseJson.parseCommonResult2(response);
                if (result) {
                    Logger.v("地理位置上报成功");
                }
            }

            @Override
            public void onFaild() {

            }
        });
    }

}
