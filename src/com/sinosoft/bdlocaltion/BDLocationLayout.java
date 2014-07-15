/**  
b * All rights Reserved, Designed By Android_Robot   
 * @Title:  MyButton.java   
 * @Package com.sinosoft.bdlocaltion   
 * @Description:    TODO(用一句话描述该文件做什么)   
 * @author: gjy     
 * @date:   2014-7-14 上午10:54:59   
 * @version V1.0     
 */
package com.sinosoft.bdlocaltion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.sinosoft.baidulocationlibrary.R;
import com.sinosoft.library.progressbar.LoadingProgressBar;

/**
 * @ClassName: MyButton
 * @Description:TODO(对百度地图定位的封装)
 * @author: gjy
 * @date: 2014-7-14 上午10:54:59
 * 
 */

public class BDLocationLayout extends RelativeLayout implements OnClickListener {

	private ImageButton bdLibraryLocationBtn;
	private TextView bdLibraryLocationTV;
	private LoadingProgressBar bdLibraryLocationProgress;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private SharedPreferences sharedPreferences;
	private Context context;
	private ConnectivityManager conn;
	private RelativeLayout bdLibraryLocationLayout;

	/**
	 * @Title: BDLocationLayout
	 * @Description: TODO(构造函数)
	 * @param: @param context
	 * @param: @param attrs
	 * @throws
	 */
	public BDLocationLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(
				R.layout.bdlibrary_location_layout, this);
		bdLibraryLocationBtn = (ImageButton) findViewById(R.id.bdlibrary_location_btn);
		bdLibraryLocationTV = (TextView) findViewById(R.id.bdlibrary_location_textview);
		bdLibraryLocationProgress = (LoadingProgressBar) findViewById(R.id.progressBar1);
		bdLibraryLocationLayout = (RelativeLayout) findViewById(R.id.bdlibrary_location_layout);
		bdLibraryLocationLayout.setOnClickListener(this);
		bdLibraryLocationBtn.setOnClickListener(this);
		sharedPreferences = getContext().getSharedPreferences("shop",
				Activity.MODE_PRIVATE);

		startLocation();

	}

	/**
	 * @Title: initLocation
	 * @Description: TODO(开始定位)
	 * @param:
	 * @return: void
	 * @throws
	 */
	public void startLocation() {
		// TODO Auto-generated method stub
		System.out.println("开始定位");
		if (isNetworkConnected()) {
			mLocationClient = new LocationClient(context); // 声明LocationClient类
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
			option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
			option.setScanSpan(5000);
			option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
			mLocationClient.setLocOption(option);
			mLocationClient.registerLocationListener(myListener);
			if (mLocationClient != null && mLocationClient.isStarted()) {
				mLocationClient.requestLocation();
			} else
				Log.d("LocSDK3", "locClient is null or not started");
			mLocationClient.start();
			bdLibraryLocationProgress.setVisibility(View.VISIBLE);
			bdLibraryLocationBtn.setVisibility(View.GONE);
			bdLibraryLocationTV.setText("正在定位……");
		} else {
			Editor editor = sharedPreferences.edit();
			editor.putString("address", String.valueOf("定位失败，请刷新"));
			editor.putString("lon", String.valueOf("0"));
			editor.putString("lat", String.valueOf("0"));
			editor.commit();
			endLocation();
		}
	}

	public void endLocation() {
		bdLibraryLocationProgress.setVisibility(View.GONE);
		bdLibraryLocationBtn.setVisibility(View.VISIBLE);
		bdLibraryLocationTV.setText(sharedPreferences.getString("address",
				"定位失败，请刷新"));
		mLocationClient.unRegisterLocationListener(myListener);
	}

	/**
	 * Title: onClick Description:
	 * 
	 * @param v
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.bdlibrary_location_btn
				|| v.getId() == R.id.bdlibrary_location_layout) {
			startLocation();
		}
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			System.out.println("定位结果");
			Editor editor = sharedPreferences.edit();
			if (location == null) {
				endLocation();
			} else {
				if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
					editor.putString("lat",
							String.valueOf(location.getLatitude()));
					editor.putString("lon",
							String.valueOf(location.getLongitude()));
					editor.putString("address",
							String.valueOf(location.getAddrStr()));
				} else {
					editor.putString("address", String.valueOf("定位失败，请刷新"));
					editor.putString("lon", String.valueOf("0"));
					editor.putString("lat", String.valueOf("0"));
				}
				editor.commit();
				endLocation();
			}

		}

	}

	protected boolean isNetworkConnected() {
		// TODO Auto-generated method stub
		conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conn.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}
}
