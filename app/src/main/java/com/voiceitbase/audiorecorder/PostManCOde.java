package com.voiceitbase.audiorecorder;

import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kshravi on 22/02/2018 AD.
 */

public class PostManCOde {


    public static  void main(String[] args) throws IOException {



            /*HttpClient Client = new DefaultHttpClient();
            String URL = "http://35.229.84.179:9009/api/text";
            try
            {
                String SetServerString = "";
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                SetServerString = Client.execute(httpget, responseHandler);
                System.out.println(SetServerString);
//                dialog.dismiss();

            }
            catch(Exception ex)
            {
//                dialog.dismi
//                showToast(ex.toString());
                Log.e("Error", ex.toString());
            }*/




        PostManCOde p=new PostManCOde();
        File f=new File("/Users/kshravi/Downloads/test6.wav");
        p.uploadImage(f,"g");



    }



    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public void uploadImage(File image, String imageName) throws IOException {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("audio_file", imageName, RequestBody.create(MEDIA_TYPE_PNG, image))
             .addFormDataPart("user","aakash")
                .addFormDataPart("textfromphone","hi how are you")
                .addFormDataPart("uuid","my_uuid")
                .build();

        Request request = new Request.Builder().url("http://35.196.80.178:8080/api/upload")
                .post(requestBody).build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

    }
}
