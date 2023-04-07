package edu.uestc.carelink.ui.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.zip.Inflater;

import edu.uestc.carelink.R;

public class BluetoothAdapter extends RecyclerView.Adapter<BluetoothAdapter.ViewHolder> {
    private final List<BluetoothChannelInfo> devices;

    public BluetoothAdapter(List<BluetoothChannelInfo> devices) {this.devices = devices;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.address.setText(devices.get(position).address);
        holder.name.setText(devices.get(position).name);
    }

    @Override
    public int getItemCount() {
        return this.devices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView name,address;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.bluetooth_name);
            address = itemView.findViewById(R.id.bluetooth_address);
        }
    }
}
