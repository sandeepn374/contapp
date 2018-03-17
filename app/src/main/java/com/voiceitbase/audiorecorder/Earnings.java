package com.voiceitbase.audiorecorder;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Earnings extends AppCompatActivity {

    TextView files,earnings,accept,reject,referalcode,referincometext;
    int totalAccepted,totalRejected,referalincome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);
        earnings=(TextView)findViewById(R.id.earnings);
        files=(TextView)findViewById(R.id.files);
        accept=(TextView)findViewById(R.id.acceptance);
        reject=(TextView)findViewById(R.id.rejected);
        referalcode=(TextView)findViewById(R.id.referalcodetoshare);
        referincometext=(TextView)findViewById(R.id.referalicome);
        makePostRequestOnNewThread();

    }

    private void makePostRequestOnNewThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {


                /*String fname     =  "*********************Test First Name///////";
                showToast("Please wait, connecting to server.");
                try{
                    String fnameValue  = URLEncoder.encode(fname, "UTF-8");
                    HttpClient Client = new DefaultHttpClient();
                    String referid=  RegisterActivity.getDefaults("ownreferalcode",getApplicationContext());
                    String URL = "http://35.196.205.226/api/userinfo/kshravi86@gmail.com";
                    try
                    {
                        String SetServerString = "";
                        HttpGet httpget = new HttpGet(URL);
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        SetServerString = Client.execute(httpget, responseHandler);
                        final String finalSetServerString = SetServerString;
                        if(SetServerString.equals("notregistered"))
                        {
                            Intent i =new Intent(Earnings.this,RegisterActivity.class);
                            startActivity(i);
                        }
                        else {
                            String[] data = SetServerString.split("-");
                            RegisterActivity.setDefaults("user", data[0], getApplicationContext());
                            RegisterActivity.setDefaults("phone", data[2], getApplicationContext());
                            RegisterActivity.setDefaults("uuid", data[1], getApplicationContext());
                            RegisterActivity.setDefaults("email", data[3], getApplicationContext());
                            RegisterActivity.setDefaults("ownreferalcode", data[4], getApplicationContext());
                            Intent i =new Intent(Earnings.this,MainActivity.class);
                            startActivity(i);
                        }

                    }
                    catch(Exception ex)
                    {
//                dialog.dismiss();

//                showToast(ex.toString());
                        Log.e("Error", ex.toString());
                    }
                }
                catch(UnsupportedEncodingException ex)
                {
//            dialog.dismiss();
//            showToast(ex.toString());
                    Log.e("Error", ex.getMessage().toString());
                }
                catch (Exception e) {
//            dialog.dismiss();
//            showToast(e.toString());
                    Log.e("Error", e.getMessage().toString());
                }

                /*OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://35.196.205.226/api/userinfo/kshravi86@gmail.com")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    System.out.println(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                getEarnings();
                getrejectedFiles();
                getReferalIncome();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        files.setText("Files = "+(totalAccepted+totalRejected));
                        earnings.setText("Earnings = Rs "+totalAccepted*0.50);
                        reject.setText("Total rejected files = "+totalRejected);
                        accept.setText("Total accepted files = "+totalAccepted);
                        referincometext.setText("Referral Income Rs = "+referalincome*0.10);
                        referalcode.setText(RegisterActivity.getDefaults("ownreferalcode",getApplicationContext()));
                        referalcode.setTypeface(null, Typeface.BOLD);

                    }
                });

            }
        });
        t.start();
    }

    private void getReferalIncome() {

        String fname     =  "*********************Test First Name///////";
        showToast("Please wait, connecting to server.");
        try{
            String fnameValue  = URLEncoder.encode(fname, "UTF-8");
            HttpClient Client = new DefaultHttpClient();
            String referid=  RegisterActivity.getDefaults("ownreferalcode",getApplicationContext());
            String URL = "http://35.196.205.226/api/refer/"+referid;
            try
            {
                String SetServerString = "";
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                SetServerString = Client.execute(httpget, responseHandler);
                final String finalSetServerString = SetServerString;
                referalincome=Integer.parseInt(SetServerString);

            }
            catch(Exception ex)
            {
//                dialog.dismiss();

//                showToast(ex.toString());
                Log.e("Error", ex.toString());
            }
        }
        catch(UnsupportedEncodingException ex)
        {
//            dialog.dismiss();
//            showToast(ex.toString());
            Log.e("Error", ex.getMessage().toString());
        }
        catch (Exception e) {
//            dialog.dismiss();
//            showToast(e.toString());
            Log.e("Error", e.getMessage().toString());
        }



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


    public void getEarnings() {
//        ProgressDialog dialog = showDialog("Downloading File.");
        String fname     =  "*********************Test First Name///////";
        showToast("Please wait, connecting to server.");
        try{
            String fnameValue  = URLEncoder.encode(fname, "UTF-8");
            HttpClient Client = new DefaultHttpClient();
          String uuid=  RegisterActivity.getDefaults("uuid",getApplicationContext());
            String URL = "http://35.196.205.226/api/earnings/"+uuid;
            try
            {
                String SetServerString = "";
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                SetServerString = Client.execute(httpget, responseHandler);
                final String finalSetServerString = SetServerString;
                totalAccepted=Integer.parseInt(SetServerString);

            }
            catch(Exception ex)
            {
//                dialog.dismiss();

//                showToast(ex.toString());
                Log.e("Error", ex.toString());
            }
        }
        catch(UnsupportedEncodingException ex)
        {
//            dialog.dismiss();
//            showToast(ex.toString());
            Log.e("Error", ex.getMessage().toString());
        }
        catch (Exception e) {
//            dialog.dismiss();
//            showToast(e.toString());
            Log.e("Error", e.getMessage().toString());
        }
    }

    public void getrejectedFiles(){

        showToast("Please wait, connecting to server.");
        try{

            HttpClient Client = new DefaultHttpClient();
            String uuid=  RegisterActivity.getDefaults("uuid",getApplicationContext());
            String URL = "http://35.196.205.226/api/no/"+uuid;
            try
            {
                String SetServerString = "";
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                SetServerString = Client.execute(httpget, responseHandler);
                final String finalSetServerString = SetServerString;
                totalRejected=Integer.parseInt(SetServerString);





            }
            catch(Exception ex)
            {
//                dialog.dismiss();

//                showToast(ex.toString());
                Log.e("Error", ex.toString());
            }
        }

        catch (Exception e) {
//            dialog.dismiss();
//            showToast(e.toString());
            Log.e("Error", e.getMessage().toString());
        }


    }

}
