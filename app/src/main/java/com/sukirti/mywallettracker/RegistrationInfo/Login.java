package com.sukirti.mywallettracker.RegistrationInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sukirti.mywallettracker.Database.SQLiteHelper;
import com.sukirti.mywallettracker.MainActivity;
import com.sukirti.mywallettracker.R;

import java.util.logging.Logger;

import androidx.annotation.NonNull;

public class Login extends Activity implements View.OnClickListener {

    Button login;
    EditText userName;
    EditText password;
    Logger logger;
    TextView loginFailText,registerLink,forgotPwd;
    Intent intent;
    private final int requestCodeRegistration = 0;
    SQLiteHelper sqLiteHelper;
    private FirebaseAuth firebaseAuth;
    private String TAG ="Login";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        logger = Logger.getLogger(Login.class.getName());
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        login  = (Button) findViewById(R.id.login);
        loginFailText = (TextView) findViewById(R.id.loginFailResult);
        registerLink = (TextView) findViewById(R.id.registerLink);
        forgotPwd = (TextView) findViewById(R.id.forgotPwd);
        sqLiteHelper = new SQLiteHelper(this);
        login.setOnClickListener(this);
        registerLink.setOnClickListener(this);
        forgotPwd.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                System.out.println("currentUser is "+currentUser);
                checkUserValidation(firebaseAuth);
                break;
            case R.id.registerLink:
                //TODO need to make this activity a fragment
               // startActivity(new Intent(this,Registration.class));
                intent = new Intent(this,Registration.class);
                startActivityForResult(intent,requestCodeRegistration);

                break;

            case R.id.forgotPwd:
                resetPassword();
                break;

                default:
                System.out.println("Default");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == requestCodeRegistration){
            if(data!=null) {
                String userName = data.getStringExtra("userName");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Your UserName is " + userName)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    public void checkUserValidation(FirebaseAuth firebaseAuth){
        final String email = userName.getText().toString();
        final String passwordStr = password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email address", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(passwordStr)) {
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, passwordStr)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (passwordStr.length() < 6) {
                                loginFailText.setText(R.string.string_incorrectPassword);
                            } else {
                                Toast.makeText(Login.this, "check username or password", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }


    public void resetPassword(){

        String result;

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Please your email id");
        final EditText input = new EditText(this);
        b.setView(input);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int whichButton)
            {
                final ProgressDialog dialog1 = ProgressDialog.show(Login.this, "",
                        "Sending Email. Please wait...", true);
                System.out.println(input.getText().toString());
                String userEmail = input.getText().toString();

                FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this,"Email sent, Please check your mailbox", Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "Email sent.");
                                }else
                                    Toast.makeText(Login.this,"Sending email failed, try again!!!!", Toast.LENGTH_LONG).show();

                                dialog1.dismiss();
                            }
                        });
            }
        });
        b.setNegativeButton("CANCEL", null);
        b.create().show();

    }


}
