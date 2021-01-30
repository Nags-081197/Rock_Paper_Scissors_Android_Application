package com.example.rps;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class activity_login extends AppCompatActivity {

    TextInputEditText email,password;
    Button login;
    TextView newusersignup;
    TextView forgotPassword;
    FirebaseAuth fAuth;
    ProgressBar progressbar;
    private Context mContext;
    private Activity mActivity;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 4 characters
                    "$");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.log_email);
        password = findViewById(R.id.log_password);
        login = findViewById(R.id.log_button);
        newusersignup = findViewById(R.id.log_Register);
        forgotPassword = findViewById(R.id.log_forgotpassword);

        fAuth = FirebaseAuth.getInstance();
        progressbar = findViewById(R.id.log_progress);
        mContext = getApplicationContext();
        mActivity = activity_login.this;

        if(fAuth.getCurrentUser()!= null && fAuth.getCurrentUser().isEmailVerified()){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        newusersignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_login.this, activity_register.class));
            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("FORGOT PASSWORD : ");
                passwordResetDialog.setMessage("ENTER EMAIL ID TO RESET YOUR PASSWORD:");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String mail = resetMail.getText().toString().trim();
                        if (TextUtils.isEmpty(mail)) {
                            Toast.makeText(activity_login.this,"Email is Required",Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                            Toast.makeText(activity_login.this,"Please Enter a Valid Email !",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(activity_login.this," Reset Link has been Sent !!",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity_login.this," Error !!  "+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordResetDialog.create().show();

            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateandsubmit();

            }
        });


    }

    public void validateandsubmit(){
        if(!validate()){
            Toast.makeText(activity_login.this, "Enter required field correctly !!", Toast.LENGTH_SHORT).show();
        }
        else{
            String mEmail = email.getText().toString().trim();

            String mPassword = password.getText().toString().trim();

            email.setText("");
//            email.setHint("Email");
//
            password.setText("");
//            password.setHint("Password");


            progressbar.setVisibility(View.VISIBLE);

            fAuth.signInWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    FirebaseUser user = fAuth.getCurrentUser();

                    if(task.isSuccessful()){
                        if(user.isEmailVerified()){
                            progressbar.setVisibility(View.INVISIBLE);
                            Toast.makeText(activity_login.this, "Logged In Successfully !!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            progressbar.setVisibility(View.INVISIBLE);
                            Toast.makeText(activity_login.this, "Email Verification Not Complete !!", Toast.LENGTH_SHORT).show();

                        }
                    }
                    else{
                        progressbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(activity_login.this, "Error :"+task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    public boolean validate(){

        String mEmail = email.getText().toString().trim();

        String mPassword = password.getText().toString().trim();

        boolean valid =  true;

        if (TextUtils.isEmpty(mEmail)) {
            email.setError("Email is required");
            valid =  false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            email.setError("Please enter a valid email address");
            valid =  false;
        }

        if (TextUtils.isEmpty(mPassword)) {
            password.setError("Password is empty");
            valid =  false;
        } else if (!PASSWORD_PATTERN.matcher(mPassword).matches()) {
            password.setError("Password usually must contain atleast 1 uppercase, " +
                    "1 lowercase,1 special character" +
                    " 6-20 characters in length   ");
            valid =  false;
        }

        return valid;

    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle("Please confirm");
        builder.setMessage("Are you sure you want to exit the app?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do something when user want to exit the app
                // Let allow the system to handle the event, such as exit the app
//                activity_login.super.onBackPressed();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                System.exit(0);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do something when want to stay in the app
//                Toast.makeText(mContext,"thank you",Toast.LENGTH_LONG).show();
            }
        });

        // Create the alert dialog using alert dialog builder
        AlertDialog dialog = builder.create();

        // Finally, display the dialog when user press back button
        dialog.show();
    }




}
