package edu.uestc.carelink.ui.bluetooth;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.uestc.carelink.R;

@RequiresApi(api = Build.VERSION_CODES.S)
public class BluetoothActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothActivity";
    private RecyclerView recyclerView;
    private BluetoothInfoAdapter recyclerAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private boolean bluetoothSupport = true;
    private int requestCode = 1;
    private String[] permissionToApply = {Manifest.permission.ACCESS_COARSE_LOCATION,
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

                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                recyclerAdapter.addInfo(new BluetoothChannelInfo(device.getName(), device.getAddress()));
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                unregisterReceiver(this);
            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                Log.d(TAG,"Start to discover bluetooth channels");
            }
            unregisterReceiver(this);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        this.recyclerView = findViewById(R.id.bluetooth_recyclerview);
        this.recyclerAdapter = new BluetoothInfoAdapter();

        if(checkPermission()){
            ActivityCompat.requestPermissions(this, permissionToApply, requestCode);
        }
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(this.bluetoothAdapter == null){
            this.bluetoothSupport = false;
            Toast.makeText(this, getString(R.string.error_bluetooth_not_supported), Toast.LENGTH_SHORT).show();
            return;
        }
        if(!bluetoothAdapter.isEnabled()){
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ActivityResultLauncher<Intent> intentActivityResultLauncher =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result==null){
                        BluetoothActivity.this.bluetoothSupport = false;
                        Toast.makeText(BluetoothActivity.this, getString(R.string.error_bluetooth_start_fail), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(result.getResultCode()==RESULT_CANCELED){
                        BluetoothActivity.this.bluetoothSupport = false;
                        Toast.makeText(BluetoothActivity.this, getString(R.string.error_user_cancel_bluetooth), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(result.getResultCode()==RESULT_OK){
                        BluetoothActivity.this.bluetoothSupport = true;
                        //TODO debug code
                        Toast.makeText(BluetoothActivity.this, "OKOKOK", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
            intentActivityResultLauncher.launch(enableBluetoothIntent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode!=this.requestCode)
            return;
        boolean allPermitted = true;
        for(int state:grantResults){
            if(state!=PackageManager.PERMISSION_GRANTED){
                allPermitted = false;
                break;
            }
        }
        if(!allPermitted){
            bluetoothSupport = false;
        }
        Toast.makeText(this, getString(R.string.no_bluetooth_permission), Toast.LENGTH_LONG).show();
    }
    public void refresh(View view) {
        if(!this.bluetoothSupport)
            return;
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
    }
}