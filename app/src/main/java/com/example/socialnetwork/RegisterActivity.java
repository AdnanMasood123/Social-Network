package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    private Button  Register;
    private TextView textView;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference  RootRef;

    private ImageView Logo;
    private EditText Email,Password,Confirm_Password;

    private ProgressDialog mLoadingBar;





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
       // currentUserId=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();


        Email=(EditText)findViewById(R.id.Register_email);
        Password=(EditText)findViewById(R.id.Register_password);
        Confirm_Password=(EditText)findViewById(R.id.confirm_password);
        Register=(Button) findViewById(R.id.Register_button);
        textView=(TextView) findViewById(R.id.Already_have_account);
        mLoadingBar=new ProgressDialog(this);

        Register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Register();
            }

        });


        textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
             SendToLoginActivity();
            }
        });

    }

    private void SendToMainActivity()
    {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    private void SendToLoginActivity()
    {
        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }

    private void SendToSetupActivity()
    {
        Intent intent=new Intent(getApplicationContext(),SetupActivity.class);
        startActivity(intent);
    }




    private void Register()
    {

        String email=Email.getText().toString();
        String password=Password.getText().toString();
        String confirmPassword=Confirm_Password.getText().toString();


        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(),"Please Provide Your Email",Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(getApplicationContext(),"Please Provide Your Password",Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(getApplicationContext(),"Please Confirm Your Password",Toast.LENGTH_LONG).show();
        }
        else
        {

            mLoadingBar.setTitle("Registering New User");
            mLoadingBar.setMessage("Please Wait while we are Registering New  User!");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();


            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                         if(task.isSuccessful())
                         {
                             /*
                             currentUserId=mAuth.getCurrentUser().getUid();

                             HashMap<String,String> ProfileMap=new HashMap<>();
                             ProfileMap.put("currentUserId",currentUserId);
                             ProfileMap.put("Email",email);
                             ProfileMap.put("Password",password);
                             ProfileMap.put("Confirm_Password",confirmPassword);

                             RootRef.child("Users").child(currentUserId).setValue(ProfileMap).addOnCompleteListener(new OnCompleteListener<Void>()
                             {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task)
                                 {

                                     if(task.isSuccessful())
                                     {
                                         Toast.makeText(getApplicationContext(),"Data Saved SuccessFully",Toast.LENGTH_LONG).show();
                                     }
                                     else
                                     {
                                         String Error=task.getException().toString();
                                         Toast.makeText(getApplicationContext(),"Error "+Error,Toast.LENGTH_LONG).show();
                                     }

                                 }
                             }) ;   */




                             SendToSetupActivity();
                             Toast.makeText(getApplicationContext(),"New User Registered SuccessFully",Toast.LENGTH_LONG).show();
                             mLoadingBar.dismiss();
                         }
                         else
                         {
                             String Error=task.getException().toString();
                             Toast.makeText(getApplicationContext(),"Error "+Error,Toast.LENGTH_LONG).show();
                             mLoadingBar.dismiss();
                         }
                }
            });
        }



    }






}