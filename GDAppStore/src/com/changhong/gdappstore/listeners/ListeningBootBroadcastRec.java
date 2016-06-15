package com.changhong.gdappstore.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.changhong.gdappstore.service.SilentInstallService;

/** 
 * @author  cym  
 * @date 创建时间：2016年6月13日 上午11:38:12 
 * @version 1.0 
 * @parameter   
 * */
public class ListeningBootBroadcastRec extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {  
			context.startService(new Intent(context, SilentInstallService.class));
        }  
	}

}
