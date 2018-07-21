package com.example.hossam.neversleep;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service
{
    private IBinder mBinder = new BluetoothServiceBinder();
    private BlutoothServiceListener mListener ;
    private boolean ready = false;

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

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    public void gotNewBPM(int bpm)
    {
        mListener.onRecieveNewBPM(bpm);
        if (bpm <= 60) {
            if (!alarmPlayer.isPlaying()) {
                alarmPlayer.start();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            }else{
                v.vibrate(200);
            }

        } else {
            if (alarmPlayer.isPlaying()) {
                alarmPlayer.pause();
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
            }

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
    }

    public void on() throws IOException
    {
        if(!connected_flag)
        {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            Toast.makeText(BluetoothService.this, "Bluetooth On!", Toast.LENGTH_SHORT).show();
            if(mmSocket.isConnected())
            {
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();
                listen();
                connected_flag = true;
            }
        }
    }

    public void off() throws IOException
    {
        if(connected_flag) {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            Toast.makeText(BluetoothService.this, "Bluetooth Off!", Toast.LENGTH_SHORT).show();
            connected_flag = false;
        }
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

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        Thread.sleep(100);
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
                                    }
                                }
                                else {
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
        public void onBluetoothEnableRequest();
    }
}