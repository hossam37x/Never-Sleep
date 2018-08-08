package com.example.hossam.neversleep;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hossam.neversleep.Activities.MainActivity;
import com.example.hossam.neversleep.Database.ApplicationDatabase;
import com.example.hossam.neversleep.Database.Model.Record;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service
{
    private IBinder mBinder = new BluetoothServiceBinder();
    private BlutoothServiceListener mListener ;
    private boolean ready = false;
    private Map<String, BluetoothDevice> devices;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    public boolean stopWorker, connected_flag, visible_flag;

    MediaPlayer alarmPlayer;

    Vibrator v;

    ArrayList<Integer> BPMs;


    public boolean isReady() {
        return ready;
    }

    public void setmListener(BlutoothServiceListener mListener) {
        this.mListener = mListener;
    }

    public BluetoothService() {
    }

    private final class ServiceHandler extends Handler
    {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        this.stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmPlayer = MediaPlayer.create(getApplicationContext(), R.raw.demonstrative);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        BPMs = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    public void setup()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null)
        {
            if(!mBluetoothAdapter.isEnabled())
            {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //TODO send message yo activity
                //startActivityForResult(enableBluetooth, 0);
                mListener.onBluetoothEnableRequest();
                //return;
            }
            else
                pairWithDevice();
        }
    }

    public void pairWithDevice()
    {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("NeverBT")) // AT+PSWD? = 1234
                {
                    mmDevice = device;
                    ready = true;
                    break;
                }
            }
        }
    }

    public void gotNewBPM(int bpm)
    {
        Log.d(this.getClass().getName(),"new BPM" );
        mListener.onRecieveNewBPM(bpm);
        BPMs.add(bpm);
        if (bpm <= 60)
        {
            if (!alarmPlayer.isPlaying())
            {
                alarmPlayer.start();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            else
            {
                v.vibrate(200);
            }
        }
        else
        {
            if (alarmPlayer.isPlaying())
            {
                alarmPlayer.pause();
                v.cancel();
            }
        }
    }

    public void deviceON() throws IOException {
        try {
            on();
            visible();
            send("ON");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deviceOFF() throws IOException {
        try {
            send("OFF");

            off();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void on() throws IOException
    {
        if(!connected_flag)
        {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            ConncetAsyncTask conncetAsyncTask = new ConncetAsyncTask();
            conncetAsyncTask.execute();
            /*
            Toast.makeText(BluetoothService.this, "Bluetooth On!", Toast.LENGTH_SHORT).show();
            if(mmSocket.isConnected())
            {
                Log.d(this.getClass().getName(), "socket is connected");
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();
                listen();
                connected_flag = true;
            }*/
        }
    }

    public void off() throws IOException
    {
        Log.d(this.getClass().getName(), "ConnectedFlag = "+String.valueOf(connected_flag));
        if(connected_flag)
        {
            Log.d(this.getClass().getName(), "Off");
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            Toast.makeText(BluetoothService.this, "Bluetooth Off!", Toast.LENGTH_SHORT).show();
            connected_flag = false;
            calculateRecord();
        }

    }


    private void calculateRecord()
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
        record.setUser_id(MainActivity.currentUser.getId());
        record.setAvg_heart_rate(average);
        record.setMin_heart_rate(min);
        record.setMax_heart_rate(max);
        ApplicationDatabase applicationDatabase = new ApplicationDatabase(this);
        applicationDatabase.insertRecord(record);
    }

    public void visible() {
        if(!visible_flag) {
            Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //TODO send message to activity
            //startActivityForResult(getVisible, 0);

            visible_flag = true;
        }
    }

    public void list()
    {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        devices = new HashMap<>();
        ArrayList list = new ArrayList();

        for (BluetoothDevice bt : pairedDevices) {
            list.add(bt.getName());
            devices.put(bt.getName(), bt);
        }

        Toast.makeText(BluetoothService.this, "Showing Paired Devices", Toast.LENGTH_SHORT).show();
    }

    void send(String msg) throws IOException
    {
        msg += "\n";
        mmOutputStream.write(msg.getBytes());

        Toast.makeText(BluetoothService.this, ("Sent : " + msg), Toast.LENGTH_SHORT).show();
    }

    void listen()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10;
        Log.d(this.getClass().getName(), "Listening");
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    //Log.d(this.getClass().getName(), "while loop started");

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }

                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    final String bpm_text = "BPM: " + data;
                                    if (isInteger(data.trim()))
                                    {
                                        gotNewBPM(Integer.valueOf(data.trim()));
                                        //Log.d(this.getClass().getName(), "new bbm arrived");
                                    }
                                }
                                else
                                    {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }


        });
        workerThread.start();
    }

    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public class BluetoothServiceBinder extends Binder
    {
        public BluetoothService getServiceInstance()
        {
            return BluetoothService.this;
        }
    }

    public interface BlutoothServiceListener
    {
        public void onRecieveNewBPM(int BPM);
        public void onConnected();
        public void onConnectionStatusUpdate(boolean state);
        public void onBluetoothEnableRequest();
    }

    class ConncetAsyncTask extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                mmSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Toast.makeText(BluetoothService.this, "Bluetooth On!", Toast.LENGTH_SHORT).show();
            if(mmSocket.isConnected())
            {
                Toast.makeText(BluetoothService.this, "Bluetooth On!", Toast.LENGTH_SHORT).show();
                Log.d(this.getClass().getName(), "socket is connected");
                try {
                    mmOutputStream = mmSocket.getOutputStream();
                    mmInputStream = mmSocket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                listen();
                connected_flag = true;
                mListener.onConnectionStatusUpdate(true);
            }
            else
            {

                Toast.makeText(BluetoothService.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                mListener.onConnectionStatusUpdate(false);
            }
        }
    }
}