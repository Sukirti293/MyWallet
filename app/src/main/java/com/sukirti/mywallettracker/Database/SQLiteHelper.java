package com.sukirti.mywallettracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sukirti.mywallettracker.R;

import java.util.Map;
import java.util.logging.Logger;

public class SQLiteHelper extends SQLiteOpenHelper {

    SQLSyntax sqlSyntax;
    Context context;
    Logger logger;
    SQLiteDatabase db;


    public SQLiteHelper(Context context) {
        super(context, context.getString(R.string.DATABASE_NAME), null, 1);

        this.context = context;
        logger = Logger.getLogger(SQLiteHelper.class.getName());
        sqlSyntax =  new SQLSyntax(context);
        SQLiteDatabase db = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            logger.info("Inside On Create Method");
            logger.info("sqlSyntax value "+sqlSyntax);
            db.execSQL(sqlSyntax.createTableQuery());
            this.db= db;
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        logger.info("Inside onUpgrade Method");
        db.execSQL(sqlSyntax.dateleTableQuery());
        onCreate(db);
    }


    public boolean insertData(Map<String,String> registrationData){
        logger.info("Inside insertData Method");
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(context.getString(R.string.EMAIL_ID),registrationData.get(context.getString(R.string.EMAIL_ID)));
        contentValues.put(context.getString(R.string.FIRST_NAME),registrationData.get(context.getString(R.string.FIRST_NAME)));
        contentValues.put(context.getString(R.string.LAST_NAME),registrationData.get(context.getString(R.string.LAST_NAME)));
        contentValues.put(context.getString(R.string.PHONE_NO),registrationData.get(context.getString(R.string.PHONE_NO)));
        contentValues.put(context.getString(R.string.PASSWORD),registrationData.get(context.getString(R.string.PASSWORD)));
        contentValues.put(context.getString(R.string.CONFIRM_PASSWORD),registrationData.get(context.getString(R.string.CONFIRM_PASSWORD)));
        contentValues.put(context.getString(R.string.DOB),registrationData.get(context.getString(R.string.DOB)));

        String phoneNo = registrationData.get(context.getString(R.string.PHONE_NO));
        String UserName = registrationData.get(context.getString(R.string.FIRST_NAME))+phoneNo.substring(phoneNo.length()-4);
        contentValues.put(context.getString(R.string.USERNAME),UserName);
        long output = db.insert(context.getString(R.string.TABLE_NAME),null,contentValues);
        return output!=-1;
    }

    public String getUserName(String emailID){
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSyntax.selectUserName(emailID),null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            logger.info("username is "+cursor.getString(0));
            return cursor.getString(0);
        }else{
            return null;
        }
    }

    public String getPassword(String userName){
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSyntax.selectUserNamePassword(userName),null);
        String uName,pwd=null;
        boolean flag= false;

        if(cursor.moveToFirst()){
            do{
                uName = cursor.getString(0);
                if(uName.equals(userName)){
                    pwd=cursor.getString(1);
                    flag =true;
                    break;
                }
            }
            //Sukirti9019
            //Sukirti293
            while (cursor.moveToNext());

//            if(flag){
//              logger.info("Both userName and Password is valid");
//              return pwd;
//            }else{
//
//            }
            logger.info("Both userName and Password is valid");
            return pwd;
        }else{
            return null;
        }
    }

    public boolean createAndStoreExpenses(int expense, String userName){
        db = this.getWritableDatabase();
        try {
            logger.info("Inside On createAndStoreExpenses Method");
            logger.info("sqlSyntax value "+sqlSyntax);
            db.execSQL(sqlSyntax.createExpenseTable());
            logger.info("Expense table created");
            ContentValues contentValues = new ContentValues();
            contentValues.put(context.getString(R.string.TOTALAMOUNT),expense);
            contentValues.put(context.getString(R.string.SPENDINGS),0);
            contentValues.put(context.getString(R.string.REMAINED),expense);
            contentValues.put(context.getString(R.string.USERNAME),userName);
            long output = db.insert(context.getString(R.string.EXPENSE_TABLE),null,contentValues);
            return output!=-1;

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        return false;
    }

    public String checkBudgetInDatabase(String userName){
        boolean flag = false;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSyntax.selectBudget(userName),null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            logger.info("username is "+cursor.getString(0));
            return cursor.getString(0);
        }else{
            return null;
        }

    }



}
