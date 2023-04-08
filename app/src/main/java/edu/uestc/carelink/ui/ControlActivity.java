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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

@RequiresApi(api = Build.VERSION_CODES.S)
public class ControlActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private final int requestCode = 1;
    private boolean bluetoothSupport = true;
    private ExecutorService executor;
    private DataItemAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private final String[] permissionToApply = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT};
    private BluetoothDevice carelinkDevice;
    private Runnable queryLocalData = new Runnable() {
        @Override
        public void run() {
            List<String> dates = SensorDataManager.getInstance(ControlActivity.this).queryRecordEntityNames();
            List<SensorData> allLocalData = SensorDataManager.getInstance(ControlActivity.this).querySensorData(dates);
            for (int i = 0; i < allLocalData.size(); i++) {
                ControlActivity.this.recyclerViewAdapter.add(allLocalData.get(i));
            }
        }
    };
    private BluetoothTask.ReceiveCompleteCallback callback = new BluetoothTask.ReceiveCompleteCallback() {
        @Override
        public void receiveComplete(String data) {
            //TODO transfer data to Sensor data
        }
    };
    private void fetchFromBLE(){
        try {
            @SuppressLint("MissingPermission") BluetoothReceiveTask<PlainHeader, PlainContent> task =
                    new BluetoothReceiveTask<>(carelinkDevice.createRfcommSocketToServiceRecord(UUID.fromString(CareLinkConfiguration.readUUID)), callback, PlainHeader.class, PlainContent.class);
            task.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        if (!bluetoothAdapter.isEnabled()) {
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
                                ControlActivity.this.bluetoothSupport = true;
                                //TODO debug code
                                Set<BluetoothDevice> pairedDevices = ControlActivity.this.bluetoothAdapter.getBondedDevices();
                                for(BluetoothDevice device:pairedDevices){
                                    if(device.getName().equals("CareLink")){
                                        ControlActivity.this.carelinkDevice = device;
                                        break;
                                    }
                                }
                                ControlActivity.this.fetchFromBLE();
                                Toast.makeText(ControlActivity.this, "OKOKOK", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            intentActivityResultLauncher.launch(enableBluetoothIntent);
        }

        this.executor = Executors.newFixedThreadPool(2);
        this.executor.submit(queryLocalData);

    }
    private boolean checkPermission(){
        for(String permission:permissionToApply){
            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED)
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
    @Override
    public void onStop() {

        super.onStop();
        this.executor.shutdownNow();

    }
}