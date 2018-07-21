package com.example.hossam.neversleep.Activities;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hossam.neversleep.BluetoothService;
import com.example.hossam.neversleep.Database.ApplicationDatabase;
import com.example.hossam.neversleep.Database.Model.User;
import com.example.hossam.neversleep.R;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,BluetoothService.BlutoothServiceListener
{
    public static final String CURRENT_USER = "user";

    BluetoothService mBluetoothService;
    @BindView(R.id.main_activity_bbm_textview)
    TextView BPM_textview;
    @BindView(R.id.connect_button)
    Button connect_button;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mmDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initUI();
        //region listeners
        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBluetoothService.isReady())
                {
                    try {
                        mBluetoothService.deviceON();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //endregion
        User u = (User) getIntent().getExtras().getSerializable(CURRENT_USER);
        Toast.makeText(this, u.getName()+"\n"+u.getAge(),Toast.LENGTH_LONG ).show();

    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final Intent intent = new Intent(this,BluetoothService.class);
        startService(intent);
        final ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBluetoothService = ((BluetoothService.BluetoothServiceBinder)service).getServiceInstance();
                mBluetoothService.setmListener(MainActivity.this);
                mBluetoothService.setup();
                Log.i("Bluetooth", "service connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            // Handle the camera action
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRecieveNewBPM(int BPM)
    {
        BPM_textview.setText(String.valueOf(BPM));
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onBluetoothEnableRequest() {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetooth, 0);
    }

}
