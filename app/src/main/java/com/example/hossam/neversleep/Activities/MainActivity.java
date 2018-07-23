package com.example.hossam.neversleep.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hossam.neversleep.BluetoothService;
import com.example.hossam.neversleep.Database.ApplicationDatabase;
import com.example.hossam.neversleep.Database.Model.Record;
import com.example.hossam.neversleep.Database.Model.User;
import com.example.hossam.neversleep.HistoryActivity;
import com.example.hossam.neversleep.R;

import java.io.IOException;

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
    @BindView(R.id.nav_history)
    TextView navHistory;
    @BindView(R.id.nav_edit_profile)
    TextView navEditProfile;
    @BindView(R.id.nav_change_user)
    TextView navChangeUser;
    @BindView(R.id.nav_settings)
    TextView navSettings;
    @BindView(R.id.nav_help)
    TextView navHelp;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_bar_imageView)
    ImageView navBarImageView;
    @BindView(R.id.nav_bar_user_name)
    TextView navBarUserName;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mmDevice;
    User currentUser;
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
        currentUser = (User) getIntent().getExtras().getSerializable(CURRENT_USER);
        Toast.makeText(this, currentUser.getName()+"\n"+ currentUser.getAge(),Toast.LENGTH_LONG ).show();
        boolean gen = currentUser.getGender();
        navBarImageView.setImageResource((gen)?R.drawable.nav_bar_boy:R.drawable.nav_bar_girl);
        navBarUserName.setText(currentUser.getName());
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final Intent intent = new Intent(this,BluetoothService.class);
        startService(intent);
        final ServiceConnection serviceConnection = new ServiceConnection()
        {
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
        navHistory.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            }
        });
        navEditProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,UserSelectionActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        navChangeUser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserSelectionActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        navSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                intent.putExtra("id", currentUser.getId());
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        navHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserSelectionActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
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


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.i("NAV", "clicked");
        if (id == R.id.nav_history) {
            // Handle the camera action
        }
        else if(id == R.id.nav_change_user)
        {

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
    public void onConnected()
    {
        connect_button.setVisibility(View.INVISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Log.i("MAH", "handler run");
                Record record = new Record();
                int bpm = Integer.parseInt(BPM_textview.getText().toString());
                record.setHeart_rate(bpm);
                record.setUser_id(currentUser.getId());
                ApplicationDatabase database = new ApplicationDatabase(MainActivity.this);
                database.insertRecord(record);
                handler.postDelayed(this,10000 );
            }
        }, 10000);
    }

    @Override
    public void onBluetoothEnableRequest()
    {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetooth, 0);
    }

}
