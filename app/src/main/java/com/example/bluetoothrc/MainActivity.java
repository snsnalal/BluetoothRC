package com.example.bluetoothrc;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.util.Range;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter btAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    Button btnSearch,btnpaired,stt,btnstop;//, btnforward, btnbackward, btnright, btnleft;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;
    ListView listView;
    BluetoothSocket btSocket;
    ConnectedThread connectedThread;
    TextView textView;
    Switch sw;

    Intent i;
    SpeechRecognizer mRecognizer;



    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btnSearch = (Button) findViewById(R.id.btn_search);
        /*
        btnforward = (Button)findViewById(R.id.button);
        btnleft = (Button)findViewById(R.id.button2);
        btnright = (Button)findViewById(R.id.button3);
        btnbackward = (Button)findViewById(R.id.button5);
*/

        btnstop = (Button)findViewById(R.id.button4);
        btnpaired = (Button)findViewById(R.id.button7);

        textView = (TextView)findViewById(R.id.textView);
        sw = (Switch)findViewById(R.id.switch1);
        stt = (Button)findViewById(R.id.button6);

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        listView = (ListView) findViewById(R.id.listview);
        ActivityCompat.requestPermissions(MainActivity.this, permission_list,  1);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceAddressArray = new ArrayList<>();
        listView.setAdapter(btArrayAdapter);

        listView.setOnItemClickListener(new myOnItemClickListener());

        btnpaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btArrayAdapter.clear();
                if(deviceAddressArray!=null && !deviceAddressArray.isEmpty()){ deviceAddressArray.clear(); }
                pairedDevices = btAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        btArrayAdapter.add(deviceName);
                        deviceAddressArray.add(deviceHardwareAddress);
                    }
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btArrayAdapter.clear();
                if(deviceAddressArray!=null && !deviceAddressArray.isEmpty()){ deviceAddressArray.clear(); }
                pairedDevices = btAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        btArrayAdapter.add(deviceName);
                        deviceAddressArray.add(deviceHardwareAddress);
                    }
                }
            }
        });

        /*
        btnforward.setOnTouchListener(new RepeatListener(1000, 1000, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectedThread!=null){ connectedThread.write("-1"); }
            }
        }));


        btnbackward.setOnTouchListener(new RepeatListener(1000, 1000, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectedThread!=null){ connectedThread.write("-4"); }
            }
        }));


        btnright.setOnTouchListener(new RepeatListener(1000, 1000, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectedThread!=null){ connectedThread.write("-2"); }
            }
        }));



        btnleft.setOnTouchListener(new RepeatListener(1000, 1000, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectedThread!=null){ connectedThread.write("-3"); }
            }
        }));
        */
          btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectedThread!=null)
                {
                    for(int i = 0; i < 2; i++)
                    {
                        if(i==0)
                        {
                            if(connectedThread!=null)
                            {
                                connectedThread.write(String.valueOf(63));
                            }

                        }
                        else
                        {
                            if(connectedThread!=null)
                            {
                                connectedThread.write(String.valueOf(191));
                            }

                        }
                    }
                }
            }
        });
