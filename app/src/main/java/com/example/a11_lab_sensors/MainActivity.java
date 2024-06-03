package com.example.a11_lab_sensors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView batteryText;
    ImageView batteryImage;
    IntentFilter intentFilter;
    int deviceStatus;
    int batteryLevel;
    TextView motionText1, envText, posText;
    private Sensor sensor, sensor1, sensor2;
    private SensorManager sensorManager;
    private SensorEventListener gyroscopeEventListener;
    private SensorEventListener accelerometerEventListener;
    private SensorEventListener thermometerEventListener;
    private SensorEventListener magnetometerEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryText = (TextView) findViewById(R.id.batteryStatus);
        batteryImage = (ImageView) findViewById(R.id.batteryImage);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        motionText1 = (TextView) findViewById(R.id.motionText);
        envText = (TextView) findViewById(R.id.enviromentText);
        posText = (TextView) findViewById(R.id.positionText);

// Initialize Gyroscope Sensor
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    String formattedX = String.format("%.2f", x);
                    String formattedY = String.format("%.2f", y);
                    String formattedZ = String.format("%.2f", z);

                    motionText1.setText("Accelerometer\nX: " + formattedX + "\nY: "
                            + formattedY + "\nZ: " + formattedZ);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Do something if sensor accuracy changes
            }
        };

        if (sensor != null) {
            sensorManager.registerListener(accelerometerEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        sensor1 = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        thermometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                        float temperature = event.values[0];

                        envText.setText("Temperature: " + temperature + " °C");
                    }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        if (sensor1 != null) {
            sensorManager.registerListener(thermometerEventListener, sensor1, SensorManager.SENSOR_DELAY_NORMAL);
        }


    }

    public void setBatteryImageView(int batteryLevel){

        if((batteryLevel<=100)&&(batteryLevel>80)){
            batteryImage.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_battery_100));
        } else if ((batteryLevel<=80)&&(batteryLevel>50)){
            batteryImage.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_battery_80)
            );
        } else if ((batteryLevel<=50)&&(batteryLevel>20)){
            batteryImage.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_battery_50)
            );
        } else if (batteryLevel<=20){
            batteryImage.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_battery_20)
            );
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryLevel = (int) (((float) level / (float) scale) * 100.0f);

            setBatteryImageView(batteryLevel);
            String text;

            if (deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING){
                batteryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_charging));
                text = "Charging at " + batteryLevel + " %";
                batteryText.setText(text);
            }
            if (deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING){
                text = "Charging at " + batteryLevel + " %";
                batteryText.setText(text);
            }
            if (deviceStatus == BatteryManager.BATTERY_STATUS_FULL){
                text = "Battery full at " + batteryLevel + " %";
                batteryText.setText(text);
            }
            if (deviceStatus == BatteryManager.BATTERY_STATUS_UNKNOWN){
                text = "Unknown at " + batteryLevel + " %";
                batteryText.setText(text);
            }
            if (deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING){
                text = "Not charging at " + batteryLevel + " %";
                batteryText.setText(text);
            }
        }
    };

    @Override
    protected void onStart(){
        MainActivity.this.registerReceiver(broadcastReceiver, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop(){
        MainActivity.this.unregisterReceiver(broadcastReceiver);
        sensorManager.unregisterListener(gyroscopeEventListener);
        super.onStop();

    }
}
