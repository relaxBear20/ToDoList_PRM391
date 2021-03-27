package com.example.todolist_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private EditText email, password, rePassword;
    private TextView registerBtn;
    private TextView registerQn;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);
        registerBtn = findViewById(R.id.registerBtn);
        registerQn = findViewById(R.id.registerQn);

        mAuth = FirebaseAuth.getInstance();
        progressDialog =new ProgressDialog(this);


        registerQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        }) ;

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringEmail =  email.getText().toString();
                String stringPassword = password.getText().toString();
                String stringRePassword = rePassword.getText().toString();

                if(TextUtils.isEmpty(stringEmail)) {
                    email.setError("Email is Required");
                }
                else if(TextUtils.isEmpty(stringPassword)) {
                    password.setError("Password is Required");
                }
                else if (TextUtils.isEmpty(stringRePassword)) {
                    rePassword.setError("Re-Password is Required");
                }
                else {
                    if (!stringRePassword.equals(stringPassword)){
                        rePassword.setError("Password isn't matched");
                    }
                        else{
                        progressDialog.setMessage("Registeration in progress");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        mAuth.createUserWithEmailAndPassword(stringEmail,stringPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(RegistrationActivity .this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    progressDialog.dismiss();
                                }
                                else {
                                    Toast.makeText(RegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void signIn(View view) {
        Intent intent = new Intent(RegistrationActivity .this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}