package project.own.hashratemonitor;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button startMain;
    private Button stopMain;
    private Button settingsMain;
    private TextView hashrateField;
    private TextView infoStatusText ;
    private WorkService workService;


    private ServiceConnection serviceConnection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            WorkService.MyBinder binder = (WorkService.MyBinder) service;
            workService = binder.getworkService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            workService = null;

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                showHashrate();
                handler.postDelayed(this, 5000);
            }
        }, 1500);


    }

    public void clickStartButton(View view){
        infoStatusText = (TextView)findViewById(R.id.infoStatusText);
        startBackgroundWorkService();

        infoStatusText.setText("sledzenie w toku aby wtrzymać kliknij stop");

    }


    public void clickStopButton(View view){
        stopBackgroundWorkService();
        infoStatusText = (TextView)findViewById(R.id.infoStatusText);
        infoStatusText.setText("sledzenie wsztrzymane aby rozpocząć kliknij start");
    }

    private void startBackgroundWorkService() {
        Intent serviceIntent = new Intent(this, WorkService.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    private void showHashrate() {
        if(workService != null){
            ((TextView) findViewById(R.id.hashrateField)).setText(workService.showHashrate());
        }
    }

    private void stopBackgroundWorkService() {
        Intent serviceIntent = new Intent(this, WorkService.class);
        unbindService(serviceConnection);
        stopService(serviceIntent);
    }

    public void clickSettingsButton(View view){
        Intent openSettingsScreen = new Intent(getApplicationContext(),Settings.class);
        startActivity(openSettingsScreen);
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            showHashrate();
        }
    }
    

}
