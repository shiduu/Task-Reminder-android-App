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

public class MainActivity extends AppCompatActivity {

    private TextView login;
    private EditText loginEmail, loginPass;
    private Button loginbtn;

    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        login = (TextView)findViewById(R.id.signup_txt);
        loginbtn = (Button) findViewById(R.id.login_btn);
        loginEmail = (EditText) findViewById(R.id.email_login);
        loginPass = (EditText) findViewById(R.id.pass_login);
        mDialog = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(register);
            }
        });


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = loginEmail.getText().toString();
                String mPass = loginPass.getText().toString();

                if (TextUtils.isEmpty(mEmail))
                {
                    loginEmail.setError("required field");
                    return;
                }
                if (TextUtils.isEmpty(mPass))
                {
                    loginPass.setError("filed required");
                    return;
                }

                mDialog.setTitle("Login in");
                mDialog.setMessage("please wait we log you in");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent home  = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(home);
                            mDialog.dismiss();
                        }else
                        {
                            Toast.makeText(MainActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }

                    }
                });

            }
        });

    }
}
