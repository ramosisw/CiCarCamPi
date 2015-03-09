package mx.itson.cicarcampi.socket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import mx.itson.cicarcampi.socket.thread.ClientThread;

public class ButtonsController extends ActionBarActivity implements View.OnTouchListener, ClientThread.Listener {
    ClientThread thread;
    boolean _LIGHT = false;
    ImageButton btn_light;
    private int PORT = 0;
    private String HOST = null;
    private ProgressDialog connectProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        Intent intent = getIntent();
        PORT = intent.getIntExtra("PORT",/*defaltvalue*/ 0);
        HOST = intent.getStringExtra("HOST");

        final int[] CLICKABLES = new int[]{
                R.id.btn_up, R.id.btn_down, R.id.btn_left, R.id.btn_right, R.id.btn_light
        };
        for (int i : CLICKABLES) {
            findViewById(i).setOnTouchListener(this);
        }
        btn_light = (ImageButton) findViewById(R.id.btn_light);
        connectProgress = ProgressDialog.show(ButtonsController.this, "Por favor espere ...", "Conectando ...", true);
        connectProgress.setCancelable(false);

        thread = new ClientThread(HOST, PORT);
        thread.setListener(this);
        thread.start();

    }

    @Override
    protected void onStop() {
        this.thread.closeConnection();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        thread.closeConnection();
        thread.interrupt();
        super.onBackPressed();
    }

    @Override
    public void onReceiveMessage(String message) {

    }

    @Override
    public void onDisconnected(String message) {
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.btn_up:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    thread.sendCommand("F1");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    thread.sendCommand("F2");
                }
                return false;
            case R.id.btn_down:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    thread.sendCommand("F0");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    thread.sendCommand("F2");
                }
                return false;
            case R.id.btn_left:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    thread.sendCommand("D1");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    thread.sendCommand("D2");
                }
                return false;
            case R.id.btn_right:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    thread.sendCommand("D0");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    thread.sendCommand("D2");
                }
                return false;
            case R.id.btn_light:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (_LIGHT) {
                        thread.sendCommand("L0");
                        btn_light.setImageResource(R.drawable.light_off);
                    } else {
                        thread.sendCommand("L1");
                        btn_light.setImageResource(R.drawable.light_on);

                    }
                    _LIGHT = !_LIGHT;
                }
                return false;
        }
        return false;
    }

    private void viewToast(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ButtonsController.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
