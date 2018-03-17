package com.voiceitbase.audiorecorder;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.BroadcastReceiver;
import com.google.firebase.auth.FirebaseAuth;

public class FileListNewMulti extends AppCompatActivity  {



    //initialize view's
    //ProgressDialog dialog;
    ListView simpleListView;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    String selectedFilePath = "";
    Thread uploadThread;
    SimpleAdapter simpleAdapter;

    private static NotificationHandler nHandler;
    private static NotificationManager mNotificationManager;
    private FirebaseAuth auth;

    TextView status;



    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                String resultCode = bundle.getString(UploadService.RESULT);
                String reason=bundle.getString("realreason");
                if (resultCode.equals("success")) {

                    Toast.makeText(FileListNewMulti.this,
                            "Upload complete" + "",
                            Toast.LENGTH_LONG).show();


                } else {

                        Toast.makeText(FileListNewMulti.this, "Upload failed",
                                Toast.LENGTH_LONG).show();



                }
            }
        }
    };

    /*@Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                UploadService.NOTIFICATION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        boolean servicestate=isMyServiceRunning(UploadService.class);

        if(servicestate) {
            Toast.makeText(FileListNewMulti.this, "File upload is in progress ",
                    Toast.LENGTH_LONG).show();




            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_filelistnewmulti);
            simpleListView = (ListView) findViewById(R.id.simpleListView);
            String[] from = {"name", "path"};//string array
            int[] to = {R.id.textView, R.id.imageView};//int array of views id's
            ArrayList<HashMap<String,String>> emptyarrayList = new ArrayList<>();
            simpleAdapter = new SimpleAdapter(this, emptyarrayList, R.layout.list_view_items, from, to);//Create object and set the parameters for simpleAdapter
            simpleListView.setAdapter(simpleAdapter);
        }
        else {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_filelistnewmulti);
            simpleListView = (ListView) findViewById(R.id.simpleListView);
            String[] from = {"name", "path"};//string array
            int[] to = {R.id.textView, R.id.imageView};//int array of views id's
            arrayList = getFolderList();
            simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.list_view_items, from, to);//Create object and set the parameters for simpleAdapter
            simpleListView.setAdapter(simpleAdapter);//sets the adapter for listView
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    public void setFilePath(int index) {
        String path = arrayList.get(index).get("path") + File.separatorChar + arrayList.get(index).get("name");
        path += File.separatorChar + RegisterActivity.getDefaults("user",getApplicationContext()) + arrayList.get(index).get("name");
        selectedFilePath = path.replace("Name: ", "").replace("Path: ","");
    }

    public String getFolderPath() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,"WavAudioRecorder");

        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * Get file List
     */
    public ArrayList<HashMap<String,String>> getFolderList() {
        ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();

        String[] fileList = {};
        File f = new File(getFolderPath());
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {

                File[] filesInside = inFile.listFiles();
                if(filesInside.length==2) {

                    HashMap<String, String> hashMap = new HashMap<>();//create a hashmap to store the data in key value pair
                    hashMap.put("name", "Name: " + inFile.getName());
                    hashMap.put("path", "Path: " + inFile.getParent());
                    arrayList.add(hashMap);//add the hashmap into arrayList

                }

            }
        }
        return arrayList;
    }





    public void multiUpload(View view){



        //setContentView(R.layout.servicestatus);

        setContentView(R.layout.activity_filelistnewmulti);


        simpleListView=(ListView)findViewById(R.id.simpleListView);



        String[] from={"name","path"};//string array
        int[] to={R.id.textView,R.id.imageView};//int array of views id's

        ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();


        simpleAdapter=new SimpleAdapter(this, arrayList, R.layout.list_view_items, from, to);//Create object and set the parameters for simpleAdapter
        simpleListView.setAdapter(simpleAdapter);//sets the adapter for listView


        Intent intent = new Intent(this, UploadService.class);

        startService(intent);



        Toast.makeText(FileListNewMulti.this, "Upload started in background",
                Toast.LENGTH_LONG).show();









    }



    @Override
    public void onBackPressed()
    {
        Intent i=new Intent(FileListNewMulti.this,MainActivity.class);
        i.putExtra("send","FileList");
        startActivity(i);
    }

}
