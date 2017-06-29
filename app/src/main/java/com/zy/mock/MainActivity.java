package com.zy.mock;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TelephonyManager mTelephonyManager;

    private TextView mTextView;

    public String mImei;
    public String mModel;
    public String mVersion;

    private EditText mIMEIView;
    private EditText mModelView;
    private EditText mReleaseVersionView;
    private EditText mIpView;
    private EditText mPortView;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        mIMEIView = (EditText) findViewById(R.id.et_imei);
        mModelView = (EditText) findViewById(R.id.et_model);
        mReleaseVersionView = (EditText) findViewById(R.id.et_release);
        mIpView = (EditText) findViewById(R.id.et_ip);
        mPortView = (EditText) findViewById(R.id.et_port);

        mIMEIView.setText(mTelephonyManager.getDeviceId());
        mModelView.setText(Build.MODEL);
        mReleaseVersionView.setText(Build.VERSION.RELEASE);

        mTextView = (TextView) findViewById(R.id.text);


        findViewById(R.id.btn_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextView.setText(getInfo());
            }
        });

        mTextView = (TextView) findViewById(R.id.text);


        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if(location!=null){
            //不为空,显示地理位置经纬度
            showLocation(location);
        }
        //监视地理位置变化
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 3000, 1, locationListener);
    }

    /**
     * 显示地理位置经度和纬度信息
     * @param location
     */
    private void showLocation(Location location){
        String locationStr = "纬度：" + location.getLatitude() + ",经度：" + location.getLongitude();
        mTextView.setText(locationStr);
        Log.i(TAG, "location:"+ locationStr);
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */

    LocationListener locationListener =  new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            showLocation(location);

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(0, 0, 0, "保存");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        save();
        return super.onOptionsItemSelected(item);
    }

    public void save() {
        setProxy();
        mImei = mIMEIView.getText().toString().trim();
        mModel = mModelView.getText().toString().trim();
        mVersion = mReleaseVersionView.getText().toString().trim();

        SharedPreferences pre = this.getSharedPreferences("prefs",
                Context.MODE_WORLD_READABLE);
        Log.d(TAG, "save.imei: " + mImei);
        pre.edit().putString("imei", mImei).apply();
        pre.edit().putString("model", mModel).apply();
        pre.edit().putString("version", mVersion).apply();

        pre.edit().putString("lan", "38.2697").apply();
        pre.edit().putString("lon", "116.8909").apply();

        getInfo();
    }

    private void setProxy() {
        String host = mIpView.getText().toString().trim();
        String portStr = mPortView.getText().toString().trim();
        if (TextUtils.isEmpty(host)) {
            return;
        }

        if (TextUtils.isEmpty(portStr)) {
            return;
        }
        int port = Integer.valueOf(portStr);
        Log.d(TAG, "host: " + host + ",post:" + port);
        WifiManager wifiManager =(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            return;
        }
        List<WifiConfiguration> configurationList = wifiManager.getConfiguredNetworks();
        WifiConfiguration configuration = null;
        int cur = wifiManager.getConnectionInfo().getNetworkId();
        for (int i = 0; i < configurationList.size(); ++i) {
            WifiConfiguration wifiConfiguration = configurationList.get(i);
            if (wifiConfiguration.networkId == cur) {
                configuration = wifiConfiguration;
            }
        }

        if (configuration == null) {
            Log.d(TAG, "configuration is null ");
            mTextView.setText("configuration is null ");
            return;
        }

        //get the link properties from the wifi configuration
        try {
            Object linkProperties = getFieldObject(configuration, "linkProperties");
            if (linkProperties == null) {
                Log.d(TAG, "linkProperties is null ");
                mTextView.setText("linkProperties is null");
                return;
            }

            //获取类 LinkProperties的setHttpProxy方法
            Class<?> proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            Class<?>[] setHttpProxyParams = new Class[1];
            setHttpProxyParams[0] = proxyPropertiesClass;
            Class<?> lpClass = Class.forName("android.net.LinkProperties");

            Method setHttpProxy = lpClass.getDeclaredMethod("setHttpProxy",setHttpProxyParams);setHttpProxy.setAccessible(true);

            // 获取类 ProxyProperties的构造函数
            Constructor<?> proxyPropertiesCtor = proxyPropertiesClass.getConstructor(String.class,int.class, String.class);
            // 实例化类ProxyProperties
            Object proxySettings =proxyPropertiesCtor.newInstance(host, port, null);
            //pass the new object to setHttpProxy
            Object[] params = new Object[1];
            params[0] = proxySettings;
            setHttpProxy.invoke(linkProperties, params);
            setEnumField(configuration, "STATIC", "proxySettings");

            //save the settings
            wifiManager.updateNetwork(configuration);
            wifiManager.disconnect();
            wifiManager.reconnect();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            mTextView.setText(e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            mTextView.setText(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            mTextView.setText(e.getMessage());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            mTextView.setText(e.getMessage());
        } catch (InstantiationException e) {
            e.printStackTrace();
            mTextView.setText(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            mTextView.setText(e.getMessage());
        }
    }

    public void setEnumField(Object obj, String value, String name)throws SecurityException, NoSuchFieldException,IllegalArgumentException, IllegalAccessException{
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }

    // getField只能获取类的public 字段.
    public Object getFieldObject(Object obj, String name)throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        Field f =
                obj.getClass().getField(name);
        Object out = f.get(obj); return out;
    }

    private String getInfo() {
        String result = "";
        //获取IMEI
        String deviceId = mTelephonyManager.getDeviceId();
        Log.d(TAG, "deviceId: " + deviceId);
        result += "deviceID=" + deviceId + "\n";

        String number = mTelephonyManager.getLine1Number();
        Log.d(TAG, "number: " + number);
//        result += "deviceID=" + deviceId + "\n";

        String deviceSoftwareVersion = mTelephonyManager.getDeviceSoftwareVersion();
        Log.d(TAG, "deviceSoftwareVersion: " + deviceSoftwareVersion);
//        result += "deviceID=" + deviceId + "\n";

        String model = Build.MODEL;
        Log.d(TAG, "model: " + model);
        result += "model=" + model + "\n";

        String manufacturer = Build.MANUFACTURER;
        Log.d(TAG, "manufacturer: " + manufacturer);
        result += "manufacturer=" + manufacturer + "\n";

        String product = Build.PRODUCT;
        Log.d(TAG, "product: " + product);
        result += "product=" + product + "\n";

        String brand = Build.BRAND;
        Log.d(TAG, "brand: " + brand);
        result += "brand=" + brand + "\n";

        String release = Build.VERSION.RELEASE;
        Log.d(TAG, "release version: " + release);
        result += "release=" + release + "\n";

        int sdk = Build.VERSION.SDK_INT;
        Log.d(TAG, "SDK: " + sdk);
//        result += "deviceID=" + deviceId + "\n";

        //在wifi未开启状态下，仍然可以获取MAC地址，但是IP地址必须在已连接状态下否则为0
//        String ip = null;
//        WifiManager wifiMgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
//        if (null != info) {
//            ip = int2ip(info.getIpAddress());
//        }

//        Log.d(TAG, "MacAddress: " + getMac());
//        Log.d(TAG, "IP: " + ip);

        return result;
    }
}
