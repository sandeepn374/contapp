package com.voiceitbase.audiorecorder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.taishi.library.Indicator;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    Button buttonPlayLastRecordAudio;
    Button buttonStopPlayingRecording;
    //Button uploadCurrent;
    String AudioSavePathInDevice = null;
    long totalSize = 0;

    MediaRecorder mediaRecorder;
    Random random;
    String usernameinfile = "Abc";
    String timeAsName = "";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    String infoFileString;
    int file_line_count;
    Indicator indicator;
    private FirebaseAuth auth;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.uploadMenu) {

            Intent intent = new Intent(getApplicationContext(), FileListNewMulti.class);
            startActivity(intent);
        } else if (id == R.id.earningsMenu) {

            Intent intent = new Intent(getApplicationContext(), Earnings.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        indicator=(Indicator)findViewById(R.id.indicator);

        Intent intent = getIntent();
        usernameinfile = RegisterActivity.getDefaults("user", getApplicationContext());
        final Button imageButton = (Button) findViewById(R.id.record);
        final Button imageButtonStop = (Button) findViewById(R.id.stop);
        buttonPlayLastRecordAudio = (Button) findViewById(R.id.play);
        buttonStopPlayingRecording = (Button) findViewById(R.id.stop_playing);

        final Button imageButtonRefresh = (Button) findViewById(R.id.refreshButton);
        TextView readableText = (TextView) findViewById(R.id.readableText);

        readableText.setMovementMethod(new ScrollingMovementMethod());
        Intent intentFromUpload = getIntent();
        if (intentFromUpload != null) {
            String checkFlag = intent.getStringExtra("send");
            if (checkFlag != null) {
                if (checkFlag.equals("FileList"))
                    refresherFunction(true);
            }
        }
        imageButtonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        imageButton.setEnabled(true);
        imageButtonRefresh.setEnabled(true);
        // display.setEnabled(true);

        random = new Random();

        /**
         * Check Permission.
         */
        if (checkPermission()) {
            imageButton.setEnabled(true);
            imageButtonStop.setEnabled(false);
            refresherFunction(false);
        } else {
            requestPermission();
        }

        imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresherFunction(false);
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(MainActivity.this, "Recording started", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                indicator.setVisibility(View.VISIBLE);
                imageButtonStop.setEnabled(true);

                imageButtonStop.getBackground().setColorFilter(null);

                imageButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                imageButtonRefresh.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                buttonPlayLastRecordAudio.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                buttonStopPlayingRecording.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                imageButton.setEnabled(false);
                imageButtonRefresh.setEnabled(false);

                buttonPlayLastRecordAudio.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(false);

                startRecording();
            }
        });

        imageButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indicator.setVisibility(View.INVISIBLE);
                stopRecording();
                Toast toast = Toast.makeText(MainActivity.this, "Recording Completed", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                imageButton.getBackground().setColorFilter(null);
                buttonPlayLastRecordAudio.getBackground().setColorFilter(null);
                imageButtonRefresh.getBackground().setColorFilter(null);

                imageButton.setEnabled(true);
                buttonPlayLastRecordAudio.setEnabled(true);
                imageButtonRefresh.setEnabled(true);


                imageButtonStop.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                buttonStopPlayingRecording.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                imageButtonStop.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(false);

            }
        });

        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {
                buttonStopPlayingRecording.setEnabled(true);
                buttonStopPlayingRecording.getBackground().setColorFilter(null);

                imageButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                imageButtonStop.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                buttonPlayLastRecordAudio.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                imageButtonRefresh.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                imageButton.setEnabled(false);
                imageButtonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(false);
                imageButtonRefresh.setEnabled(false);


                mediaPlayer = new MediaPlayer();
                try {
                    String a = getFilename();
                    mediaPlayer.setDataSource(getFilename());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Toast toast = Toast.makeText(MainActivity.this, "Playing Last Recorded Audio.", Toast.LENGTH_SHORT);
                //toast.setGravity(Gravity.CENTER, 0, 0);
                //toast.show();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    public void onCompletion(MediaPlayer mp) {


                        buttonPlayLastRecordAudio.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                        imageButtonStop.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);


                        buttonStopPlayingRecording.setEnabled(false);
                        imageButtonStop.setEnabled(false);


                        imageButton.getBackground().setColorFilter(null);

                        buttonPlayLastRecordAudio.getBackground().setColorFilter(null);

                        imageButtonRefresh.getBackground().setColorFilter(null);

                        imageButton.setEnabled(true);
                        buttonPlayLastRecordAudio.setEnabled(true);
                        imageButtonRefresh.setEnabled(true);
                        //display.setEnabled(true);

                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                        }
                    }
                });


            }
        });

        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStopPlayingRecording.setEnabled(false);
                imageButtonStop.setEnabled(false);


                buttonPlayLastRecordAudio.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                imageButtonStop.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);


                imageButton.getBackground().setColorFilter(null);

                buttonPlayLastRecordAudio.getBackground().setColorFilter(null);

                imageButtonRefresh.getBackground().setColorFilter(null);


                imageButton.setEnabled(true);
                buttonPlayLastRecordAudio.setEnabled(true);
                imageButtonRefresh.setEnabled(true);
                //display.setEnabled(true);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }
        });


        /**
         * Wave Formater
         */
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING) * 3;
        audioData = new short[bufferSize]; //short array that pcm data is put into.
    }

    public void refresherFunction(boolean noCheck) {
        TextView readableText = (TextView) findViewById(R.id.readableText);
        if (noCheck || initFileCountAndDownload() >= 0) {
            String text = setSentenceToRead();
            text = text.replaceAll("\"", "");
            text = text.replaceAll(",", "");
            text = text.replaceAll("\n", "");
            text = text.trim();
            text = text.substring(0, text.length() - 2);
            readableText.setText(text);
        }
    }

    public int initFileCountAndDownload() {
        TextView readableText = (TextView) findViewById(R.id.readableText);

        file_line_count = getSentenceCount();
        if (file_line_count >= 50) {
            makePostRequestOnNewThread();
            setSentenceCount(0);
            file_line_count = 0;
        } else if (file_line_count <= 0) {
            makePostRequestOnNewThread();
            setSentenceCount(0);
            file_line_count = 0;
        } else {
//            readableText.setText(setSentenceToRead());
        }
        return file_line_count;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        makePostRequestOnNewThread();
                        setSentenceCount(0);
                        Toast toast = Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }



    /**
     * Do not navigate when back button is pressed.
     */
    @Override
    public void onBackPressed() { }

    /**
     * User Sentences to show
     * TODO: Get this from server.
     */
    public String setSentenceToRead() {
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        BufferedReader reader = null;
        ArrayList<String> sentences = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(new File (getSentenceFilePath(false)));
            reader = new BufferedReader( new InputStreamReader(fis));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                sentences.add(mLine);
            }
        } catch (IOException e) {
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        String file_path = getSentenceFilePath(false);
        File file = new File(file_path);

        int cnt = getSentenceCount();
        cnt = (cnt >= 50) ? 0: cnt;
        setSentenceCount(cnt);
        int sentencesLength = sentences.size();

        String sentence = (sentencesLength == 0) ? "No Sentences to Load, check your N/W!":  sentences.get(cnt);
        infoFileString = "";
        infoFileString = getInfoString();
        infoFileString += "<sentence>" + sentence + "\n";
        timeAsName = System.currentTimeMillis() + "";
        if (sentencesLength != 0) {
            setSentenceCount(cnt+1);
        }
        return sentence;
    }

    private String getInfoString() {
        String info = "";
        info = "<Name> " + usernameinfile + "\n";
        info += "<Sex> " + RegisterActivity.USER_SEX + "\n";
        info += "<Phone Make> " + RegisterActivity.USER_PHONE_MAKE + "\n";
        info += "<Phone Model> " + RegisterActivity.USER_PHONE_MODEL + "\n";
        info += "<Native District> " + RegisterActivity.USER_NATIVE_DISTRICT + "\n";
        return info;
    }


    /**
     * Wave Formter Code Start;
     */

    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "WavAudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    short[] audioData;
    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    int[] bufferData;
    int bytesRecorded;

    private String getFilename(){

        AudioSavePathInDevice = getFolderPath() + File.separatorChar + usernameinfile + timeAsName + AUDIO_RECORDER_FILE_EXT_WAV;
        return AudioSavePathInDevice;
    }

    private String getTxtFilename() {
        AudioSavePathInDevice = getFolderPath() + File.separatorChar + usernameinfile + timeAsName + ".txt";
        return AudioSavePathInDevice;
    }

    public String getFolderPath() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(file.getAbsoluteFile(), timeAsName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    private String getTempFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

        if (tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private void startRecording() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                bufferSize);
        int i = recorder.getState();
        if (i==1)
            recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");

        recordingThread.start();
    }

    private void writeAudioDataToFile() {
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;
        if (null != os) {
            while(isRecording) {
                read = recorder.read(data, 0, bufferSize);
                if (read > 0){
                }

                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeTxtDataToFile(String data) {
        String filename = getTxtFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(os);
            myOutWriter.append(data);

            myOutWriter.close();

            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void stopRecording() {
        if (null != recorder){
            isRecording = false;

            int i = recorder.getState();
            if (i==1)
                recorder.stop();
            recorder.release();

            recorder = null;
            recordingThread = null;
        }

        copyWaveFile(getTempFilename(),getFilename());
        deleteTempFile();
    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());
        file.delete();
    }

    private void copyWaveFile(String inFilename,String outFilename){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;


            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while(in.read(data) != -1) {
                out.write(data);
            }

            in.close();
            out.close();
            if (infoFileString  == ""){

            } else {
                writeTxtDataToFile(infoFileString);
                infoFileString = "";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException
    {
        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8);  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }
    /**
     * Wav Formater Code End;
     */

    /**
     * Get Sentences from Server
     */

    private void makePostRequestOnNewThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                getSentences();
            }
        });
        t.start();
    }
    public void getSentences() {
//        ProgressDialog dialog = showDialog("Downloading File.");
        String fname     =  "*********************Test First Name///////";
        showToast("Please wait, connecting to server.");
        try{
            String fnameValue  = URLEncoder.encode(fname, "UTF-8");
            HttpClient Client = new DefaultHttpClient();
            String URL = "http://35.196.205.226/api/text";
            try
            {
                String SetServerString = "";
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                SetServerString = Client.execute(httpget, responseHandler);
                int indexOfLines=SetServerString.indexOf("lines");
                SetServerString=SetServerString.substring(indexOfLines+11);
//                dialog.dismiss();
                writeSentencesToFile(SetServerString);

                refresherFunction(true);
                refresherFunction(true);
                showToast("Download Complete");
            }
            catch(Exception ex)
            {
//                dialog.dismiss();
                writeSentencesToFile(null);
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
              Toast toast=  Toast.makeText(getApplicationContext(), strMessage, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    public ProgressDialog showDialog(String message) {
        final String strMessage = message;
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", strMessage, true);
                return dialog;
//            }
//        });
    }

    public String getSentenceFilePath(Boolean clearFile) {

        try {
            String folderpath = Environment.getExternalStorageDirectory().getPath();
            File folder = new File(folderpath,AUDIO_RECORDER_FOLDER);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(folder.getAbsoluteFile(), "sentences.txt");

            if (!file.exists()) {
                    file.createNewFile();
            } else {
                if (clearFile) {
                    FileWriter fw;
                    fw = new FileWriter(file);
                    fw.write("");
                    fw.close();
                }
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void writeSentencesToFile(String data) {
        if (data != null) {
            String filename = getSentenceFilePath(true);
            FileOutputStream os = null;

            try {
                os = new FileOutputStream(filename);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(os);
                myOutWriter.write(data);

                myOutWriter.close();

                os.flush();
                os.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
    }

    private int getSentenceCount() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int count = sharedPref.getInt( "sentence_count", 0);
        if (count == 0) {
            setSentenceCount(0);
        }
        return count;
    }

    public  void setSentenceCount(int count) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("sentence_count", count);
        editor.commit();
    }

}
