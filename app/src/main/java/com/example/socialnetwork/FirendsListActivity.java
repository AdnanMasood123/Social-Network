package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompatSideChannelService;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FirendsListActivity extends AppCompatActivity
{
    private RecyclerView Friends_List;
    private DatabaseReference  FriendsRef,UsersRef;
    private String Online_User_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firends_list);

        Online_User_Id= FirebaseAuth.getInstance().getCurrentUser().getUid();
        FriendsRef= FirebaseDatabase.getInstance().getReference().child("Friends").child(Online_User_Id);
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");

        Friends_List=(RecyclerView)findViewById(R.id.Friends_List_RecyclerView);

        Friends_List.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        Friends_List.setLayoutManager(linearLayoutManager);



        DisplayAllFriends();


    }


    @Override
    protected void onStart()
    {
        super.onStart();

        UpdateUserState("Online");

    }


    @Override
    protected void onStop()
    {
        super.onStop();

        UpdateUserState("Offline");

    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        UpdateUserState("Offline");

    }

    private void DisplayAllFriends()
    {
        FirebaseRecyclerAdapter<FriendsGetter,FriendsViewHolder> recyclerAdapter=new FirebaseRecyclerAdapter<FriendsGetter, FriendsViewHolder>
                (FriendsGetter.class,R.layout.all_users_display,FriendsViewHolder.class,FriendsRef)
        {
            @Override
            protected void populateViewHolder(FriendsViewHolder friendsViewHolder, FriendsGetter friendsGetter, int i)
            {

                friendsViewHolder.setDate(friendsGetter.getDate());

              final String UserIDs=getRef(i).getKey();


              UsersRef.child(UserIDs).addValueEventListener(new ValueEventListener()
              {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot)
                  {
                      if(snapshot.exists())
                      {
                          final String name=snapshot.child("name").getValue().toString();
                          friendsViewHolder.setName(name);


                          if(snapshot.hasChild("Users Status"))
                          {
                          final String Type=snapshot.child("Users Status").child("Status").getValue().toString();

                          if(Type.equals("Online"))
                          {
                              friendsViewHolder.OnlineIcon.setVisibility(View.VISIBLE);
                          }
                          else
                          {
                              friendsViewHolder.OnlineIcon.setVisibility(View.INVISIBLE);
                          }

                          }




                            friendsViewHolder.view.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                     CharSequence options[]=new CharSequence[]
                                             {
                                                 name +"'s Profile",
                                                 "Send Message"
                                             };

                                    AlertDialog.Builder builder=new AlertDialog.Builder(FirendsListActivity.this);
                                    builder.setTitle("Select Options");

                                    builder.setItems(options, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            if(i==0)
                                            {
                                                Intent intent=new Intent(getApplicationContext(),Person_Profile_Activity.class);
                                                intent.putExtra("Visit_User_Id",UserIDs);
                                                startActivity(intent);
                                            }

                                            if(i==1)
                                            {
                                                Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
                                                intent.putExtra("Visit_User_Id",UserIDs);
                                                intent.putExtra("name",name);
                                                startActivity(intent);
                                            }

                                        }
                                    });


                                    builder.show();


                                }
                            });



                      }

                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error)
                  {

                  }
              });



               }
        };

        Friends_List.setAdapter(recyclerAdapter);


    }


    public static class  FriendsViewHolder extends  RecyclerView.ViewHolder
    {
      View view;
      ImageView OnlineIcon;

        public FriendsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            view=itemView;

            OnlineIcon=(ImageView)view.findViewById(R.id.all_user_online_icon);
        }


        public void setDate(String date)
        {
            TextView Date=(TextView)view.findViewById(R.id.Firend_user_Date);
            Date.setText(date);
        }

        public  void   setName(String name)
        {
            TextView Name=(TextView)view.findViewById(R.id.Friend_user_name);
            Name.setText(name);
        }



    }




    public void UpdateUserState(String Status)
    {
        String SaveCurrentDate,SaveCurrentTime;

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        SaveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        SaveCurrentTime = currentTime.format(calFordDate.getTime());

        Map SatausMap=new HashMap();
        SatausMap.put("Date",SaveCurrentDate);
        SatausMap.put("Time",SaveCurrentTime);
        SatausMap.put("Status",Status);

        UsersRef.child(Online_User_Id).child("Users Status").updateChildren(SatausMap);


    }



}