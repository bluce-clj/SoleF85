package com.dyaco.spiritbike.product_flavors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.dyaco.spiritbike.MainActivity;

import es.dmoral.toasty.Toasty;

public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		String action = intent.getAction();
	//	String localPkgName = context.getPackageName();//取得MyReceiver所在的App的包名
	//	Uri data = intent.getData();
//		String installedPkgName = data.getSchemeSpecificPart();//取得安装的Apk的包名，只在该app覆盖安装后自启动
		Log.d("更新", "action: " + action);
//		if ((action.equals(Intent.ACTION_PACKAGE_ADDED)
//				|| action.equals(Intent.ACTION_PACKAGE_REPLACED)) && installedPkgName.equals(localPkgName)) {
//			Intent launchIntent = new Intent(context, MainActivity.class);
//			launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(launchIntent);
//			Log.d("ININININ", "start: ");
//		}

		if (action.equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
			Intent launchIntent = new Intent(context, MainActivity.class);
			launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(launchIntent);
			Log.d("更新", "start: 重新啟動");
		}
	}

}
