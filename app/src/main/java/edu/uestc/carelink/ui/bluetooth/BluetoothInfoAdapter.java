package edu.uestc.carelink.ui.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.uestc.carelink.R;

public class BluetoothInfoAdapter extends RecyclerView.Adapter<BluetoothInfoAdapter.ViewHolder> {
    private final List<BluetoothChannelInfo> devices;

    public BluetoothInfoAdapter(List<BluetoothChannelInfo> devices) {this.devices = devices;}
    public BluetoothInfoAdapter(){
        this.devices = new ArrayList<>();
    }

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

    public void addInfo(BluetoothChannelInfo channelInfo){
        this.devices.add(channelInfo);
        notifyItemInserted(this.devices.size()-1);
    }
    public void clearAllInfo(){
        this.devices.clear();
        notifyDataSetChanged();
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
