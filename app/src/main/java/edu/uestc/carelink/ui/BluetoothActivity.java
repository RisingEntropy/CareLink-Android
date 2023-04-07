package edu.uestc.carelink.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import edu.uestc.carelink.R;

public class BluetoothActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private boolean bluetoothSupport = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blooth);
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
                        Toast.makeText(BluetoothActivity.this, "OKOKOK", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
            intentActivityResultLauncher.launch(enableBluetoothIntent);
        }
    }
}