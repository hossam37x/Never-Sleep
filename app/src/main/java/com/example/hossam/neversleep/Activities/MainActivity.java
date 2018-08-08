package com.example.hossam.neversleep.Activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.media.MediaMetadataCompat;
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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// import com.example.hossam.neversleep.BluetoothService;
import com.example.hossam.neversleep.BluetoothService;
import com.example.hossam.neversleep.Database.ApplicationDatabase;
import com.example.hossam.neversleep.Database.Model.Record;
import com.example.hossam.neversleep.Database.Model.User;
import com.example.hossam.neversleep.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,BluetoothService.BlutoothServiceListener
{
    public static final String CURRENT_USER = "user";
    //region Bindings
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
    @BindView(R.id.heart_animate)
    ImageView heartAnimate;
    @BindView(R.id.graph)
    GraphView graphView;
    @BindView(R.id.text_linear_layout)
    LinearLayout textLinear;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    //endregion

    //region Variables
    public static User currentUser;

    private Map<String, BluetoothDevice> devices;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker, connected_flag, visible_flag;

    MediaPlayer alarmPlayer;

    Vibrator v;

    ArrayList<Integer> BPMs;

    Queue<Integer> graphBufferQueue;

    Handler graphUpdateHandler;
    Runnable graphUpdateRunnable;
    LineGraphSeries<DataPoint> mGraphSeries;
    double graphLastX = 0;
    boolean graphHandlerLoopFlag = true;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initUI();
        currentUser = (User) getIntent().getExtras().getSerializable(CURRENT_USER);
        boolean gen = currentUser.getGender();
        //graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        //graphView.getGridLabelRenderer().setGridColor(0xff696a);
        navBarImageView.setImageResource((gen)?R.drawable.boy:R.drawable.nav_bar_girl);
        navBarUserName.setText(currentUser.getName());
        alarmPlayer = MediaPlayer.create(getApplicationContext(), R.raw.demonstrative);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        connected_flag = false;
        visible_flag = false;
        BPMs = new ArrayList<>();

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
        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothBroadcastReciever,intentFilter );
        //setup();
        graphView.setVisibility(View.INVISIBLE);
        graphBufferQueue = new LinkedList<>();
        mGraphSeries = new LineGraphSeries<>();
        graphView.addSeries(mGraphSeries);
        graphView.getViewport().setMaxY(200);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxX(1000);
        graphView.getViewport().setMinX(0);
        graphUpdateHandler = new Handler();
        graphUpdateRunnable = new Runnable() {
            @Override
            public void run()
            {
                if(!graphBufferQueue.isEmpty())
                {
                    graphLastX+=.01;
                    Log.d(MainActivity.class.getName(), "graph update");
                    final int val = graphBufferQueue.remove();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGraphSeries.appendData(new DataPoint(graphLastX,val), false, 1000);
                        }
                    });
                }
                if(graphHandlerLoopFlag)
                    graphUpdateHandler.postDelayed(this,10 );
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void initUI()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
                Intent intent = new Intent(MainActivity.this,EditProfileActivity.class);
                intent.putExtra("user", currentUser);
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
                Intent intent = new Intent(MainActivity.this,HelpActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        heartScaleOutAnimation();
    }

    private void showProgress(boolean visibilty) {
        if (visibilty)
        {
            progressBar.setVisibility(View.VISIBLE);
            connect_button.setVisibility(View.INVISIBLE);
        }
        else
        {
            progressBar.setVisibility(View.INVISIBLE);
            connect_button.setVisibility(View.VISIBLE);
        }
    }

    public void deviceON(View view) throws IOException {
        mBluetoothService.on();
        showProgress(true);
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
    public void onRecieveNewBPM(final int BPM)
    {
        Log.d(MainActivity.this.getClass().getName(), "new BPM");
        if(BPM==0)
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(MainActivity.this.getClass().getName(), "new BPM");
                BPM_textview.setText(String.valueOf(BPM));
            }
        });
        BPMs.add(BPM);
        graphBufferQueue.add(BPM);
        Log.d("service interface","receive");
    }

    /*private void calculateRecord()
    {
        Integer[] bpms = new Integer[BPMs.size()];
        BPMs.toArray(bpms);
        Integer sum = 0;
        for(Integer val:bpms)
        {
            sum+=val;
        }
        Arrays.sort(bpms);
        int average = sum/bpms.length;
        int min = bpms[0];
        int max = bpms[bpms.length-1];
        Record record = new Record();
        record.setUser_id(currentUser.getId());
        record.setAvg_heart_rate(average);
        record.setMin_heart_rate(min);
        record.setMax_heart_rate(max);
        ApplicationDatabase applicationDatabase = new ApplicationDatabase(this);
        applicationDatabase.insertRecord(record);
    }*/

    @Override
    public void onConnected()
    {
        connect_button.setVisibility(View.INVISIBLE);
        Log.d("service interface","onconnected");


    }

    @Override
    public void onConnectionStatusUpdate(boolean state) {
        if(state)
        {
            graphView.setVisibility(View.VISIBLE);
            graphHandlerLoopFlag = true;
            graphUpdateHandler.postDelayed(graphUpdateRunnable,10 );
            textLinear.setVisibility(View.INVISIBLE);
            showProgress(false);
            connect_button.setText("Disconncet");
        }
        else
        {

            graphView.setVisibility(View.INVISIBLE);
            graphHandlerLoopFlag = false;
            connect_button.setVisibility(View.VISIBLE);
            connect_button.setText("Connect");
            showProgress(false);
        }
    }

    @Override
    public void onBluetoothEnableRequest()
    {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetooth, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0)
        {
            if(resultCode ==Activity.RESULT_OK)
            {
                //Toast.makeText(this, "result OK!",Toast.LENGTH_SHORT ).show();
            }
        }
    }

    final BroadcastReceiver bluetoothBroadcastReciever = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR );
                switch (state)
                {
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(MainActivity.this, "Pair!",Toast.LENGTH_LONG ).show();
                        mBluetoothService.pairWithDevice();
                        break;
                    case BluetoothAdapter.STATE_OFF:

                        break;
                }
            }
        }
    };

    public void heartScaleOutAnimation()
    {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f
                , 1f, 0.5f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);

        heartAnimate.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                heartScaleInAnimation();
            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationStart(Animation animation) {

            }
        });
    }

    public void heartScaleInAnimation()
    {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.5f, 1f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);

        heartAnimate.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                heartScaleOutAnimation();
            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationStart(Animation animation) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothBroadcastReciever);
    }
}
