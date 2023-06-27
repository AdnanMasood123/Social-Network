package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class SetupActivity extends AppCompatActivity
{
  private CircleImageView Image;
  private EditText Name,FullName,Country;
  private Button Save;

  String currentUserId;
  private FirebaseAuth mAuth;
  private DatabaseReference RootRef;
  private StorageReference ProfileImagesRef;

  final  static  int GalleryPic=1;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth=FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference().child("Users");
        ProfileImagesRef= FirebaseStorage.getInstance().getReference().child("Profile Images");


        Image=(CircleImageView) findViewById(R.id.Setup_Profile_Image);
        Name=(EditText)findViewById(R.id.Setup_Profile_Name);
        FullName=(EditText)findViewById(R.id.Setp_Profile_FullName);
        Country=(EditText) findViewById(R.id.Setup_Profile_Country);
        Save=(Button) findViewById(R.id.Setup_Profile_button);




        Image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               Intent galleryIntent=new Intent();
               galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
               galleryIntent.setType("images/*");
               startActivityForResult(galleryIntent,GalleryPic);
            }
        });



        Save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                  SaveDataIntoDatabase();
            }
        });




    }

    private void SaveDataIntoDatabase()
    {
        String name=Name.getText().toString();
        String fullName=FullName.getText().toString();
        String country=Country.getText().toString();




        HashMap<String,String> ProfileMap=new HashMap<>();
        ProfileMap.put("currentUsrId" , currentUserId);
        ProfileMap.put("name" , name);
        ProfileMap.put("Full Name" , fullName);
        ProfileMap.put("Country" , country);
        ProfileMap.put("status" , "Busy");
        ProfileMap.put("dob" , "24/11/2021");
        ProfileMap.put("Relationship status" , "Single");


        RootRef.child(currentUserId).setValue(ProfileMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    SendToMainActivity();
                    Toast.makeText(getApplicationContext(),"Data Saved SucessfULLY",Toast.LENGTH_LONG).show();
                }
                else
                {
                    String message=task.getException().toString();
                    Toast.makeText(getApplicationContext(),"error"+ message,Toast.LENGTH_LONG).show();
                }

            }
        });

    }



    private void  SendToMainActivity()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPic && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }


        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK)
            {
                Uri resultUri=result.getUri();

                StorageReference filePath=ProfileImagesRef.child(currentUserId+ ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"Image Upload SuccessFully To Storage",Toast.LENGTH_LONG).show();

                            final String downloadUri=task.getResult().getStorage().getDownloadUrl().toString();

                                RootRef.child(currentUserId).child("Profile Images").setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(getApplicationContext(), "Image Upload SuccessFully To Database", Toast.LENGTH_LONG).show();


                                            RootRef.child(currentUserId).addValueEventListener(new ValueEventListener()
                                            {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                                {
                                                    if(snapshot.exists())
                                                    {
                                                         String ProfileImage=snapshot.child("Profile Images").getValue().toString();


                                                           Picasso.with(SetupActivity.this).load(ProfileImage).placeholder(R.drawable.profile).into(Image);
                                                            //Glide.with(SetupActivity.this).load(ProfileImage).into(Image);
                                                            Toast.makeText(getApplicationContext(),"Image Retrieved",Toast.LENGTH_LONG).show();
                                                    }
                                                }


                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error)
                                                {
                                                    Toast.makeText(getApplicationContext(),"Error"+ error.getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });



                                        }
                                        else
                                        {
                                            String message=task.getException().toString();
                                            Toast.makeText(getApplicationContext(),"Error "+message,Toast.LENGTH_LONG).show();
                                        }
                                    }

                                });


                        }
                        else
                        {
                            String message=task.getException().toString();
                            Toast.makeText(getApplicationContext(),"Error "+message,Toast.LENGTH_LONG).show();
                        }

                    }

                });
            }


        }

    }


}

