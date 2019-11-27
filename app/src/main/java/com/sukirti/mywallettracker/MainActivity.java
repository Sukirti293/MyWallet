package com.sukirti.mywallettracker;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sukirti.mywallettracker.PushNotification.FirebaseMsgService;
import com.sukirti.mywallettracker.RegistrationInfo.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static PieChart pieChart;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private ExecuteFunctions executeFunctions;
    private String TAG= "MainActivity";
    FirebaseMsgService firebaseMsgService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeactivity);
        firebaseAuth =  FirebaseAuth.getInstance();
        executeFunctions = new ExecuteFunctions(MainActivity.this);
        firebaseMsgService = new FirebaseMsgService(MainActivity.this);
        pieChart = findViewById(R.id.piChart);
        pieChart.invalidate();
        pieChart.setUsePercentValues(true);

        drawerLayout = (DrawerLayout)findViewById(R.id.activity_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView)findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        getDataToDisplayInPieChart();
        pieChart.invalidate();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                executeFunctions.getDataForExcelSheet("");
                //executeFunctions.createExcelsheet();
            }
        });


        pushNotificationFirebase();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.budget:
                executeFunctions.addBudgetForUser();
                pieChart.invalidate();
                break;
            case R.id.spendings:
                executeFunctions.addSpendingsForUser();
                pieChart.invalidate();
                break;
            case R.id.signOut:
                Toast.makeText(MainActivity.this, "Signing out", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
            case R.id.prediction:
                predictFutureExpenses();
                break;
            default:
                return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void setPieChartValues(){
        pieChart.invalidate();

        ExecuteFunctions.DatabaseAsync databaseAsync =  executeFunctions.new DatabaseAsync("loadSpending");
        databaseAsync.execute();
        List<PieEntry> pieEntries = new ArrayList<>();
        SharedPreferences sharedpreferences = getSharedPreferences("sharedPref",
                Context.MODE_PRIVATE);
        int totalAmountSpent = sharedpreferences.getInt("Spendings",0);
        int totalAmt = sharedpreferences.getInt("Total",0);
        if(totalAmountSpent<=0 || totalAmt==0){
            totalAmountSpent =0;
            if(totalAmt==0){
                totalAmt =100;
                Toast.makeText(MainActivity.this, "Please add your Budget!!!", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(MainActivity.this, "Please add your spendings!!!", Toast.LENGTH_LONG).show();
            }
        }

        pieEntries.add(new PieEntry(totalAmt,"Total Expenses"));
        pieEntries.add(new PieEntry(totalAmountSpent,"Spendings"));
        pieEntries.add(new PieEntry(totalAmt-totalAmountSpent,"Remaining"));
        PieDataSet pieDataSet = new PieDataSet(pieEntries,"category");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieChart.invalidate();

    }

    public void getDataToDisplayInPieChart(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    User userSpendings = noteDataSnapshot.getValue(User.class);
                    executeFunctions.getList().add(userSpendings);
                }
                setPieChartValues();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void pushNotificationFirebase(){

        final SharedPreferences sharedpreferences = getSharedPreferences("sharedPref",
                Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            System.out.println("getInstanceId failed "+task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        String msg = token;
                        System.out.println("TOKEN IS : "+msg);
                        Log.d(TAG, msg);
                        editor.putString("Token", msg);
                        editor.apply();
                        editor.commit();
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


        //TOKEN :    dtGvDayUFGw:APA91bEfqVkoV3U4yX-pP_bxSosGHT6IavRDiTSUSEviFH1jeEfBex5aKj13Kxdjc3m716WW_A91Yza6zVb4iVsQag4U1_QHbJ8_xYrFn95AyFuS7hTJJmzmrdTr1v7THGR5a5kkElIB
        //executeFunctions.shareExcelViaMail();
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("MyNotification",
                    "MyNotification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg ="Successful";
                        if (!task.isSuccessful()) {
                            msg = "Not Successful";
                        }
                        //Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void predictFutureExpenses(){
        //Toast.makeText(MainActivity.this, "Future Prediction clicked", Toast.LENGTH_LONG).show();
        SharedPreferences sharedpreferences = getSharedPreferences("sharedPref",
                Context.MODE_PRIVATE);

        int totalAmountSpent = sharedpreferences.getInt("Spendings",0);
        int totalAmt = sharedpreferences.getInt("Total",0);
        int noOfDays = sharedpreferences.getInt("NoOfDays",0);

        if(totalAmountSpent<=0 || totalAmt==0){
            totalAmountSpent =0;
            if(totalAmt==0){
                totalAmt =100;
                Toast.makeText(MainActivity.this, "Please add your Budget!!!", Toast.LENGTH_LONG).show();
                return ;
            }else {
                Toast.makeText(MainActivity.this, "Please add your spendings!!!", Toast.LENGTH_LONG).show();
                return ;
            }
        }

        float expPerDay = (float) totalAmt/30;
        if(totalAmountSpent > expPerDay*noOfDays){
            float overBudget = totalAmountSpent-expPerDay*noOfDays;
            Toast.makeText(MainActivity.this, "You are running over budget by : $"+overBudget, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this, "You are running fine! No over budget spendings", Toast.LENGTH_LONG).show();
        }

    }


}
