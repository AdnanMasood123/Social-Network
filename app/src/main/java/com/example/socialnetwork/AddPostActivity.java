package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

@SuppressWarnings("ALL")
public class AddPostActivity extends AppCompatActivity
{
    private ImageView Post;
    private EditText Something;
    private ImageButton Update;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private StorageReference PostRef;
    private String currentUserId;
    final static int galleryPic = 1;
    private Uri ImageUri;
    private Uri resulturi;
    private String Description,SaveCurrentDate,SaveCurrentTime;
    private  DatabaseReference UsersRef;
    String RandomPostName;
     public   String downLoadUri;
     private ProgressDialog mLoadingBar;


    //final  String downLoadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference().child("User Posts");
        PostRef = FirebaseStorage.getInstance().getReference().child("Posts");
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");


        Post = (ImageView) findViewById(R.id.post_click);
        Something = (EditText) findViewById(R.id.write_post);
        Update = (ImageButton) findViewById(R.id.update);
        mLoadingBar=new ProgressDialog(this);


        Toolbar mToolBar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Add Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("images/*");
                startActivityForResult(galleryIntent, galleryPic);
            }
        });


        Update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ValidatePostInfo();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galleryPic && resultCode == RESULT_OK && data != null)
        {
            ImageUri = data.getData();
            Post.setImageURI(ImageUri);
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(3, 3).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                 resulturi = result.getUri();
            }


        }

    }


    private void ValidatePostInfo()
    {
        Description =Something.getText().toString();

        if(ImageUri==null)
        {
            Toast.makeText(getApplicationContext(), "Please Select Image", Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(getApplicationContext(), "Please Say Something Aout Post", Toast.LENGTH_LONG).show();
        }
        else
        {
            StoreInfo();
        }


    }

    private void StoreInfo()
    {

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        SaveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        SaveCurrentTime = currentTime.format(calFordDate.getTime());

        RandomPostName = SaveCurrentDate + SaveCurrentTime;


        StorageReference filePath = PostRef.child(ImageUri.getLastPathSegment() + RandomPostName + ".jpg");


        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if (task.isSuccessful())
                {

                    mLoadingBar.setTitle("Adding Post");
                    mLoadingBar.setMessage("Please Wait while we are Uploading Post!");
                    mLoadingBar.setCanceledOnTouchOutside(false);
                    mLoadingBar.show();

                    downLoadUri = task.getResult().getStorage().getDownloadUrl().toString();
                    Toast.makeText(getApplicationContext(), "Image Upload SuccessFully To Storage", Toast.LENGTH_LONG).show();
                    SavingPostInformationToDatabase();
                    mLoadingBar.dismiss();

                }
                else
                    {
                    String message = task.getException().toString();
                    Toast.makeText(getApplicationContext(), "Error " + message, Toast.LENGTH_LONG).show();

                }
            }
        });

    }


    private void  SendToMainActivity()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void SavingPostInformationToDatabase()
    {
        UsersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String userFullName = dataSnapshot.child("Full Name").getValue().toString();


                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", currentUserId);
                    postsMap.put("date", SaveCurrentDate);
                    postsMap.put("time",SaveCurrentTime);
                    postsMap.put("postimage", downLoadUri);
                    postsMap.put("description", Description);
                    postsMap.put("fullname", userFullName);

                    RootRef.child(currentUserId + RandomPostName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener()
                            {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        SendToMainActivity();
                                        Toast.makeText(getApplicationContext(), "New Post is updated successfully.", Toast.LENGTH_SHORT).show();

                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Error Occured while updating your post.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




}




