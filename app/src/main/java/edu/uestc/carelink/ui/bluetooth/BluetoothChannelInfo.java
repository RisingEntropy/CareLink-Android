package edu.uestc.carelink.ui.bluetooth;

import android.bluetooth.BluetoothDevice;

public class BluetoothChannelInfo {
    public String address,name,alias;
    public BluetoothDevice device;
    public BluetoothChannelInfo(){

    }

    public BluetoothChannelInfo(String name, String address, BluetoothDevice device){
        this.address = address;
        this.device = device;
        if(name==null)
            this.name = "N/A";
        else
            this.name = name;
    }
}
