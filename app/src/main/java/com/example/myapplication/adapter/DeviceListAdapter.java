package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.dto.DeviceInfo;

import java.util.List;

/**
 * 设备列表适配器
 */
public class DeviceListAdapter extends BaseAdapter {
    
    private Context context;
    private List<DeviceInfo> deviceList;
    private LayoutInflater inflater;
    
    public DeviceListAdapter(Context context, List<DeviceInfo> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
        this.inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return deviceList.size();
    }
    
    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_device, parent, false);
            holder = new ViewHolder();
            holder.deviceIcon = convertView.findViewById(R.id.device_icon);
            holder.deviceName = convertView.findViewById(R.id.device_name);
            holder.deviceType = convertView.findViewById(R.id.device_type);
            holder.deviceStatus = convertView.findViewById(R.id.device_status);
            holder.lastActive = convertView.findViewById(R.id.last_active);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        DeviceInfo device = deviceList.get(position);
        
        // 设置设备图标
        if ("ANDROID".equals(device.getDeviceType())) {
            holder.deviceIcon.setImageResource(R.drawable.ic_android);
        } else if ("IOS".equals(device.getDeviceType())) {
            holder.deviceIcon.setImageResource(R.drawable.ic_apple);
        } else {
            holder.deviceIcon.setImageResource(R.drawable.ic_device);
        }
        
        // 设置设备名称
        holder.deviceName.setText(device.getDeviceName() != null ? 
                device.getDeviceName() : "未知设备");
        
        // 设置设备类型
        holder.deviceType.setText(device.getDeviceType() != null ? 
                device.getDeviceType() : "未知");
        
        // 设置设备状态
        boolean isActive = device.getIsActive() != null && device.getIsActive();
        holder.deviceStatus.setText(isActive ? "活跃" : "已停用");
        holder.deviceStatus.setTextColor(isActive ? 
                context.getResources().getColor(android.R.color.holo_green_dark) :
                context.getResources().getColor(android.R.color.holo_red_dark));
        
        // 设置最后活跃时间
        if (device.getLastActiveAt() != null) {
            holder.lastActive.setText("最后活跃: " + device.getLastActiveAt());
        } else {
            holder.lastActive.setText("最后活跃: 未知");
        }
        
        return convertView;
    }
    
    /**
     * 更新设备列表
     */
    public void updateDeviceList(List<DeviceInfo> newDeviceList) {
        this.deviceList.clear();
        this.deviceList.addAll(newDeviceList);
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder类
     */
    private static class ViewHolder {
        ImageView deviceIcon;
        TextView deviceName;
        TextView deviceType;
        TextView deviceStatus;
        TextView lastActive;
    }
}
