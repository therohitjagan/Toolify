package com.therohitjagan.toolify.alltools.systemtools;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.therohitjagan.toolify.R;

import java.util.List;

public class SystemInfoActivity extends AppCompatActivity {

    private MaterialTextView ramInfo, storageInfo, batteryInfo, cpuInfo, networkInfo, deviceInfo, sensorInfo, appUsageInfo;
    private MaterialCardView exportCard;
    private static final int REQUEST_CODE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_info);

        // Initialize UI elements
        ramInfo = findViewById(R.id.ramInfo);
        storageInfo = findViewById(R.id.storageInfo);
        batteryInfo = findViewById(R.id.batteryInfo);
        cpuInfo = findViewById(R.id.cpuInfo);
        networkInfo = findViewById(R.id.networkInfo);
        deviceInfo = findViewById(R.id.deviceInfo);
        sensorInfo = findViewById(R.id.sensorInfo);
        appUsageInfo = findViewById(R.id.appUsageInfo);
        exportCard = findViewById(R.id.exportCard);

        // Request necessary permissions
        requestPermissions();

        // Load system information
        loadSystemInfo();
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.READ_PHONE_STATE,
                            android.Manifest.permission.ACCESS_NETWORK_STATE
                    }, REQUEST_CODE_PERMISSION);
        }

        // Special permission for App Usage Stats
        if (!hasUsageStatsPermission()) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            Toast.makeText(this, "Enable Usage Access for App Usage Info", Toast.LENGTH_LONG).show();
        }
    }

    private boolean hasUsageStatsPermission() {
        try {
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long currentTime = System.currentTimeMillis();
            long oneDayAgo = currentTime - (1000 * 3600 * 24);
            List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, oneDayAgo, currentTime);
            return stats != null && !stats.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void requestUsageStatsPermission() {
        if (!hasUsageStatsPermission()) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            Toast.makeText(this, "Enable Usage Access for App Usage Info", Toast.LENGTH_LONG).show();
        }
    }

    private void loadSystemInfo() {
        showRamInfo();
        showStorageInfo();
        showBatteryInfo();
        showCpuInfo();
        showNetworkInfo();
        showDeviceInfo();
        showSensorInfo();
        showAppUsageStats();
    }

    private void showRamInfo() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(memoryInfo);
        ramInfo.setText("Total RAM: " + formatSize(memoryInfo.totalMem) + "\nAvailable RAM: " + formatSize(memoryInfo.availMem));
    }

    private void showStorageInfo() {
        StatFs statFs = new StatFs(getFilesDir().getAbsolutePath());
        long totalStorage = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
        long availableStorage = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
        storageInfo.setText("Total Storage: " + formatSize(totalStorage) + "\nAvailable Storage: " + formatSize(availableStorage));
    }

    private void showBatteryInfo() {
        Intent batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level * 100 / (float) scale;
        batteryInfo.setText("Battery Level: " + batteryPct + "%");
    }

    private void showCpuInfo() {
        String cpuInfoText = "CPU Cores: " + Runtime.getRuntime().availableProcessors();
        cpuInfo.setText(cpuInfoText);
    }

    private void showNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            networkInfo.setText("Network Type: " + activeNetwork.getTypeName());
        } else {
            networkInfo.setText("No Network Connected");
        }
    }

    private void showDeviceInfo() {
        deviceInfo.setText("Device Model: " + Build.MODEL + "\nManufacturer: " + Build.MANUFACTURER + "\nAndroid Version: " + Build.VERSION.RELEASE);
    }

    private void showSensorInfo() {
        List<android.hardware.Sensor> sensorList = ((android.hardware.SensorManager) getSystemService(SENSOR_SERVICE)).getSensorList(android.hardware.Sensor.TYPE_ALL);
        StringBuilder sensorDetails = new StringBuilder();

        for (android.hardware.Sensor sensor : sensorList) {
            sensorDetails.append(sensor.getName()).append("\n");
        }
        sensorInfo.setText(sensorDetails.toString());
    }

    private void showAppUsageStats() {
        if (!hasUsageStatsPermission()) {
            appUsageInfo.setText("App Usage permission not granted!");
            return;
        }

        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();
        long oneDayAgo = currentTime - (1000 * 3600 * 24);

        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, oneDayAgo, currentTime);
        if (stats != null && !stats.isEmpty()) {
            StringBuilder appStats = new StringBuilder();
            for (UsageStats usage : stats) {
                appStats.append("App: ").append(usage.getPackageName()).append("\nLast Used: ").append(usage.getLastTimeUsed()).append("\n");
            }
            appUsageInfo.setText(appStats.toString());
        }
    }

    private String formatSize(long size) {
        return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
    }
}
