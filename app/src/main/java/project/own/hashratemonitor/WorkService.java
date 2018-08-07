package project.own.hashratemonitor;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
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

public class WorkService extends Service{

    private String hashrateMessage;
    private Timer timer;
    private TimerTask timerTask;

    public String showHashrate() {
        return hashrateMessage;
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            webStart();
        }
    }

    private final IBinder binder = new MyBinder();
    public class MyBinder extends Binder{
        WorkService getworkService(){
            return WorkService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        timer = new Timer();
    }
    public void setStartHashrate(String hashrateMessage){
        this.hashrateMessage = hashrateMessage;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        clearTimerSchedule();
        initTask();
        timer.scheduleAtFixedRate(timerTask, 0 * 1000, 60 * 1000);
        return super.onStartCommand(intent, flags, startId);
    }
    private void clearTimerSchedule() {
        if(timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }
    }
    private void initTask() {
        timerTask = new MyTimerTask();
    }

    @Override
    public void onDestroy(){
        clearTimerSchedule();
        super.onDestroy();
    }
    public  void webStart(){
        new WebServicesHandler()
                .execute("https://ethereum.miningpoolhub.com/index.php?page=api&action=getuserhashrate&api_key=3e32dafdb817ef7add17f2dd0db0ef05e50a4d3780cabb03830cfd2ae21fc944&id=150097");

    }

    private class WebServicesHandler extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... urls){

            try{
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();

                InputStream in = new BufferedInputStream(connection.getInputStream());

                return streamToString(in);
            }catch (Exception e){
                Log.d(MainActivity.class.getSimpleName(), e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result){



            try {


                JSONObject json = new JSONObject(result);
                JSONObject getuserhashrate = json.getJSONObject("getuserhashrate");
                String hashrate = getuserhashrate.getString("data");
                int i =0;
                String endHashrate;
                while (hashrate.charAt(i)!='.'){
                    i++;
                }
                if(i<2){
                    endHashrate = "0";
                }else if(i<3){
                    endHashrate = hashrate.substring(0,i);
                    endHashrate = "0.0"+endHashrate;
                }else if(i<4){
                    endHashrate = hashrate.substring(0,i);
                    endHashrate = "0."+endHashrate;
                }else {
                    endHashrate = hashrate.substring(0,i-3);
                }
                hashrateMessage = endHashrate;

            }catch (Exception e) {
                Log.d(MainActivity.class.getSimpleName(), e.toString());
            }
        }
    }
    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try {

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            reader.close();

        } catch (IOException e) {
            Log.d(MainActivity.class.getSimpleName(), e.toString());
        }

        return stringBuilder.toString();
    }



}
