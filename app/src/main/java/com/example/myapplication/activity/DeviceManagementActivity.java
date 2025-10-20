package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.adapter.DeviceListAdapter;
import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.dto.ApiResponse;
import com.example.myapplication.dto.DeviceInfo;
import com.example.myapplication.network.NetworkConfig;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 设备管理界面
 * 显示用户的所有注册设备，支持停用设备
 */
public class DeviceManagementActivity extends AppCompatActivity {
    
    private static final String TAG = "DeviceManagement";
    
    private ListView deviceListView;
    private TextView deviceCountText;
    private Button refreshButton;
    private Button testPushButton;
    private DeviceListAdapter deviceAdapter;
    private List<DeviceInfo> deviceList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);
        
        initViews();
        setupListeners();
        loadDeviceList();
    }
    
    private void initViews() {
        deviceListView = findViewById(R.id.device_list_view);
        deviceCountText = findViewById(R.id.device_count_text);
        refreshButton = findViewById(R.id.refresh_button);
        testPushButton = findViewById(R.id.test_push_button);
        
        deviceAdapter = new DeviceListAdapter(this, deviceList);
        deviceListView.setAdapter(deviceAdapter);
    }
    
    private void setupListeners() {
        refreshButton.setOnClickListener(v -> loadDeviceList());
        
        testPushButton.setOnClickListener(v -> sendTestPush());
        
        // 设置设备列表点击监听器
        deviceListView.setOnItemClickListener((parent, view, position, id) -> {
            DeviceInfo device = deviceList.get(position);
            showDeviceOptions(device);
        });
    }
    
    /**
     * 加载设备列表
     */
    private void loadDeviceList() {
        Log.d(TAG, "开始加载设备列表");
        
        AuthManager authManager = AuthManager.getInstance(this);
        String token = authManager.getToken();
        
        if (token == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        refreshButton.setEnabled(false);
        refreshButton.setText("加载中...");
        
        NetworkConfig.getApiService().getDeviceList("Bearer " + token)
                .enqueue(new Callback<ApiResponse<List<DeviceInfo>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<DeviceInfo>>> call, 
                                         Response<ApiResponse<List<DeviceInfo>>> response) {
                        refreshButton.setEnabled(true);
                        refreshButton.setText("刷新");
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<DeviceInfo>> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                deviceList.clear();
                                deviceList.addAll(apiResponse.getData());
                                deviceAdapter.notifyDataSetChanged();
                                updateDeviceCount();
                                loadDeviceStats(); // 同时加载统计信息
                                Log.i(TAG, "设备列表加载成功，共 " + deviceList.size() + " 个设备");
                            } else {
                                Toast.makeText(DeviceManagementActivity.this, 
                                        "加载失败: " + apiResponse.getMessage(), 
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DeviceManagementActivity.this, 
                                    "网络请求失败: " + response.code(), 
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<List<DeviceInfo>>> call, Throwable t) {
                        refreshButton.setEnabled(true);
                        refreshButton.setText("刷新");
                        Toast.makeText(DeviceManagementActivity.this, 
                                "网络错误: " + t.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "加载设备列表失败", t);
                    }
                });
    }
    
    /**
     * 更新设备数量显示
     */
    private void updateDeviceCount() {
        int totalDevices = deviceList.size();
        long activeDevices = deviceList.stream()
                .filter(device -> device.getIsActive() != null && device.getIsActive())
                .count();
        long androidDevices = deviceList.stream()
                .filter(device -> "ANDROID".equals(device.getDeviceType()))
                .count();
        long iosDevices = deviceList.stream()
                .filter(device -> "IOS".equals(device.getDeviceType()))
                .count();
        
        deviceCountText.setText(String.format("总设备: %d | 活跃: %d | Android: %d | iOS: %d", 
                totalDevices, activeDevices, androidDevices, iosDevices));
    }
    
    /**
     * 加载设备统计信息
     */
    private void loadDeviceStats() {
        Log.d(TAG, "开始加载设备统计信息");
        
        AuthManager authManager = AuthManager.getInstance(this);
        String token = authManager.getToken();
        
        if (token == null) {
            return;
        }
        
        NetworkConfig.getApiService().getDeviceStats("Bearer " + token)
                .enqueue(new Callback<ApiResponse<java.util.Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<java.util.Map<String, Object>>> call, 
                                         Response<ApiResponse<java.util.Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<java.util.Map<String, Object>> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                updateDeviceStatsDisplay(apiResponse.getData());
                                Log.i(TAG, "设备统计信息加载成功");
                            } else {
                                Log.w(TAG, "设备统计信息加载失败: " + apiResponse.getMessage());
                            }
                        } else {
                            Log.w(TAG, "设备统计信息请求失败: " + response.code());
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<java.util.Map<String, Object>>> call, Throwable t) {
                        Log.e(TAG, "加载设备统计信息失败", t);
                    }
                });
    }
    
    /**
     * 更新设备统计显示
     */
    private void updateDeviceStatsDisplay(java.util.Map<String, Object> stats) {
        try {
            int totalDevices = ((Number) stats.get("totalDevices")).intValue();
            int activeDevices = ((Number) stats.get("activeDevices")).intValue();
            int androidDevices = ((Number) stats.get("androidDevices")).intValue();
            int iosDevices = ((Number) stats.get("iosDevices")).intValue();
            
            deviceCountText.setText(String.format("总设备: %d | 活跃: %d | Android: %d | iOS: %d", 
                    totalDevices, activeDevices, androidDevices, iosDevices));
            
            Log.i(TAG, "设备统计更新 - 总设备: " + totalDevices + ", 活跃: " + activeDevices + 
                    ", Android: " + androidDevices + ", iOS: " + iosDevices);
        } catch (Exception e) {
            Log.e(TAG, "更新设备统计显示失败", e);
        }
    }
    
    /**
     * 显示设备选项
     */
    private void showDeviceOptions(DeviceInfo device) {
        String[] options = {"停用设备", "查看详情"};
        
        androidx.appcompat.app.AlertDialog.Builder builder = 
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(device.getDeviceName())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // 停用设备
                            deactivateDevice(device);
                            break;
                        case 1: // 查看详情
                            showDeviceDetails(device);
                            break;
                    }
                })
                .show();
    }
    
    /**
     * 停用设备
     */
    private void deactivateDevice(DeviceInfo device) {
        androidx.appcompat.app.AlertDialog.Builder builder = 
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("确认停用")
                .setMessage("确定要停用设备 \"" + device.getDeviceName() + "\" 吗？\n" +
                           "停用后该设备将不再接收推送通知。")
                .setPositiveButton("停用", (dialog, which) -> {
                    performDeactivateDevice(device);
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    /**
     * 执行停用设备
     */
    private void performDeactivateDevice(DeviceInfo device) {
        Log.d(TAG, "开始停用设备: " + device.getRegistrationId());
        
        AuthManager authManager = AuthManager.getInstance(this);
        String token = authManager.getToken();
        
        NetworkConfig.getApiService().deactivateDevice("Bearer " + token, 
                device.getRegistrationId())
                .enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<String>> call, 
                                         Response<ApiResponse<String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<String> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                Toast.makeText(DeviceManagementActivity.this, 
                                        "设备已停用", Toast.LENGTH_SHORT).show();
                                loadDeviceList(); // 重新加载列表
                            } else {
                                Toast.makeText(DeviceManagementActivity.this, 
                                        "停用失败: " + apiResponse.getMessage(), 
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DeviceManagementActivity.this, 
                                    "停用失败: " + response.code(), 
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                        Toast.makeText(DeviceManagementActivity.this, 
                                "停用失败: " + t.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "停用设备失败", t);
                    }
                });
    }
    
    /**
     * 显示设备详情
     */
    private void showDeviceDetails(DeviceInfo device) {
        String details = String.format(
                "设备名称: %s\n" +
                "设备类型: %s\n" +
                "应用版本: %s\n" +
                "系统版本: %s\n" +
                "注册时间: %s\n" +
                "最后活跃: %s\n" +
                "状态: %s\n" +
                "Registration ID: %s",
                device.getDeviceName(),
                device.getDeviceType(),
                device.getAppVersion() != null ? device.getAppVersion() : "未知",
                device.getOsVersion() != null ? device.getOsVersion() : "未知",
                device.getCreatedAt() != null ? device.getCreatedAt() : "未知",
                device.getLastActiveAt() != null ? device.getLastActiveAt() : "未知",
                (device.getIsActive() != null && device.getIsActive()) ? "活跃" : "已停用",
                device.getRegistrationId()
        );
        
        androidx.appcompat.app.AlertDialog.Builder builder = 
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("设备详情")
                .setMessage(details)
                .setPositiveButton("确定", null)
                .show();
    }
    
    /**
     * 发送测试推送
     */
    private void sendTestPush() {
        Log.d(TAG, "发送测试推送");
        
        // 这里可以调用后端的测试推送API
        // 由于后端没有专门的测试推送接口，这里只是显示提示
        Toast.makeText(this, "测试推送功能需要后端支持", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 启动设备管理界面
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, DeviceManagementActivity.class);
        context.startActivity(intent);
    }
}
