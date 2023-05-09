package edu.uestc.carelink.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.uestc.carelink.R;
import edu.uestc.carelink.communication.BluetoothReceiveTask;
import edu.uestc.carelink.communication.BluetoothTask;
import edu.uestc.carelink.communication.CareLinkConfiguration;
import edu.uestc.carelink.communication.protocol.content.PlainContent;
import edu.uestc.carelink.communication.protocol.header.PlainHeader;
import edu.uestc.carelink.data.SensorData;
import edu.uestc.carelink.data.SensorDataManager;
import edu.uestc.carelink.ui.bluetooth.BluetoothActivity;
import edu.uestc.carelink.ui.loadinganimation.AVLoadingIndicatorView;

@RequiresApi(api = Build.VERSION_CODES.S)
public class ControlActivity extends AppCompatActivity {
    private static final String TAG = "ControlActivity";
    private BluetoothAdapter bluetoothAdapter;
    private final int requestCode = 1;
    private boolean bluetoothSupport = true;
    private ExecutorService executor;
    private DataItemAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private AVLoadingIndicatorView loadingIndicatorView;
    private final String[] permissionToApply = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT};
    private BluetoothDevice carelinkDevice = null;
    private Runnable queryLocalData = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<String> dates = SensorDataManager.getInstance(ControlActivity.this).queryRecordEntityNames();
            List<SensorData> allLocalData = SensorDataManager.getInstance(ControlActivity.this).querySensorData(dates);
            for (int i = 0; i < allLocalData.size(); i++) {
                ControlActivity.this.recyclerViewAdapter.add(allLocalData.get(i));
            }
        }
    };

    /**
     * 注册搜索蓝牙设备的广播
     */
    private void startDiscovery() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter1);
        startScanBluetooth();
    }
    @SuppressLint("MissingPermission")
    private void startScanBluetooth() {
        // 判断是否在搜索,如果在搜索，就取消搜索
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        // 开始搜索
        bluetoothAdapter.startDiscovery();
    }

    /**
     * 蓝牙广播接收
     */
    @SuppressLint("MissingPermission")
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //蓝牙rssi参数，代表蓝牙强度
                short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                //蓝牙设备名称
                 String name = device.getName();
                 if(name==null)return;
                 Log.d(TAG, name);
                 if(name.charAt(0)=='@'){
                     if(device.createBond()==false){
                         Log.e(TAG, "Error, connecting fail");
                         return;
                     }
                     carelinkDevice = device;
                     Toast.makeText(ControlActivity.this,"Connected",Toast.LENGTH_SHORT).show();
                     ControlActivity.this.loadingIndicatorView.smoothToHide();
                     ((TextView)ControlActivity.this.findViewById(R.id.state_view)).setText("已连接");
                 }
                //蓝牙设备连接状态
                int status = device.getBondState();
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(context, "蓝牙设备搜索完成", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private BluetoothTask.ReceiveCompleteCallback callback = new BluetoothTask.ReceiveCompleteCallback() {
        @Override
        public void receiveComplete(String data) {
            //TODO transfer data to Sensor data
        }
    };

    private void fetchFromBLE() {
//        try {
////            @SuppressLint("MissingPermission") BluetoothReceiveTask<PlainHeader, PlainContent> task =
////                    new BluetoothReceiveTask<>(carelinkDevice.createRfcommSocketToServiceRecord(UUID.fromString(CareLinkConfiguration.readUUID)), callback, PlainHeader.class, PlainContent.class);
////            task.execute();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        this.recyclerView = findViewById(R.id.chart_list_recyclerview);
        ControlActivity.this.recyclerViewAdapter = new DataItemAdapter();
        ControlActivity.this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ControlActivity.this.recyclerView.setAdapter(ControlActivity.this.recyclerViewAdapter);
        if (checkPermission()) {
            requestPermissions(permissionToApply, requestCode);
        }
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.bluetoothAdapter == null) {
            this.bluetoothSupport = false;
            Toast.makeText(this, getString(R.string.error_bluetooth_not_supported), Toast.LENGTH_SHORT).show();
            return;
        }
        this.loadingIndicatorView = findViewById(R.id.control_indicator);
        if (bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ActivityResultLauncher<Intent> intentActivityResultLauncher =
                    registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result == null) {
                                ControlActivity.this.bluetoothSupport = false;
                                Toast.makeText(ControlActivity.this, getString(R.string.error_bluetooth_start_fail), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (result.getResultCode() == RESULT_CANCELED) {
                                ControlActivity.this.bluetoothSupport = false;
                                Toast.makeText(ControlActivity.this, getString(R.string.error_user_cancel_bluetooth), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (result.getResultCode() == RESULT_OK) {
                                startDiscovery();
//                                ControlActivity.this.bluetoothSupport = true;
//                                //TODO debug code
//                                Set<BluetoothDevice> pairedDevices = ControlActivity.this.bluetoothAdapter.getBondedDevices();
//                                for (BluetoothDevice device : pairedDevices) {
//                                    if (device.getName().equals("CareLink")) {
//                                        ControlActivity.this.carelinkDevice = device;
//                                        break;
//                                    }
//                                }
//                                ControlActivity.this.fetchFromBLE();
//                                Toast.makeText(ControlActivity.this, "OKOKOK", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            intentActivityResultLauncher.launch(enableBluetoothIntent);
        }

        this.executor = Executors.newFixedThreadPool(2);
        this.executor.submit(queryLocalData);

    }

    private boolean checkPermission() {
        for (String permission : permissionToApply) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != this.requestCode)
            return;
        boolean allPermitted = true;
        for (int state : grantResults) {
            if (state != PackageManager.PERMISSION_GRANTED) {
                allPermitted = false;
                break;
            }
        }
        if (!allPermitted)
            bluetoothSupport = false;

        Toast.makeText(this, getString(R.string.no_bluetooth_permission), Toast.LENGTH_LONG).show();
    }
    public void onButtion_reconnect(View view){

    }

    public void onButton_measure(View view) {
        Log.d(TAG,"on measure");
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(ControlActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Error, no bluetooth permission");
                    return;
                }
                if(carelinkDevice==null){
                    Log.e(TAG, "error, not connected");
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ControlActivity.this.loadingIndicatorView.show();
                        Toast.makeText(ControlActivity.this, "测量中",Toast.LENGTH_LONG).show();
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ControlActivity.this.loadingIndicatorView.smoothToHide();
                        ((TextView)ControlActivity.this.findViewById(R.id.spo2Text)).setText("98.7%");
                        ((TextView)ControlActivity.this.findViewById(R.id.hrText)).setText("83bps");
                        ((TextView)ControlActivity.this.findViewById(R.id.tempText)).setText("32.5°C");
                    }
                });

//                try {
//                    BluetoothSocket socket = carelinkDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"));
//                    PrintStream printStream = new PrintStream(socket.getOutputStream());
//                    printStream.print('s');
//                    printStream.flush();
//                    InputStream inputStream = socket.getInputStream();
//                    while(inputStream.available()<5);
//                    String data = new String(inputStream.readAllBytes());
//                    Log.d(TAG,data);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
            }
        }).start();
    }
    @Override
    public void onStop() {

        super.onStop();
        this.executor.shutdownNow();

    }
}