package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Person_Profile_Activity extends AppCompatActivity
{

    private String RecieverUserId,SenderId,CURRENT_STATE, SaveCurrentDate;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,FirendRequestRef,FriendsRef;

    private ImageView Image;
    private TextView ProfileName,UserName,Country,Relation,status;
    private Button SendRequest,CancelRequest;
    String Request_Typ1;
    private Button Posts,Friends;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);


        mAuth=FirebaseAuth.getInstance();
        SenderId=mAuth.getCurrentUser().getUid();
        RecieverUserId=getIntent().getExtras().get("Visit_User_Id").toString();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FirendRequestRef=FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        FriendsRef=FirebaseDatabase.getInstance().getReference().child("Friends");



        Friends=(Button)findViewById(R.id.Friends);
        Posts=(Button)findViewById(R.id.No_Of_Posts);
        Image=(ImageView) findViewById(R.id.Person_Profile_Image);
        ProfileName=(TextView)findViewById(R.id.Person_Profile);
        status=(TextView)findViewById(R.id.Person_Profile_status);
        UserName=(TextView)findViewById(R.id.Person_Profile_UserName);
        Country=(TextView)findViewById(R.id.Person_Profile_Country);
        Relation=(TextView)findViewById(R.id.Person_Profile_Relationship);
        SendRequest=(Button)findViewById(R.id.Person_Profile_SendRequest_button);
        CancelRequest=(Button)findViewById(R.id.Person_Profile_CancelRequest_button);
        CURRENT_STATE="not friends";




        UsersRef.child(RecieverUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                if(snapshot.exists())
                {
                    String profileNmae=snapshot.child("Full Name").getValue().toString();
                    String profileStatus=snapshot.child("status").getValue().toString();
                    String profileUserName=snapshot.child("name").getValue().toString();
                    String profileCountry=snapshot.child("Country").getValue().toString();
                    String profileRelation=snapshot.child("Relationship status").getValue().toString();


                    ProfileName.setText(profileNmae);
                    status.setText(profileStatus);
                    UserName.setText(profileUserName);
                    Country.setText(profileCountry);
                    Relation.setText(profileRelation);

                    MaintainanceOfButtons();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }

        });



          CancelRequest.setVisibility(View.INVISIBLE);
          CancelRequest.setEnabled(false);

             if(!SenderId.equals(RecieverUserId))
             {
               SendRequest.setOnClickListener(new View.OnClickListener()
               {
                   @Override
                   public void onClick(View view)
                   {
                      SendRequest.setEnabled(false);

                      if(CURRENT_STATE.equals("not friends"))
                      {
                            SendFriendRequestToPerson();
                      }

                       if(CURRENT_STATE.equals("Request Sent"))
                       {
                           CancelFriendRequest();
                       }


                       if(CURRENT_STATE.equals("Request Recieved"))
                       {

                           AcceptFriendRequest();
                       }



                   }
               });
          }
          else
          {
                   SendRequest.setVisibility(View.INVISIBLE);
                 CancelRequest.setVisibility(View.INVISIBLE);

        }



        Friends.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
             Intent intent=new Intent(getApplicationContext(),FirendsListActivity.class);
             startActivity(intent);
            }
        });

        Posts.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(getApplicationContext(),OnlineUserPostActivity.class);
                startActivity(intent);
            }
        });


    }



    private void SendFriendRequestToPerson()
    {
        FirendRequestRef.child(SenderId).child(RecieverUserId)
                .child("Request Type").setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                if(task.isSuccessful())
                {

                    FirendRequestRef.child(RecieverUserId).child(SenderId)
                            .child("Request Type").setValue("Recieved")
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                           if(task.isSuccessful())
                           {
                               SendRequest.setEnabled(true);
                               CURRENT_STATE="Request Sent";
                               SendRequest.setText("Cancel Request");;

                                CancelRequest.setVisibility(View.INVISIBLE);
                                CancelRequest.setEnabled(false);


                           }


                        }
                    });

                }

            }
        });

    }




    private void MaintainanceOfButtons()
    {
        FirendRequestRef.child(SenderId)
                .addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.hasChild(RecieverUserId))
                {
                    String Request_Type=snapshot.child(RecieverUserId).child("Request Type").getValue().toString();

                    if(Request_Type.equals("Sent"))
                    {
                      CURRENT_STATE="Request Sent";
                      SendRequest.setText("Cancel Request");

                        CancelRequest.setVisibility(View.INVISIBLE);
                        CancelRequest.setEnabled(true);
                    }

                    else if(Request_Type.equals("Recieved"))
                    {
                        CURRENT_STATE="Request Recieved";
                        SendRequest.setText("Accept Request");
                        CancelRequest.setVisibility(View.VISIBLE);
                        CancelRequest.setEnabled(true);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    private void CancelFriendRequest()
    {
        FirendRequestRef.child(SenderId).child(RecieverUserId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                if(task.isSuccessful())
                {

                    FirendRequestRef.child(RecieverUserId).child(SenderId)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        SendRequest.setEnabled(true);
                                        CURRENT_STATE="not_friends";
                                        SendRequest.setText("Send Request");;

                                        CancelRequest.setVisibility(View.INVISIBLE);
                                        CancelRequest.setEnabled(false);


                                    }
                                    else
                                    {

                                    }

                                }
                            });

                }

            }
        });
    }




    private void AcceptFriendRequest()
    {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        SaveCurrentDate = currentDate.format(calFordDate.getTime());


        FriendsRef.child(SenderId).child(RecieverUserId).child("Date")
                .setValue(SaveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                if(task.isSuccessful())
                {

                    FriendsRef.child(RecieverUserId).child(SenderId).child("Date")
                            .setValue(SaveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                            if(task.isSuccessful())
                            {


                                FirendRequestRef.child(SenderId).child(RecieverUserId)
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {

                                        if(task.isSuccessful())
                                        {

                                            FirendRequestRef.child(RecieverUserId).child(SenderId)
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                    {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                                SendRequest.setEnabled(true);
                                                                CURRENT_STATE="Friends";
                                                                SendRequest.setText("UnFreind Person");;

                                                                CancelRequest.setVisibility(View.INVISIBLE);
                                                                CancelRequest.setEnabled(false);


                                                            }
                                                            else
                                                            {

                                                            }

                                                        }
                                                    });

                                        }

                                    }
                                });


                            }


                        }

                    });
                }

            }

        });


    }



}