package com.sukirti.mywallettracker.RegistrationInfo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sukirti.mywallettracker.Database.SQLiteHelper;
import com.sukirti.mywallettracker.R;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import androidx.annotation.NonNull;

public class Registration extends Activity implements View.OnClickListener {


    EditText firstName,lastName,emilaID,phoneNo,password;
    EditText confirmPassword,dob;
    Button register;
    Logger logger;
    SQLiteHelper sqLiteHelper;
    Map<String,String> registrationDataMap;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        logger = Logger.getLogger(Login.class.getName());
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        emilaID = (EditText) findViewById(R.id.email);
        phoneNo = (EditText) findViewById(R.id.phoneNumber);
        password = (EditText) findViewById(R.id.passwordReg);
        confirmPassword = (EditText) findViewById(R.id.confirmPasswordReg);
        dob = (EditText) findViewById(R.id.dob);
        sqLiteHelper = new SQLiteHelper(this);
        registrationDataMap = new HashMap<>();

        register  = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:

                if(validateRegistrationData()) {

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                    firebaseAuth.createUserWithEmailAndPassword(emilaID.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(Registration.this, "Registration is successful", Toast.LENGTH_LONG).show();
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Registration.this, "Registration is failed", Toast.LENGTH_LONG).show();
                                    } else {
                                        startActivity(new Intent(Registration.this, Login.class));
                                        finish();
                                    }
                                }
                            });
                }
                break;
            default:
                logger.info("Default call inside registration page");
                break;
        }
    }

    public boolean validateRegistrationData(){
        boolean result  = false;
        if(TextUtils.isEmpty(firstName.getText().toString()) || TextUtils.isEmpty(lastName.getText().toString()) || TextUtils.isEmpty(phoneNo.getText().toString())
                || TextUtils.isEmpty(dob.getText().toString())){
            Toast.makeText(this,"Don't leave a filed empty", Toast.LENGTH_LONG).show();
            return result;
        }

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(TextUtils.isEmpty(emilaID.getText().toString())){
            Toast.makeText(this,"Enter a valid email", Toast.LENGTH_LONG).show();
            return result;
        }
        if(!emilaID.getText().toString().trim().matches(emailPattern)){
            Toast.makeText(this,"Enter a valid email ID", Toast.LENGTH_LONG).show();
            return result;
        }

        if(password.getText().toString().length()<6){
            Toast.makeText(this,"Password must be of 6 character in DOB filed", Toast.LENGTH_LONG).show();
            return result;
        }

        if(!password.getText().toString().equals(confirmPassword.getText().toString())){
            Toast.makeText(this,"Confirm password doesn't match with password", Toast.LENGTH_LONG).show();
            return result;
        }
        String regexStr = "^[0-9]*$";
        if(!phoneNo.getText().toString().trim().matches(regexStr)){
            Toast.makeText(this,"Please enter only digits", Toast.LENGTH_LONG).show();
            return result;
        }

        String regexStr2 = "^[0-9\\-]*$";
        if(!dob.getText().toString().trim().matches(regexStr2)){
            Toast.makeText(this,"Please enter only digits and - ,other chrecters are not allowed", Toast.LENGTH_LONG).show();
            return result;
        }

        return true;
    }
}
