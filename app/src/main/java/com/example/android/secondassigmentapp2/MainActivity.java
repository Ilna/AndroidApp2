package com.example.android.secondassigmentapp2;

import android.Manifest;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private MyBroadcastReceiver receiver;
    private IntentFilter filter;
    LocationManager locationManager;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==7) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListenig();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiver = new MyBroadcastReceiver();
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(receiver,filter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListenig();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void startListenig(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},7);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 20,
                new LocationListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onLocationChanged(Location location) {
//                        TextView lat = findViewById(R.id.lattextView);
//                        Log.d("LATITUDE", String.valueOf(location.getLatitude()));
//                        TextView lon = findViewById(R.id.lontextView);
//                        lat.setText(location.getLatitude()+"");
//                        lon.setText((location.getLongitude()+""));

                        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy,hh:mm:ss");
                        String currentTimestamp = s.format(new Date());

                        EditText editText = findViewById(R.id.editText);
                        String userid = editText.getText().toString();

                        Double latitude = location.getLatitude();
                        Double longtitude = location.getLongitude();
                        System.out.println(latitude+"\n"+ longtitude);

                        ContentValues values = new ContentValues();
                        values.put("_USERID",userid);
                        values.put("_LONGTITUDE",longtitude);
                        values.put("_LATITUDE",latitude);
                        values.put("timeStamp",currentTimestamp);

                        String AUTHORITY = "com.example.android.firstassignment";
                        String PATH ="/datatable";
                        Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY+PATH);
                        getContentResolver().insert(CONTENT_URI,values);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
    }
}
