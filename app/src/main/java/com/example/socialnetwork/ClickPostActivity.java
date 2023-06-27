package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Currency;
import java.util.Locale;

public class ClickPostActivity extends AppCompatActivity
{

      private  String PostKey;
      private DatabaseReference clickPostsRef;
      private ImageView Image;
      private TextView Description;
      private Button Delete,Edit;
      private String currentUserId,DatabaseUserId,image,description;
      private Button Update;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        Image=(ImageView)findViewById(R.id.post_click);
        Description=(TextView)findViewById(R.id.Description);
        Edit= (Button) findViewById(R.id.edit_post);
        Delete= (Button) findViewById(R.id.delete_post);
          setTitle("Edit or Delete Post");


        PostKey=getIntent().getExtras().get("PostKey").toString();

        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        clickPostsRef= FirebaseDatabase.getInstance().getReference().child("User Posts").child(PostKey);


        // Delete.setVisibility(View.INVISIBLE);
        // Edit.setVisibility(View.INVISIBLE);



        clickPostsRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    image = snapshot.child("postimage").getValue().toString();
                    description = snapshot.child("description").getValue().toString();
                    DatabaseUserId = snapshot.child("uid").getKey().toString();

                    Picasso.with(ClickPostActivity.this).load(image).into(Image);
                    Description.setText(description);

                    if (currentUserId.equals(DatabaseUserId))
                    {
                        Delete.setVisibility(View.VISIBLE);
                        Edit.setVisibility(View.VISIBLE);
                    }


                    Edit.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                              EditCurrentPost(description);
                        }
                    });




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });





        Delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               DeleteCurrentPost();
            }
        });







    }

    private void DeleteCurrentPost()
    {
        clickPostsRef.removeValue();
        SendToMainActivity();
        Toast.makeText(getApplicationContext(),"Post has been Deleted!",Toast.LENGTH_LONG).show();
    }


    private void SendToMainActivity()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }




    private void EditCurrentPost(String description)
    {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(ClickPostActivity.this);
        alertDialog.setTitle("Editing Your Post Description");

        final EditText inputField=new EditText(this);
        inputField.setText(description);
        alertDialog.setView(inputField);


        alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                clickPostsRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(getApplicationContext(),"Post has been Eidited SuccessFully",Toast.LENGTH_LONG).show();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                    dialogInterface.cancel();
            }
        });

        Dialog dialog=alertDialog.create();
        dialog.show();

    }



}