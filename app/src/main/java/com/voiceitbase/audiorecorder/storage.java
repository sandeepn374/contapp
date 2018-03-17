package com.voiceitbase.audiorecorder;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.StorageObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
//import static android.support.v4.BuildConfig.DEBUG;

/**
 * Created by kshravi on 21/02/2018 AD.
 */

public class storage extends Activity {
   public  Storage storage;

   @Override
  protected void onCreate(Bundle newInstance){
       super.onCreate(newInstance);



       new AsyncTask(){

           @Override
           protected Object doInBackground(Object[] params) {
               try {

                   CloudStorage.uploadFile(storage.this,"gnani", "call-beep.wav");

               } catch (Exception e) {
                  // if(DEBUG)Log.d(TAG, "Exception: "+e.getMessage());
                   e.printStackTrace();
               }
               return null;
           }
       }.execute();}




}
