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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

public class AccountSettingsActivity extends AppCompatActivity
{
    private EditText Name,FullName,Country;
    private Button Update;

    private FirebaseAuth mAuth;
    private String CurrentUserId,userName,userFullName,userCountry;
    private DatabaseReference GetUserInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mAuth=FirebaseAuth.getInstance();
        CurrentUserId=mAuth.getCurrentUser().getUid();
        GetUserInfoRef= FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserId);

        Name=(EditText) findViewById(R.id.Setup_Profile_Name);
        FullName=(EditText) findViewById(R.id.Setp_Profile_FullName);
        Country=(EditText) findViewById(R.id.Setup_Profile_Country);
        Update=(Button)findViewById(R.id.Setup_Profile_button);

        GetUserInfoFromDatabase();



        Update.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                 ValidateUserInfo();
            }

        });


    }



    private void GetUserInfoFromDatabase()
    {

         GetUserInfoRef.addValueEventListener(new ValueEventListener()
         {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot)
             {
                  if(snapshot.exists())
                  {
                        String name=snapshot.child("Name").getValue().toString();
                        String fullname=snapshot.child("Full Name").getValue().toString();
                        String country=snapshot.child("Country").getValue().toString();

                        Name.setText(name);
                         FullName.setText(fullname);
                        Country.setText(country);



                  }


             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

    }




    private void ValidateUserInfo()
    {
        userName=Name.getText().toString();
        userFullName=FullName.getText().toString();
        userCountry=Country.getText().toString();

        if(TextUtils.isEmpty(userName))
        {
            Toast.makeText(getApplicationContext(),"Please Set Name First",Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(userFullName))
        {
            Toast.makeText(getApplicationContext(),"Please Set Full Name First",Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(userCountry))
        {
            Toast.makeText(getApplicationContext(),"Please Set Country First",Toast.LENGTH_LONG).show();
        }
        else
        {
            UpdateAccountInformation();
        }



    }

    private void UpdateAccountInformation()
    {
        HashMap userMap=new HashMap();

        userMap.put("Name",userName);
        userMap.put("Country",userCountry);
        userMap.put("Full Name",userFullName);


        GetUserInfoRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener()
        {
            @Override
            public void onComplete(@NonNull Task task)
            {
              if(task.isSuccessful())
              {
                  SendToMainActivity();
                  Toast.makeText(getApplicationContext(),"Information Updated SuccessFully",Toast.LENGTH_LONG).show();
              }
              else
              {
                  String Error=task.getException().toString();
                  Toast.makeText(getApplicationContext(),"Error"+Error,Toast.LENGTH_LONG).show();
              }

            }

        });


    }


    private void SendToMainActivity()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}