package edu.uestc.carelink.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.uestc.carelink.R;
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
    private Runnable queryLocalData = new Runnable() {
        @Override
        public void run() {
            List<String> dates = SensorDataManager.getInstance(ControlActivity.this).queryRecordEntityNames();
            List<SensorData> allLocalData = SensorDataManager.getInstance(ControlActivity.this).querySensorData(dates);
            for (int i = 0;i<allLocalData.size();i++){
                ControlActivity.this.recyclerViewAdapter.add(allLocalData.get(i));
            }
        }
    };
    private Runnable fetchFromBLE = new Runnable() {
        @Override
        public void run() {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        this.recyclerView = findViewById(R.id.chart_list_recyclerview);
        ControlActivity.this.recyclerViewAdapter = new DataItemAdapter();
        ControlActivity.this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ControlActivity.this.recyclerView.setAdapter(ControlActivity.this.recyclerViewAdapter);
        if(checkPermission()){
            requestPermissions(permissionToApply,requestCode);
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
                                Toast.makeText(ControlActivity.this, "OKOKOK", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            intentActivityResultLauncher.launch(enableBluetoothIntent);
        }
        this.executor = Executors.newFixedThreadPool(2);
        this.executor.submit(queryLocalData);
        this.executor.submit(fetchFromBLE);

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