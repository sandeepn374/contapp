package com.voiceitbase.audiorecorder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    public static String USER_NAME = "";
    public static String phoneNumber = "";
    public static String USER_SEX = "";
    public static String USER_NATIVE_DISTRICT = "";
    public static String USER_PHONE_MAKE= "";
    public static String USER_PHONE_MODEL = "";
    private FirebaseAuth mAuth;

    public EditText referal;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      ;
        setSentenceCount(0);



        super.onCreate(savedInstanceState);
        String user=RegisterActivity.getDefaults("user",getApplicationContext());

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String fname     =  "*********************Test First Name///////";
                mAuth = FirebaseAuth.getInstance();

                try{
                    String fnameValue  = URLEncoder.encode(fname, "UTF-8");
                    HttpClient Client = new DefaultHttpClient();

                    String URL = "http://35.196.205.226/api/userinfo/"+mAuth.getCurrentUser().getEmail();
                    try
                    {
                        String SetServerString = "";
                        HttpGet httpget = new HttpGet(URL);
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        SetServerString = Client.execute(httpget, responseHandler);
                        final String finalSetServerString = SetServerString;
                        if(SetServerString.equals("notregistered"))
                        {
                            System.out.println("please register");
                        }
                        else {
                            String[] data = SetServerString.split("-");
                            RegisterActivity.USER_NAME=data[0];
                            RegisterActivity.setDefaults("user", data[0], getApplicationContext());
                            RegisterActivity.setDefaults("phone", data[2], getApplicationContext());
                            RegisterActivity.setDefaults("uuid", data[1], getApplicationContext());
                            RegisterActivity.setDefaults("email", data[3], getApplicationContext());
                            RegisterActivity.setDefaults("ownreferalcode", data[4], getApplicationContext());
                            if(data.length==6)
                            RegisterActivity.setDefaults("referedby", data[5], getApplicationContext());
                            Intent i =new Intent(RegisterActivity.this,MainActivity.class);
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


            }});
        //t.start();
        if(user!=null){

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(intent);


        }

        else {

            class BackgroundTask extends AsyncTask<Void, Void, Void> {
                private ProgressDialog dialog;

                public BackgroundTask(RegisterActivity activity) {
                    dialog = new ProgressDialog(activity);
                }

                @Override
                protected void onPreExecute() {
                    dialog.setMessage("Validating user please wait");
                    dialog.show();
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

                @Override
                protected Void doInBackground(Void... params) {

                    String fname     =  "*********************Test First Name///////";
                    mAuth = FirebaseAuth.getInstance();

                    try{
                        String fnameValue  = URLEncoder.encode(fname, "UTF-8");
                        HttpClient Client = new DefaultHttpClient();

                        String URL = "http://35.196.205.226/api/userinfo/"+mAuth.getCurrentUser().getEmail();
                        try
                        {
                            String SetServerString = "";
                            HttpGet httpget = new HttpGet(URL);
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();
                            SetServerString = Client.execute(httpget, responseHandler);
                            final String finalSetServerString = SetServerString;
                            if(SetServerString.equals("notregistered"))
                            {
                                System.out.println("please register");
                            }
                            else {
                                String[] data = SetServerString.split("-");
                                RegisterActivity.USER_NAME=data[0];
                                RegisterActivity.setDefaults("user", data[0], getApplicationContext());
                                RegisterActivity.setDefaults("phone", data[2], getApplicationContext());
                                RegisterActivity.setDefaults("uuid", data[1], getApplicationContext());
                                RegisterActivity.setDefaults("email", data[3], getApplicationContext());
                                RegisterActivity.setDefaults("ownreferalcode", data[4], getApplicationContext());
                                if(data.length==6)
                                RegisterActivity.setDefaults("referedby", data[5], getApplicationContext());
                                else
                                    RegisterActivity.setDefaults("referedby", "no refer used", getApplicationContext());

                                Intent i =new Intent(RegisterActivity.this,MainActivity.class);
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

                    return null;
                }

            }

            BackgroundTask task = new BackgroundTask(RegisterActivity.this);
           task.execute();




            setContentView(R.layout.activity_register);

        }






    }



    public String getRandomReferalCode() {


        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder(6);
        Random random = new Random();
        for(int i = 0; i< 6;i++)

        {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        String output = sb.toString();
        System.out.println(output);
        return output;
    }

    /*private void makePostRequestOnNewThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {


                String fname     =  "*********************Test First Name///////";
                mAuth = FirebaseAuth.getInstance();

                try{
                    String fnameValue  = URLEncoder.encode(fname, "UTF-8");
                    HttpClient Client = new DefaultHttpClient();

                    String URL = "http://35.196.205.226/api/userinfo/"+mAuth.getCurrentUser().getEmail();
                    try
                    {
                        String SetServerString = "";
                        HttpGet httpget = new HttpGet(URL);
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        SetServerString = Client.execute(httpget, responseHandler);
                        final String finalSetServerString = SetServerString;
                        if(SetServerString.equals("notregistered"))
                        {
                          System.out.println("please register");
                        }
                        else {
                            String[] data = SetServerString.split("-");
                            RegisterActivity.USER_NAME=data[0];
                            RegisterActivity.setDefaults("user", data[0], getApplicationContext());
                            RegisterActivity.setDefaults("phone", data[2], getApplicationContext());
                            RegisterActivity.setDefaults("uuid", data[1], getApplicationContext());
                            RegisterActivity.setDefaults("email", data[3], getApplicationContext());
                            RegisterActivity.setDefaults("ownreferalcode", data[4], getApplicationContext());
                            RegisterActivity.setDefaults("referedby", data[5], getApplicationContext());
                            Intent i =new Intent(RegisterActivity.this,MainActivity.class);
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




            }
        });
        t.start();
    }
*/

    /** Called when the user taps the Send button */
    public void registerUser(View view) {
        if(validateFields()) {
            showErrorToast();
        } else {
            storeUserCreds();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(intent);

        }
    }

    /**
     * Show Custom Error Toast
     */
    public void showErrorToast() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.customr_toast_layout,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.toastText);
        text.setText("Fields are empty !!!");

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    /**
     * validates Fields
     * @return
     */
    public boolean validateFields() {
        Boolean isEmpty = false;
        EditText textName = (EditText) findViewById(R.id.fieldName), phone= (EditText) findViewById(R.id.phoneNumber), textNativeDistrict = (EditText) findViewById(R.id.fieldNativeDistrict);
        RadioGroup radioSex = (RadioGroup) findViewById(R.id.radioGroup);
        int selectedId = radioSex.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        RadioButton radioButton = (RadioButton) findViewById(selectedId);

        USER_NAME = textName.getText().toString();
        phoneNumber = phone.getText().toString();
        USER_SEX = (radioButton == null) ? "" : radioButton.getText().toString();
        USER_NATIVE_DISTRICT= textNativeDistrict.getText().toString();

        if(USER_NAME.matches("") ||phoneNumber.matches("") || USER_NATIVE_DISTRICT.matches("") || radioSex.getCheckedRadioButtonId() == -1) {
            isEmpty = true;
        }
        return isEmpty;
    }

    /**
     * Stores User creds in storedprefrences.
     */
    public void storeUserCreds() {


        RegisterActivity.setDefaults("user",USER_NAME,getApplicationContext());
        final String uuid = UUID.randomUUID().toString().replace("-", "");
        RegisterActivity.setDefaults("phone",phoneNumber,getApplicationContext());
        RegisterActivity.setDefaults("uuid",uuid,getApplicationContext());

        referal=(EditText)findViewById(R.id.referalcode);

        String referedby=referal.getText().toString();

        RegisterActivity.setDefaults("referedby",referedby,getApplicationContext());

        String userReferal=getRandomReferalCode();



        RegisterActivity.setDefaults("ownreferalcode",userReferal,getApplicationContext());



    }




    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
    public  void setSentenceCount(int count) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("sentence_count", count);
        editor.commit();
    }





}
