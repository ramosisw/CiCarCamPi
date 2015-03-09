package mx.itson.cicarcampi.socket.vistas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import mx.itson.cicarcampi.socket.AccelerometerController;
import mx.itson.cicarcampi.socket.ButtonsController;
import mx.itson.cicarcampi.socket.R;

public class Startup extends ActionBarActivity implements View.OnClickListener {
    private static final int RESULT_SETTINGS = 1;
    Intent control = null;
    private int _SERVER_PORT = 8080;
    private String _SERVER_HOST = "192.168.10.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        showUserSettings();
        final int[] CLICKABLES = new int[]{
                R.id.btn_acelerometro, R.id.btn_botones
        };
        for (int i : CLICKABLES) {
            findViewById(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_acelerometro:
                control = new Intent(this, AccelerometerController.class);
                break;
            case R.id.btn_botones:
                control = new Intent(this, ButtonsController.class);
                break;
            default:
        }
        control.putExtra("PORT", _SERVER_PORT);
        control.putExtra("HOST", _SERVER_HOST);
        startActivity(control);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                break;

        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                showUserSettings();
                break;
        }

    }

    private void showUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            _SERVER_HOST = sharedPrefs.getString("pref_host", "192.168.10.1");
            _SERVER_PORT = Integer.parseInt(sharedPrefs.getString("pref_port", "8080"));
            Toast.makeText(this, String.format("%s:%d", _SERVER_HOST, _SERVER_PORT), Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            //Log.println(1,ex.getCause().toString(),ex.getMessage());
        }
    }
}

