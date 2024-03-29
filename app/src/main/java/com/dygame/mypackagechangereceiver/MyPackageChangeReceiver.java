package com.dygame.mypackagechangereceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
/**
 * 可以去移除一個 Package(Game) , 並且知道已經移除完畢(BroadcastReceiver)
 */
public class MyPackageChangeReceiver extends BroadcastReceiver
{
	protected String TAG = "PackageChangeReceiver" ;
	protected boolean isPackageOnDevice = false ;

    public void setTag(String sNewTag) { TAG = sNewTag ; }
	//知道已經移除完畢
	@Override
	public void onReceive(Context context, Intent intent)
    {
		String action=intent.getAction();		
		Log.i(TAG, "Broadcast Receiver Action: " + action);
	    Uri data = intent.getData();
	    //不要監聽 移除本身
	    if ("com.dygame.mypackagechangereceiver".equals(data)) return ;

	    //Broadcast Action: An existing application package has been removed from the device.
	    if(Intent.ACTION_PACKAGE_REMOVED.equalsIgnoreCase(action))
        {
		    Log.i(TAG, "Remove The DATA: " + data);
		    Toast.makeText(context, data.toString() + " is removed.", Toast.LENGTH_LONG).show();
	    }
	    //Broadcast Action: A new application package has been installed on the device.
        else if ("android.intent.action.PACKAGE_ADDED".equals(action))
        {
            Log.i(TAG, "Package Add The DATA: " + data);
        }
        //Broadcast Action: A new version of an application package has been installed, replacing an existing
        else if ("Intent.ACTION_PACKAGE_REPLACED".equals(action))
        {
            Log.i(TAG, "Package replace The DATA: " + data);
        }
	}
	
	//移除一個 Package(Game)
	public void removePackage(Context inContext , String sPackage)
    {
		//Check if package exists
	    try
        {
	        inContext.getPackageManager().getPackageInfo(sPackage, PackageManager.GET_META_DATA);
	        isPackageOnDevice = true;
	    } catch (NameNotFoundException e)
        {
	        // Do appropriate action when package not found on device. 
	        // it's nothing.	    
	    	Toast.makeText(inContext, "Package not found", Toast.LENGTH_LONG).show();
	    	Log.i(TAG, "Package not found="+sPackage);
	    }
		if(isPackageOnDevice == false) return ;
		//If exists, then prompt to delete.
		Intent uninstallIntent = new Intent();
		Uri packageUri = Uri.parse("package:"+sPackage);		
		//ACTION_UNINSTALL_PACKAGE is only availible to android-14 (i.e. Ice Cream Sandwich, Android 4.0).
		if (Build.VERSION.SDK_INT >= 14)
        {
            uninstallIntent.setAction(Intent.ACTION_UNINSTALL_PACKAGE);
		}
		else
        {
            uninstallIntent.setAction(Intent.ACTION_DELETE);
		}
		uninstallIntent.setData(packageUri);
		uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		inContext.startActivity(uninstallIntent);
    }
}
