package com.vonage.vbs.wifiexample;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private WifiManager wifiManager;
    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private WifiScanReceiver wifiReceiver;

    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private static final String LOCATION_PERMISSION_DENIED_TEXT = "Permission denied to use location services";
    private static final String WIFI_DISABLED_PROMPT_TEXT = "WiFi is disabled, making it enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestPermissions();

        listView = (ListView) findViewById(R.id.listView);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        //if the wifi is turned off we turn it on
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), WIFI_DISABLED_PROMPT_TEXT, Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        //create and register the receiver
        wifiReceiver = new WifiScanReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();


    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    //We request the user to give location permissions for
    //marshmallow OS and above
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST);

    }

    //TODO: change cases to variables
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, LOCATION_PERMISSION_DENIED_TEXT, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {

            List<ScanResult> wifiList = wifiManager.getScanResults();
            ArrayList<String> wifiInfo = new ArrayList<>();

            for(ScanResult scanResult : wifiList){//Get only the info we need
                wifiInfo.add("Wifi Name: "+scanResult.SSID+System.getProperty("line.separator")+
                "Mac: "+scanResult.BSSID+ System.getProperty("line.separator")+
                "Strength: "+scanResult.level);
            }


            //create the list view adapter
            arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.wifi_list_item_card_view, R.id.wifi_Name,wifiInfo);
            listView.setAdapter(arrayAdapter);
        }
    }
}
