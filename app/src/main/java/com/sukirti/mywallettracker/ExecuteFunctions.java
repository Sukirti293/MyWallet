package com.sukirti.mywallettracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.sukirti.mywallettracker.RegistrationInfo.User;
import com.sukirti.mywallettracker.Spendings.UserSpendings;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;

public class ExecuteFunctions {

    private Context context =null;
    private UserSpendings userCurrSpendings;
    private String TAG = "ExecuteFunctions";
    List<User> userList;
    private boolean downloadFlag = false;
    private List<User> userSpendingsList;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public ExecuteFunctions(Context context){
        this.context = context;
        userList = new ArrayList<>();
        userSpendingsList =  new ArrayList<>();
        pref = context.getSharedPreferences("sharedPref", 0);
        editor = pref.edit();
    }


    public void addBudgetForUser(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Please enter your monthly budget");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        alert.setView(input);
        //alert.setCancelable(false);
        alert.setPositiveButton("OK", null);

        final AlertDialog mAlertDialog = alert.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (!input.getText().toString().equals("")) {
                            int budget = Integer.parseInt(input.getText().toString());
                            if (budget <= 0) {
                                Toast.makeText(context, "Please Enter a valid budget", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    checkExistingBudget(budget,"budget");
                                    editor.putInt("Total", budget);
                                    editor.apply();
                                    editor.commit();

                                }catch (Exception e){
                                    Toast.makeText(context, "Couldn't able to add budget for this user", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                                //setPieChartValues();
                                mAlertDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(context, "Please Enter a valid budget", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        mAlertDialog.show();
    }


    public void addSpendingsForUser(){
        userCurrSpendings =  new UserSpendings();
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_spending_category);
        dialog.setTitle("Title...");
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText("Choose a category from below dropdown to add your spendings");
        final EditText editText = (EditText) dialog.findViewById(R.id.amount);
        editText.setVisibility(View.INVISIBLE);
        Button done = (Button) dialog.findViewById(R.id.button);
        Button add = (Button) dialog.findViewById(R.id.add);



        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
                System.out.println(id);
                if(position==0){
                    editText.setVisibility(View.INVISIBLE);
                }else{
                    editText.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setRawInputType(Configuration.KEYBOARD_12KEY);
                    //editText.addTextChangedListener(new addListenerOnTextChange(context,editText,spinner));


                }
                //editText.setVisibility(View.VISIBLE);
                //dialog.dismiss();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //dialog.dismiss();

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value  =  Integer.parseInt(editText.getText().toString());
                String category  = spinner.getSelectedItem().toString().toLowerCase();
                switch (category){
                    case "clothes":
                        userCurrSpendings.setClothes(value);
                        break;
                    case "food":
                        userCurrSpendings.setFood(value);
                        break;
                    case "entertainment":
                        userCurrSpendings.setEntertainment(value);
                        break;
                    case "education":
                        userCurrSpendings.setEducation(value);
                        break;
                    case "necessities":
                        userCurrSpendings.setNecessities(value);
                        break;
                    case "extras":
                        userCurrSpendings.setExtras(value);
                        break;
                }
                editText.getText().clear();
                spinner.setSelection(0);

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    checkExistingBudget(0,"spendings");

//                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
//                    //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//                    databaseReference.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            boolean val =false;
//                            String key =null;
//                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//
//                            boolean flag = false;
//
//
//                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
//                                User user = snapshot.getValue(User.class);
//                                if(user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
//                                    key = user.getUserID();
//
//
//                                    Map<String, Object> messageValues = userCurrSpendings.toMap();
//                                    Map<String, Object> childUpdates = new HashMap<>();
//
//                                    childUpdates.put(key+"/userSpendings/" + getDate() , messageValues);
//                                    databaseReference.updateChildren(childUpdates);
//                                    flag = true;
//                                    break;
//
//                                }
//                            }
//
//
//                            if(flag)
//                                Toast.makeText(context, "All spendings have been added successfully", Toast.LENGTH_LONG).show();
//                            else
//                                Toast.makeText(context, "Couldn't able to add spendings for this user", Toast.LENGTH_LONG).show();
//
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });


                }catch (Exception e){
                    Toast.makeText(context, "Couldn't able to add spendings for this user", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }



    public void checkExistingBudget(final int budget, String function){



        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean val =false;
                String key =null;

                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                boolean flag = false;


                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    userList.add(user);


                    if(function.equalsIgnoreCase("spendings")) {
                        if (user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                            key = user.getUserID();


                            Map<String, Object> messageValues = userCurrSpendings.toMap();
                            Map<String, Object> childUpdates = new HashMap<>();

                            childUpdates.put(key + "/userSpendings/" + getDate(), messageValues);
                            database.updateChildren(childUpdates);
                            flag = true;
                            break;

                        }
                    }

                }

                for(User user: userList){
                    if(user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        key = user.getUserID();
                        val =true;
                        break;

                    }
                }

                if(function.equalsIgnoreCase("spendings")) {
                    if (flag) {
                        MainActivity.pieChart.invalidate();
                        Toast.makeText(context, "Added Sucessfully", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(context, "Couldn't able to add spendings for this user, Please set the monthly budget first", Toast.LENGTH_LONG).show();
                    }
                }else {

                    if (val) {
                        //System.out.println(dataSnapshot.getChildrenCount());
                        database.child(key).child("monthlyBudget").setValue(budget);
                        Toast.makeText(context, "Added Sucessfully", Toast.LENGTH_LONG).show();
                    } else {

                        String id = database.push().getKey();
                        User user = new User(id, FirebaseAuth.getInstance().getCurrentUser().getEmail().toString(), budget, new HashMap<String, Object>());
                        database.child(id).setValue(user);
                        Toast.makeText(context, "Monthly Budget added successfully", Toast.LENGTH_LONG).show();
                        userList.add(user);
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    public void createExcelsheet(){


        String[] colors = {"Download data", "Upload Data"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a function");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int itemPosition) {
                if(itemPosition ==0){

                    DatabaseAsync databaseAsync =  new DatabaseAsync("downloadExcel");
                    databaseAsync.execute();

                }else{
                    Toast.makeText(context,"Item Selected "+itemPosition, Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.show();


        //return flag;
    }


    public void shareExcelViaMail(){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("xlsx");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"siva.teja.st@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Trial");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "PFA");
        File root = Environment.getExternalStorageDirectory();
        String pathToMyAttachedFile = "Wallet/Spendings.xlsx";
        File file = new File(root, pathToMyAttachedFile);
        if (!file.exists() || !file.canRead()) {
            return;
        }
        Uri uri = Uri.fromFile(file);
        //Uri uri1 = FileProvider.getUriForFile(context,)
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }


    public String getDate(){
        try {
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            //Date date = dateFormat.parse("1/11/2019");
            return dateFormat.format(date);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public boolean getDataForExcelSheet(final String function){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    User userSpendings = noteDataSnapshot.getValue(User.class);
                    userSpendingsList.add(userSpendings);
                    downloadFlag = true;
                }
//                if(function.equals("loadSpendings")){
//                    new MainActivity().setPieChartValues();
//
//                }else{
//                    createExcelsheet();
//                }

                createExcelsheet();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return downloadFlag;

    }

    class DatabaseAsync extends AsyncTask<Void,Void,Void> {
        ProgressDialog dialog1 =null;
        String function =null;

        public DatabaseAsync(String function){
            this.function =function;
        }


        @Override
        protected void onPreExecute(){

            if(function.equals("downloadExcel")) {

                dialog1 = ProgressDialog.show(context, "",
                        "Downloading. Please wait...", true);
            }else if(function.equals("loadSpending")) {

                dialog1 = ProgressDialog.show(context, "",
                        "Loading Spendings. Please wait...", true);
                Log.d(TAG, "Loading Spendings. Please wait");
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Log.d(TAG, "send to databse");
          //  downloadFlag = getDataForExcelSheet();
            Log.d(TAG, "sent to database - DONE");

            if(function.equals("downloadExcel")) {

                if (downloadFlag) {
                    Map<String, Object> spendingsMap = new HashMap<>();
                    for (User user : userSpendingsList) {
                        if(user.getEmail().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString())) {
                            spendingsMap = user.getUserSpendings();
                        }
                    }

                    int count = 1;
                    Workbook workbook = new XSSFWorkbook();
                    Sheet sheet = workbook.createSheet("Spendings");

                    Row row = sheet.createRow(0);
                    row.createCell(0).setCellValue("Date");
                    row.createCell(1).setCellValue("Clothes");
                    row.createCell(2).setCellValue("Food");
                    row.createCell(3).setCellValue("Entertainment");
                    row.createCell(4).setCellValue("Education");
                    row.createCell(5).setCellValue("Necessities");
                    row.createCell(6).setCellValue("Extras");

                    for (String st : spendingsMap.keySet()) {

                        Gson gson = new Gson();
                        UserSpendings userSpendings = gson.fromJson(spendingsMap.get(st).toString(), UserSpendings.class);
                        System.out.println(userSpendings.toString());

                        row = sheet.createRow(count);

                        row.createCell(0).setCellValue(st);
                        row.createCell(1).setCellValue(userSpendings.getClothes());
                        row.createCell(2).setCellValue(userSpendings.getFood());
                        row.createCell(3).setCellValue(userSpendings.getEntertainment());
                        row.createCell(4).setCellValue(userSpendings.getEducation());
                        row.createCell(5).setCellValue(userSpendings.getNecessities());
                        row.createCell(6).setCellValue(userSpendings.getExtras());


                        count++;

                    }

                    String fileName = "Spendings.xlsx";
                    //String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                    String extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                    File folder = new File(extStorageDirectory, "Wallet");
                    folder.mkdir();
                    File file = new File(folder, fileName);
                    try {
                        file.createNewFile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    try {
                        FileOutputStream fileOut = new FileOutputStream(file);
                        workbook.write(fileOut);
                        fileOut.close();
                        //Toast.makeText(context,"File downloaded successfully check the download folder!!",Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else if(function.equalsIgnoreCase("loadSpending")){
                //if (downloadFlag) {
                    for(User user:userSpendingsList){
                        if(user.getEmail().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString())) {
                            Map<String, Object> userSpendingsMap = user.getUserSpendings();
                            int noOfdays = 0;
                            for (String str : userSpendingsMap.keySet()) {
                                Gson gson = new Gson();
                                UserSpendings userSpendings = gson.fromJson(userSpendingsMap.get(str).toString(), UserSpendings.class);
                                System.out.println(userSpendings.toString());
                                editor.putInt("Total", user.getMonthlyBudget());
                                if (userSpendings != null) {

                                    int total = userSpendings.totalSpendings();
                                    editor.putInt("Spendings", total);
                                    editor.apply();
                                } else {
                                    int total = -1;
                                    editor.putInt("Spendings", total);
                                    editor.putInt("Spendings", total);
                                    editor.apply();
                                }

                                noOfdays++;
                            }

                            editor.putInt("NoOfDays", noOfdays);
                            editor.apply();
                            editor.commit();
                        }
                    }
                //}
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "p execute");
            editor.commit();
            dialog1.dismiss();
            if(function.equals("downloadExcel")) {
                System.out.println(downloadFlag);
                if (downloadFlag)
                    Toast.makeText(context, "File downloaded successfully check the download folder!!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, "File download failed, try again!!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "done executing");
            }
        }

    }

    public List<User> getList(){
        return userSpendingsList;
    }

}
