package com.dygame.mypackagechangereceiver;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity
{
    protected String TAG = "" ;
    protected Button quitButton ;
    protected Button installButton ;
    protected Button uninstallButton ;
    // 廣播
    protected MyPackageChangeReceiver mPackageChangeReceiver = null ;//接收廣播以便知道 Package已經移除完畢
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Uncaught Exception Handler(Crash Exception)
        MyCrashHandler pCrashHandler = MyCrashHandler.getInstance();
        pCrashHandler.init(getApplicationContext());
        TAG = pCrashHandler.getTag() ;
        //find resources
        installButton = (Button)findViewById(R.id.button1) ;
        uninstallButton = (Button)findViewById(R.id.button2) ;
        quitButton = (Button)findViewById(R.id.button3) ;
        //set OnClickListener
        //安裝
        installButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                File file = null;
                try
                {
                    InputStream is = getApplicationContext().getAssets().open("com.dygame.rushhours.apk");
                    //轉移到可讀寫文件目錄下
                    file = new File(Environment.getExternalStorageDirectory().getPath() + "/temp.apk");
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] temp = new byte[1024];
                    int i = 0;
                    while ((i = is.read(temp)) > 0)
                    {
                        fos.write(temp, 0, i);
                    }
                    is.close();
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                //
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(android.content.Intent.ACTION_VIEW);//安裝
                intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
                startActivity(intent);
            }
        });
        //指定移除一個 Package
        uninstallButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mPackageChangeReceiver != null)
                {
                    String sPackage = "com.dygame.rushhours" ;
                    mPackageChangeReceiver.removePackage(MainActivity.this , sPackage) ;
                }
            }
        });
        quitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        //註冊廣播
        registerPackageChangeReceiver() ;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterPackageChangeReceiver();
    }

    public void registerPackageChangeReceiver()
    {
        try
        {
            mPackageChangeReceiver = new MyPackageChangeReceiver();
            mPackageChangeReceiver.setTag(TAG);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            intentFilter.addDataScheme("package"); // add addDataScheme
            registerReceiver(mPackageChangeReceiver, intentFilter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void unregisterPackageChangeReceiver()
    {
        try
        {
            if(mPackageChangeReceiver != null)
            {
                unregisterReceiver(mPackageChangeReceiver);
                mPackageChangeReceiver = null;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
