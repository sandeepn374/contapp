package com.voiceitbase.audiorecorder;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Earnings extends AppCompatActivity  {

    TextView files,earnings,accept,reject,referalcode,referincometext;
    int totalAccepted,totalRejected,referalincome;
    float referprice ,uploadprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.earningsnav) {
                    Intent intent = new Intent(getApplicationContext(), Earnings.class);
                    startActivity(intent);


                } else if (id == R.id.uploadsnav) {

                    Intent intent = new Intent(getApplicationContext(), FileListNewMulti.class);
                    startActivity(intent);



                }
                else if(id==R.id.pricingpolicynav   ){
                    Intent intent = new Intent(getApplicationContext(), pricingpolicy.class);
                    startActivity(intent);

                }



                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        navigationView.bringToFront();
        earnings=(TextView)findViewById(R.id.earnings);
        files=(TextView)findViewById(R.id.files);
        accept=(TextView)findViewById(R.id.acceptance);
        reject=(TextView)findViewById(R.id.rejected);
        referalcode=(TextView)findViewById(R.id.referalcodetoshare);
        referincometext=(TextView)findViewById(R.id.referalicome);
        makePostRequestOnNewThread();

    }
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(getApplicationContext(),MainActivity.class);

        startActivity(setIntent);
    }

    private void makePostRequestOnNewThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                getEarnings();
                getrejectedFiles();
                getReferalIncome();
                getprice();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        files.setText("Files = "+(totalAccepted+totalRejected));

                        float earningstotal=0;

                        if(totalAccepted<=500) {
                            uploadprice = (float) 0.5;
                           earningstotal= totalAccepted*uploadprice;
                        }
                        else if(totalAccepted>500 && totalAccepted<=1000) {
                            uploadprice = (float) 0.25;
                            earningstotal=250+(totalAccepted-500)*uploadprice;

                        }
                        else {
                            uploadprice = (float) 0.10;
                            earningstotal=375+(totalAccepted-1000)*uploadprice;
                        }

                        earnings.setText("Earnings = Rs "+earningstotal);
                        reject.setText("Total rejected files = "+totalRejected);
                        accept.setText("Total accepted files = "+totalAccepted);
                        float refericome= (float) (referalincome*referprice);
                        refericome=round(refericome,2);

                        referincometext.setText("Referral Income Rs = "+refericome);
                        referalcode.setText(RegisterActivity.getDefaults("ownreferalcode",getApplicationContext()));
                        referalcode.setTypeface(null, Typeface.BOLD);

                    }
                });

            }
        });
        t.start();
    }


    private void getprice() {

        try {

            HttpClient Client = new DefaultHttpClient();

            String URL = "http://35.196.205.226/api/priceinfo";
            try {
                String SetServerString = "";
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                SetServerString = Client.execute(httpget, responseHandler);
                final String finalSetServerString = SetServerString;

                    String[] data = SetServerString.split("-");
                uploadprice=Float.valueOf(data[0]);
                referprice=Float.valueOf(data[1]);



            } catch (Exception ex) {
//                dialog.dismiss();

//                showToast(ex.toString());
                Log.e("Error", ex.toString());
            }
        }  catch (Exception e) {
//            dialog.dismiss();
//            showToast(e.toString());
            Log.e("Error", e.getMessage().toString());
        }
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
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

}
