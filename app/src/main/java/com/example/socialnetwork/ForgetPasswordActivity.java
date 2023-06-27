package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity
{
    private EditText inputPassword;
    private Button button;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth=FirebaseAuth.getInstance();

        inputPassword=(EditText)findViewById(R.id.Forget_Password_Field);
        button=(Button)findViewById(R.id.Forget_Password_Button);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                   String UserEmail=inputPassword.getText().toString();

                   if(TextUtils.isEmpty(UserEmail))
                   {
                       Toast.makeText(getApplicationContext(),"Pleae write your Email Address",Toast.LENGTH_LONG).show();
                   }
                   else
                   {
                       mAuth.sendPasswordResetEmail(UserEmail)
                               .addOnCompleteListener(new OnCompleteListener<Void>()
                       {
                           @Override
                           public void onComplete(@NonNull Task<Void> task)
                           {
                              if(task.isSuccessful())
                              {
                                  Toast.makeText(getApplicationContext(),"Check Your Email",Toast.LENGTH_LONG).show();
                                  Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                                  startActivity(intent);

                              }
                              else
                              {
                                  String Error=task.getException().toString();
                                  Toast.makeText(getApplicationContext(),""+Error,Toast.LENGTH_LONG).show();
                              }
                           }
                       });
                   }



            }
        });


    }

}