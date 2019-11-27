package com.sukirti.mywallettracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.sukirti.mywallettracker.RegistrationInfo.Login;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SplashActivity extends AppCompatActivity {

    private String TAG= "SplashActivity";
    private TextView text;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        boolean permissionFlag = checkAndRequestPermissions();
        if(permissionFlag){
            Toast.makeText(this,"Permission Granted", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"Please grant permissions", Toast.LENGTH_LONG).show();
        }
        text =  (TextView) findViewById(R.id.text);
        text.setText("Checking whether user has logged in !!!");
        new Handler().postDelayed(new Runnable() {
            @Override

            public void run() {
                userLoginStatus();
            }

        }, 5*1000);

    }


    public void userLoginStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Toast.makeText(this,"User session has already logged in !", Toast.LENGTH_LONG).show();
            Log.d(TAG, "User session has already logged in !");
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this,"User has signed out, Please login again!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "User has signed out, Please login again!");
            Intent i = new Intent(SplashActivity.this, Login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

//    private void appPermission(){
//
//        int permissionCode = 1;
//        String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION};
//
//        if(!hasPermissions(SplashActivity.this, permissionCode)){
//            ActivityCompat.requestPermissions(this, permissions, permissionCode);
//        }
//
////        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
////                != PackageManager.PERMISSION_GRANTED) {
////
////            ActivityCompat.requestPermissions((Activity) this,
////                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
////
////            return;
////        }
////        if(ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
////                != PackageManager.PERMISSION_GRANTED) {
////
////            ActivityCompat.requestPermissions((Activity) this,
////                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
////
////            return;
////        }
////        if(ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
////                != PackageManager.PERMISSION_GRANTED) {
////
////            ActivityCompat.requestPermissions((Activity) this,
////                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
////
////            return;
////        }
//    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private  boolean checkAndRequestPermissions() {
        int permissionStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int locationReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (locationReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),1);
            return false;
        }
        return true;
    }
}
