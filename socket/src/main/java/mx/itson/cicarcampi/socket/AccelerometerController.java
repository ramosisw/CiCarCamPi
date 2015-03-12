package mx.itson.cicarcampi.socket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

import mx.itson.cicarcampi.socket.thread.ClientThread;

public class AccelerometerController extends ActionBarActivity implements SensorEventListener, View.OnTouchListener, ClientThread.Listener {
    /**
     * Called when the activity is first created.
     */
    ClientThread thread;
    TextView x, y, z;
    boolean _yAx;
    PowerManager pm;
    PowerManager.WakeLock wl;
    boolean _LIGHT = false, _CONNECTED = false;
    ImageButton btn_light;
    private int PORT = 0;
    private String HOST = null;
    private ProgressDialog connectProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "AccelerometerController");
        wl.acquire();
        setContentView(R.layout.activity_accelerometer_controller);

        Intent intent = getIntent();
        PORT = intent.getIntExtra("PORT",/*defaltvalue*/ 0);
        HOST = intent.getStringExtra("HOST");

        x = (TextView) findViewById(R.id.xID);
        y = (TextView) findViewById(R.id.yID);
        z = (TextView) findViewById(R.id.zID);

        final int[] CLICKABLES = new int[]{
                R.id.btn_brake, R.id.btn_accelerator, R.id.btn_light, R.id.btn_connected
        };
        for (int i : CLICKABLES) {
            findViewById(i).setOnTouchListener(this);
        }
        btn_light = (ImageButton) findViewById(R.id.btn_light);
        connectProgress = ProgressDialog.show(AccelerometerController.this, "Por favor espere ...", "Conectando ...", true);
        connectProgress.setCancelable(false);
        thread = new ClientThread(HOST, PORT);
        thread.setListener(this);
        thread.start();
    }

    protected void onResume() {
        super.onResume();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "AccelerometerController");
        wl.acquire();
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);
        wl.release();
        super.onPause();
    }

    @Override
    protected void onStop() {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(this);
        thread.closeConnection();
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
            this.x.setText("X = " + x);
            this.y.setText("Y = " + y);
            this.z.setText("Z = " + z);
            if (y < -2) {
                if (!_yAx) {
                    sendCommand("D1");
                }
                _yAx = true;
            } else if (y > 2) {
                if (!_yAx) {
                    sendCommand("D0");
                }
                _yAx = true;
            } else {
                if (_yAx) {
                    sendCommand("D2");
                }
                _yAx = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onBackPressed() {
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);
        thread.closeConnection();
        thread.interrupt();
        super.onBackPressed();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.btn_accelerator:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sendCommand("F1");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    sendCommand("F2");
                }
                return false;
            case R.id.btn_brake:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sendCommand("F0");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    sendCommand("F2");
                }
                return false;
            case R.id.btn_light:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (_LIGHT) {
                        sendCommand("L0");
                        btn_light.setImageResource(R.drawable.light_off);
                    } else {
                        sendCommand("L1");
                        btn_light.setImageResource(R.drawable.light_on);

                    }
                    _LIGHT = !_LIGHT;
                }
                return false;
            case R.id.btn_connected:
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    _CONNECTED = !((ToggleButton) view).isChecked();
                }
                return false;
        }
        return false;
    }

    private void sendCommand(String command) {
        if (_CONNECTED) {
            thread.sendCommand(command);
        }
    }

    @Override
    public void onReceiveMessage(String message) {

    }

    @Override
    public void onDisconnected(String message) {
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);
        viewToast(message);
        finish();
    }

    @Override
    public void onConnected() {
        connectProgress.dismiss();
    }

    @Override
    public void onSendMessage() {

    }

    private void viewToast(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(AccelerometerController.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
