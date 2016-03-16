package com.ngc123.emoface.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.easemob.chat.EMChatManager;
import com.ngc123.emoface.R;
import com.ngc123.emoface.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 * 
 */
public class AroundActivity extends Activity {

    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    
    public String username = EMChatManager.getInstance().getCurrentUser();
    
    MapView mMapView;
    BaiduMap mBaiduMap;
    private InfoWindow mInfoWindow;
    boolean isFirstLoc = true;// 是否首次定位
    
    //overlay
    private Marker[] mMarker;
    BitmapDescriptor bd;
    JSONObject[] jsonLoc;
    
    
    double latitude;
    double longitude;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_around);
        
        bd = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        
        
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                Log.d("5v", "click");
                OnInfoWindowClickListener listener = null;
                button.setText(marker.getExtraInfo().getString("username"));
                listener = new OnInfoWindowClickListener() {
                    public void onInfoWindowClick() {
                        String flog = getIntent().getStringExtra("flog");
                        Intent intent = new Intent();
                        if(flog.equals("p")){
                            intent.setClass(AroundActivity.this, StartGameActivity.class);
                        } else {
                            intent.setClass(AroundActivity.this, StartGameActivity.class);
                        }
                        
//                        Intent intent = new Intent(AroundActivity.this, StartGameActivity.class);
                        intent.putExtras(marker.getExtraInfo());
                        startActivity(intent);
                        
                        mBaiduMap.hideInfoWindow();
                    }
                };
                LatLng ll = marker.getPosition();
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }
    
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
                
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                new Thread(){
                    public void run(){
                        HttpUtils.postLoc(username, latitude, longitude);
                    }
    
                }.start();
                initMyOverlay(username);
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                float f = mBaiduMap.getMaxZoomLevel();
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, f-2);
                mBaiduMap.animateMapStatus(u);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
    
    
    private class ReadJSONFeedTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            return HttpUtils.readJSONFeed(urls[0]);
        }
        
        protected void onPostExecute(String result){
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(result);
                Log.i("JSON","Number of surveys in feed: "+jsonArray.length());
                jsonLoc = new JSONObject[jsonArray.length()];
                for(int i = 0;i < jsonArray.length();i++){
                    jsonLoc[i] = jsonArray.getJSONObject(i);
                }
                
                LatLng[] ll = new LatLng[jsonLoc.length];
                mMarker = new Marker[jsonLoc.length];
                OverlayOptions[] oo = new OverlayOptions[jsonLoc.length];
                for(int i = 0; i<jsonLoc.length;i++ ){
                    ll[i] = new LatLng(jsonLoc[i].getDouble("latitude"),jsonLoc[i].getDouble("longitude"));
                    Log.d("5v", ll[i].toString());
                    oo[i] = new MarkerOptions().position(ll[i]).icon(bd)
                            .zIndex(5);
                    mMarker[i] = (Marker) (mBaiduMap.addOverlay(oo[i]));
                    Bundle bundle = new Bundle();
                    bundle.putString("username", jsonLoc[i].getString("username"));
                    mMarker[i].setExtraInfo(bundle);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("5v", e.toString());
                e.printStackTrace();
            }
        }
        
    }
    
    public void initMyOverlay(String username){
        String url = HttpUtils.endpoint+"/index.php/Upload/getNearBy/username/"+username;
        new ReadJSONFeedTask().execute(url);
    }
    
    
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        bd.recycle();
        super.onDestroy();
    }

}