/*
        btnstop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if(connectedThread!=null){ connectedThread.write(String.valueOf(270)); }
                }
                return false;
            }
        });
*/
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    onDestroy();
                }
            }
        });
        stt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("음성인식 시작!");
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                    //권한을 허용하지 않는 경우
                } else {
                    //권한을 허용한 경우
                    try {
                        mRecognizer.startListening(i);
                    } catch(SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        JoystickView joystick = (JoystickView) findViewById(R.id.joystick);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onMove(int angle, int strength) {
                // do whatever you want
                double rad = Math.toRadians(angle);
                double dist = strength / 100.0;
                double x = dist * Math.cos(rad);
                double y = dist * Math.sin(rad);

                Range<Integer> bounds = new Range<>(-127, 127);

                int adjX = bounds.clamp((int) ((double) x * 127));
                int adjY = bounds.clamp((int) ((double) y * -127));
                int ax = map2(adjX,-127, 127, 0, 127);
                int ay = map2(adjY,-127, 127, 128, 255);

                //if(connectedThread!=null){ connectedThread.write(String.valueOf(ax)+" "+String.valueOf(ay)); }
                for(int i = 0; i < 2; i++)
                {
                    if(i==0)
                    {
                        if(connectedThread!=null)
                        {
                            connectedThread.write(String.valueOf(ax));
                        }

                    }
                    else
                    {
                        if(connectedThread!=null)
                        {
                            connectedThread.write(String.valueOf(ay));
                        }

                    }
                }


                }
        });
    }

    public int map2(int x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    long _startTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(System.currentTimeMillis()-_startTime > 3000)
            {
                Toast.makeText(getApplicationContext(), "종료하려면 한 번 더 누르세요", Toast.LENGTH_LONG).show();
                _startTime = System.currentTimeMillis();
            }
            else
            {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }
        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            Toast.makeText(getApplicationContext(), "끝", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int error) {
            Toast.makeText(getApplicationContext(), "천천히 다시 말해주세요.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onResults(Bundle results) {
            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            textView.setText(rs[0]);
            if (textView.getText().equals("전진"))
            {
                for(int i = 0; i < 2; i++)
                {
                    if(i==0)
                    {
                        if(connectedThread!=null)
                        {
                            connectedThread.write(String.valueOf(107));
                        }
                    }
                    else
                    {
                        if(connectedThread!=null)
                        {
                            connectedThread.write(String.valueOf(161));
                        }

                    }
                }
            }
            else if(textView.getText().equals("후진"))
            {
                for(int i = 0; i < 2; i++)
                {
                    if(i==0)
                    {
                        if(connectedThread!=null)
                        {
                            connectedThread.write(String.valueOf(55));
                        }

                    }
                    else
                    {
                        if(connectedThread!=null)
                        {
                            connectedThread.write(String.valueOf(204));
                        }

                    }
                }
            }
            else if(textView.getText().equals("왼쪽"))
            {
                for(int i = 0; i < 2; i++)
                {
                    if(i==0)
                    {
                        if(connectedThread!=null)
                        {
                            connectedThread.write(String.valueOf(69));
                        }

                    }
                    else
                    {
                        if(connectedThread!=null)
                        {
                            connectedThread.write(String.valueOf(184));
                        }

                    }
                }
            }
            else if(textView.getText().equals("오른쪽"))
            {
                for(int i = 0; i < 2; i++)
                {
                    if(i==0)
                    {
                        if(connectedThread!=null)
                        {
                            connectedThread.write(String.valueOf(76));
                        }

                    }
                    else
                    {
                        if(connectedThread!=null)
                        {
                            connectedThread.write(String.valueOf(186));
                        }

                    }
                }
            }
            else if(textView.getText().equals("정지"))
            {
                for(int i = 0; i < 2; i++)
                {
                    if(i==0)
                    {
                        if(connectedThread!=null)
                        {
                            connectedThread.write(String.valueOf(63));
                        }

                    }
                    else
                    {
                        if(connectedThread!=null)
                        {
                            connectedThread.write(String.valueOf(191));
                        }

                    }
                }
            }
            //mRecognizer.startListening(i); //음성인식이 계속 되는 구문
        }
    };


    public class myOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), btArrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();

            final String name = btArrayAdapter.getItem(position); // get name
            final String address = deviceAddressArray.get(position); // get address
            boolean flag = true;

            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            // create & connect socket

            try {
                btSocket = createBluetoothSocket(device);
                btSocket.connect();
            } catch (IOException e) {
                flag = false;
                Toast.makeText(getApplicationContext(), "연결 실패", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            if(flag){
                Toast.makeText(getApplicationContext(), "연결 성공", Toast.LENGTH_SHORT).show();
                connectedThread = new ConnectedThread(btSocket);
                connectedThread.start();
            }

        }
    }
    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        @Override
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }
        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                int a = Integer.parseInt(input);
                mmOutStream.write(a);
                Log.d("Mytag", String.valueOf(a));
            } catch (IOException e) {
            }
        }
        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e("Mytag", "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
    protected void onDestroy() {
        super.onDestroy();
        if(mRecognizer!=null){
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer=null;
        }
    }
    public class RepeatListener implements View.OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final View.OnClickListener clickListener;
        private View touchedView;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if(touchedView.isEnabled()) {
                    handler.postDelayed(this, normalInterval);
                    clickListener.onClick(touchedView);
                } else {
                    // if the view was disabled by the clickListener, remove the callback
                    handler.removeCallbacks(handlerRunnable);
                    touchedView.setPressed(false);
                    touchedView = null;
                }
            }
        };

        public RepeatListener(int initialInterval, int normalInterval,
                              View.OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.removeCallbacks(handlerRunnable);
                    handler.postDelayed(handlerRunnable, initialInterval);
                    touchedView = view;
                    touchedView.setPressed(true);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                    if(connectedThread!=null){ connectedThread.write(String.valueOf(700)); }
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(handlerRunnable);
                    touchedView.setPressed(false);
                    touchedView = null;
                    return true;
            }
            return false;
        }
    }
}