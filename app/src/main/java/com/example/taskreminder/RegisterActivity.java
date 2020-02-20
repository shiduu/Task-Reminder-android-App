package com.example.taskreminder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class RegisterActivity extends AppCompatActivity {

    private TextView register;
    private EditText registerEmail, registerPass;
    private Button registerbtn;

    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        register =  (TextView)findViewById(R.id.register_txt);
        registerEmail = (EditText)findViewById(R.id.email_register);
        registerPass = (EditText)findViewById(R.id.pass_register);
        registerbtn = (Button)findViewById(R.id.register_btn);
        mDialog = new ProgressDialog(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(login);
            }
        });


        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = registerEmail.getText().toString();
                String mPass = registerPass.getText().toString();

                if (TextUtils.isEmpty(mEmail))
                {
                    registerEmail.setError("required field...");
                    return;
                }
                if (TextUtils.isEmpty(mPass))
                {
                    registerPass.setError("required field...");
                    return;
                }

                mDialog.setTitle("Registration");
                mDialog.setMessage("please wait while we register you");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            Toast.makeText(RegisterActivity.this, "Successfull", Toast.LENGTH_SHORT).show();
                            Intent home  = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(home);
                            mDialog.dismiss();
                        }else
                        {
                            Toast.makeText(RegisterActivity.this, "problem while creating account", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }

                    }
                });



            }
        });



    }
}
