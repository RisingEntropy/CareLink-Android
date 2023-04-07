package edu.uestc.carelink.ui.bluetooth;

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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.uestc.carelink.R;
import edu.uestc.carelink.ui.loadinganimation.AVLoadingIndicatorView;

@Deprecated
@RequiresApi(api = Build.VERSION_CODES.S)
public class BluetoothActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothActivity";
    private RecyclerView recyclerView;
    private BluetoothInfoAdapter recyclerAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private Button refreshButton;
    private boolean bluetoothSupport = true;
    private final int requestCode = 1;
    private TextView loadingLabel;
    private AVLoadingIndicatorView indicator;
    private final String[] permissionToApply = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT};

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                    return;
                Log.d(TAG, "device detect:"+device.getName());
                recyclerAdapter.addInfo(new BluetoothChannelInfo(device.getName(), device.getAddress(), device));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                BluetoothActivity.this.loadingLabel.setVisibility(View.INVISIBLE);
                BluetoothActivity.this.indicator.setVisibility(View.INVISIBLE);
                BluetoothActivity.this.refreshButton.setEnabled(true);
                unregisterReceiver(receiver);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                BluetoothActivity.this.loadingLabel.setVisibility(View.VISIBLE);
                BluetoothActivity.this.indicator.setVisibility(View.VISIBLE);
                BluetoothActivity.this.refreshButton.setEnabled(false);
                Log.d(TAG, "Start to discover bluetooth channels");
            }
//            unregisterReceiver(this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        this.loadingLabel = findViewById(R.id.ble_loading_label);
        this.indicator = findViewById(R.id.ble_loading_indicator);
        this.refreshButton = findViewById(R.id.button_refresh);

        this.recyclerView = findViewById(R.id.bluetooth_recyclerview);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));


        this.recyclerAdapter = new BluetoothInfoAdapter();

        this.recyclerView.setAdapter(this.recyclerAdapter);
        if (checkPermission()) {
            ActivityCompat.requestPermissions(this, permissionToApply, requestCode);
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
                                BluetoothActivity.this.bluetoothSupport = false;
                                Toast.makeText(BluetoothActivity.this, getString(R.string.error_bluetooth_start_fail), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (result.getResultCode() == RESULT_CANCELED) {
                                BluetoothActivity.this.bluetoothSupport = false;
                                Toast.makeText(BluetoothActivity.this, getString(R.string.error_user_cancel_bluetooth), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (result.getResultCode() == RESULT_OK) {
                                BluetoothActivity.this.bluetoothSupport = true;
                                //TODO debug code
                                Toast.makeText(BluetoothActivity.this, "OKOKOK", Toast.LENGTH_SHORT).show();

                                refresh(null);
                                return;
                            }
                        }
                    });
            intentActivityResultLauncher.launch(enableBluetoothIntent);
        }
        refresh(null);
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

    public void refresh(View view) {
        if (!this.bluetoothSupport)
            return;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
            return;

        this.bluetoothAdapter.startDiscovery();
//        bluetoothChannels.clear();
        recyclerAdapter.clearAllInfo();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
    }
    private boolean checkPermission(){
        for(String permission:permissionToApply){
            if(ContextCompat.checkSelfPermission(this,permission)!=PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
        unregisterReceiver(receiver);
    }
}