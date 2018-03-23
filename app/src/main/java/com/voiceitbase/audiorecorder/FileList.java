package com.voiceitbase.audiorecorder;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.Charsets;
import com.google.common.io.Files;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.google.common.io.Files;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.io.FileUtils;

public class FileList extends AppCompatActivity  {



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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        simpleListView=(ListView)findViewById(R.id.simpleListView);
        if(nHandler == null) {
            nHandler = new NotificationHandler();
            mNotificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }


        String[] from={"name","path"};//string array
        int[] to={R.id.textView,R.id.imageView};//int array of views id's
        arrayList = getFolderList();

        simpleAdapter=new SimpleAdapter(this, arrayList, R.layout.list_view_items, from, to);//Create object and set the parameters for simpleAdapter
        simpleListView.setAdapter(simpleAdapter);//sets the adapter for listView

        //perform listView item click event
        simpleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setFilePath(i);
            }
        });
    }

    public void showToast(String message) {
        final String strMessage = message;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), strMessage, Toast.LENGTH_SHORT).show();
            }
        });
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

               // FileUtils.sizeOfDirectory(inFile);
               long foldersize= folderSize(inFile);
                File[] filesInside = inFile.listFiles();

                double kilobytes = (foldersize / 1024);
                double megabytes = (kilobytes / 1024);



                if(filesInside.length==2 && megabytes<3) {




                    HashMap<String, String> hashMap = new HashMap<>();//create a hashmap to store the data in key value pair
                    hashMap.put("name", "Name: " + inFile.getName());
                    hashMap.put("path", "Path: " + inFile.getParent());
                    arrayList.add(hashMap);//add the hashmap into arrayList

                }

            }
        }
        return arrayList;
    }

    public String getFormatedTime(String yourmilliseconds) {
        return DateFormat.getDateTimeInstance().format(new Date(Long.parseLong(yourmilliseconds)));
    }
    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }




    public void onUpload(View view) {
        if(selectedFilePath != null && selectedFilePath != "" ){
           // dialog = ProgressDialog.show(FileList.this,"","Uploading File...",true);
            //nHandler.createProgressNotification(this);
           // Toast.makeText(getApplicationContext(), "Upload Started ", Toast.LENGTH_SHORT).show();
            arrayList = getFolderList();



            setContentView(R.layout.activity_file_list);

            simpleListView=(ListView)findViewById(R.id.simpleListView);


            String[] from={"name","path"};//string array
            int[] to={R.id.textView,R.id.imageView};//int array of views id's
            arrayList = getFolderList();
            for(int i=0;i<arrayList.size();i++){
                String[] splited = arrayList.get(i).get("name").split("\\s+");
                if(selectedFilePath.contains(splited[1]))
                    arrayList.remove(i);

            }


            simpleAdapter=new SimpleAdapter(FileList.this, arrayList, R.layout.list_view_items, from, to);//Create object and set the parameters for simpleAdapter
            simpleListView.setAdapter(simpleAdapter);//sets the adapter for listView


            simpleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    setFilePath(i);
                }
            });


            // used to update the progress notification
            final int progresID = new Random().nextInt(1000);

            // building the notification
            final NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.icons8inprogress24)
                    .setContentTitle("File Upload is starting")
                    .setContentText("wait")
                    .setTicker("File Upload is starting")
                    .setUsesChronometer(true)
                    .setProgress(100, 0, true);



            AsyncTask<Integer, Integer, Integer> downloadTask = new AsyncTask<Integer, Integer, Integer>() {
                @Override
                protected void onPreExecute () {
                    super.onPreExecute();
                    mNotificationManager.notify(progresID, nBuilder.build());
                }

                @Override
                protected Integer doInBackground (Integer... params) {
                    try {
                        // Sleeps 2 seconds to show the undeterminated progress
                        //Thread.sleep(5000);

                        // update the progress
                        try {
                            String text = Files.toString(new File(selectedFilePath+".txt"), com.google.common.base.Charsets.UTF_8);
                            String user=text.substring(text.indexOf("<Name>")+7,text.indexOf("\n"));
                            String sentence="";
                            if(text.contains("<sentence>"))
                                sentence=text.substring(text.indexOf("<sentence>")+11);
                            //sentence=sentence.substring(sentence.indexOf("\""));
                            sentence=sentence.replaceAll("\"","");
                            sentence=sentence.replaceAll("\n","");

                            uploadFile(".wav",sentence,user);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //dialog.dismiss();
                                    showToast("Upload is happenning in background" );

                                }
                            });

                        }
                        catch (final FileNotFoundException e){


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //dialog.dismiss();
                                    showToast("File Not Found!!!" + e);

                                }
                            });


                        }

                        catch (final IOException e) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //dialog.dismiss();
                                    showToast("Failed to upload "+e);

                                }
                            });
                            e.printStackTrace();
                        }
                        for (int i = 0; i < 105; i+=5) {
                            nBuilder
                                    .setContentTitle("File uploading...")
                                    .setContentText("File Uploading...")
                                    .setProgress(100, i, false)
                                    .setSmallIcon(R.drawable.icons8downloadingupdates)
                                    .setContentInfo(i + " %");

                            // use the same id for update instead created another one
                            mNotificationManager.notify(progresID, nBuilder.build());
                            Thread.sleep(1);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    return null;
                }


                @Override
                protected void onPostExecute (Integer integer) {
                    super.onPostExecute(integer);

                    nBuilder.setContentText("Upload finished")
                            .setContentTitle("Upload finished !!")
                            .setTicker("Upload finished !!!")
                            .setSmallIcon(R.drawable.icons8ok24)
                            .setUsesChronometer(false);

                    mNotificationManager.notify(progresID, nBuilder.build());
                }
            };

            // Executes the progress task
            downloadTask.execute();

        }else{
            showToast("Please choose a File!!!");
        }
    }








    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public void uploadFile(String filelastname,String stringfromPhone,String user) throws IOException {

        String referedby=RegisterActivity.getDefaults("referedby",getApplicationContext());
        String ownreferalcode=RegisterActivity.getDefaults("ownreferalcode",getApplicationContext());

        File image=new File(selectedFilePath+filelastname);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        user=RegisterActivity.getDefaults("user",getApplicationContext());
        String phone=RegisterActivity.getDefaults("phone",getApplicationContext());

        String uuid=RegisterActivity.getDefaults("uuid",getApplicationContext());
        auth = FirebaseAuth.getInstance();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("audio_file", image.getName(), RequestBody.create(MEDIA_TYPE_PNG, image))
                .addFormDataPart("user",user)
                .addFormDataPart("textfromphone","aakash")
                .addFormDataPart("uuid",uuid)
                .addFormDataPart("phone",phone)
                .addFormDataPart("referedby",referedby)
                .addFormDataPart("ownreferalcode",ownreferalcode)
                .addFormDataPart("email",auth.getCurrentUser().getEmail())
                .addFormDataPart("age","18")
                .addFormDataPart("sex","female")
                .addFormDataPart("district","bangalore")
                .build();

        Request request = new Request.Builder().url("http://35.196.205.226/api/upload")
                .post(requestBody).build();

        Response response = client.newCall(request).execute();
       //
        if(response.isSuccessful()){

            String deleteFolderName = image.getAbsolutePath();

            int index=deleteFolderName.lastIndexOf("/");

            deleteFolderName = deleteFolderName.substring(0, index);
            deleteFiles(deleteFolderName);







        }
        if (!response.isSuccessful()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Upload Failed ", Toast.LENGTH_SHORT).show();
                }
            });
           // dialog.dismiss();

            throw new IOException("Unexpected code " + response);
        }



    }


    public void deleteFiles(String path) {

        File file = new File(path);

        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) { }
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent i=new Intent(FileList.this,MainActivity.class);
        i.putExtra("send","FileList");
        startActivity(i);
    }

}
