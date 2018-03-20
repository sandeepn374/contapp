package com.voiceitbase.audiorecorder;

/**
 * Created by kshravi on 15/03/2018 AD.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.google.common.io.Files;
import com.google.firebase.auth.FirebaseAuth;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadService extends IntentService {

    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlpath";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.vogella.android.service.receiver";

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public UploadService() {
        super("DownloadService");
    }

    // will be called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {



        ArrayList<HashMap<String,String>> arrayList = getFolderList();

        for(int index=0;index<arrayList.size();index++){


            String path = arrayList.get(index).get("path") + File.separatorChar + arrayList.get(index).get("name");
            path += File.separatorChar + RegisterActivity.getDefaults("user",getApplicationContext()) + arrayList.get(index).get("name");
            String FilePath = path.replace("Name: ", "").replace("Path: ","");


            try {
                String text = Files.toString(new File(FilePath+".txt"), com.google.common.base.Charsets.UTF_8);
                String user=text.substring(text.indexOf("<Name>")+7,text.indexOf("\n"));
                String sentence="";
                if(text.contains("<sentence>"))
                    sentence=text.substring(text.indexOf("<sentence>")+11);
                //sentence=sentence.substring(sentence.indexOf("\""));
                sentence=sentence.replaceAll("\"","");
                sentence=sentence.replaceAll("\n","");
                sentence=sentence.trim();


                String referedby=RegisterActivity.getDefaults("referedby",getApplicationContext());
                String ownreferalcode=RegisterActivity.getDefaults("ownreferalcode",getApplicationContext());

                File image=new File(FilePath+".wav");

                OkHttpClient client = new OkHttpClient();


                user=RegisterActivity.getDefaults("user",getApplicationContext());
                String phone=RegisterActivity.getDefaults("phone",getApplicationContext());

                String uuid=RegisterActivity.getDefaults("uuid",getApplicationContext());

                String age=RegisterActivity.getDefaults("age",getApplicationContext());

                String sex=RegisterActivity.getDefaults("sex",getApplicationContext());
                String district=RegisterActivity.getDefaults("district",getApplicationContext());

                if(age==null)
                    age="";
                if(sex==null)
                    sex="";
                if(district==null)
                    district="";



                FirebaseAuth auth = FirebaseAuth.getInstance();

                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("audio_file", image.getName(), RequestBody.create(MEDIA_TYPE_PNG, image))
                        .addFormDataPart("user",user)
                        .addFormDataPart("textfromphone",sentence.substring(0,sentence.length()-3))
                        .addFormDataPart("uuid",uuid)
                        .addFormDataPart("phone",phone)
                        .addFormDataPart("referedby",referedby)
                        .addFormDataPart("ownreferalcode",ownreferalcode)
                        .addFormDataPart("email",auth.getCurrentUser().getEmail())
                        .addFormDataPart("age",age)
                        .addFormDataPart("sex",sex)
                        .addFormDataPart("district",district)
                        .build();



                Request request = new Request.Builder().url("http://35.196.205.226/api/upload")
                        .post(requestBody).build();

                Response response = client.newCall(request).execute();
                //
                if(response.isSuccessful()){

                    String deleteFolderName = image.getAbsolutePath();

                    int index3=deleteFolderName.lastIndexOf("/");

                    deleteFolderName = deleteFolderName.substring(0, index3);
                    deleteFiles(deleteFolderName);







                }
                if (!response.isSuccessful()) {
                    // dialog.dismiss();

                    publishResults(response.toString(), "failed");
                    return;

                }


            }
            catch (final FileNotFoundException e){


                publishResults("filenotfound", "failure");
                return;



            }

            catch (final IOException e) {

                publishResults(""+e.toString(), "failure");

                e.printStackTrace();
                return;
            }


            publishResults("suceess", "success");


        }






    }

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

    public String getFolderPath() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,"WavAudioRecorder");

        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    private void publishResults(String realreason, String result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("realreason", realreason);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
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
}
