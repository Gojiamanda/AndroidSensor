package jp.aoyama.a5821033.hgsensormonitor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textView;
    private int currentTime = 0;
    private int lastTime = 0;
    private String data = "";
    private boolean record = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SensorManager sma = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sma.registerListener(this, sma.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.accelerate);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.mipmap.ic_launcher);
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
    }

    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            textView.setText(event.sensor.getName());
            textView.append("\nType: " + event.sensor.getStringType());
            textView.append("\nVendor: " + event.sensor.getVendor());
            textView.append("\nVersion: " + event.sensor.getVersion());
            textView.append("\nResolution: " + event.sensor.getResolution());
            textView.append("\nMax range: " + event.sensor.getMaximumRange());
            textView.append("\nPower: " + event.sensor.getPower() + "mA");

            currentTime = (int)System.currentTimeMillis();
            textView.append("\nSampling interval: " + (currentTime - lastTime) + "msec");
            lastTime = currentTime;

            textView.append("\n\nx=" + x + " y=" + y + " z=" + z);

            if(record){
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSS");

                if(data == ""){
                    data = sdf.format(date) + ", " + x + ", " + y + ", " + z + "\n";
                }else{
                    data += sdf.format(date) + ", " + x + ", " + y + ", " + z + "\n";
                }

            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    public void writeFileBundle(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String filename = "hgsensoractivity_data-" + sdf.format(date) + ".txt";
        File file = new File(getFilesDir(), filename);

        saveFile(data, file);
    }

    public void saveFile(String str, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(str);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void myBtnClick(View v){
        if (v.getId()==R.id.startButton){
            record = true;
            displayToast("start");
        }else if(v.getId()==R.id.stopButton){
            writeFileBundle();
            record = false;
            displayToast("stop");
            data = "";
        }
    }

    public void displayToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}