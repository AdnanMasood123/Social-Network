package com.example.socialnetwork;

import static com.example.socialnetwork.R.layout.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
{
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView  PostList;
    private Toolbar mToolBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView Image;
    private TextView Name;
    private ImageButton Add_Post;

    private FirebaseAuth mAuth;
    private String CurentUserId;
    public boolean LikeChekcer=false;
    private DatabaseReference UsersRef,PostsRef,LikesRef;




    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);



       mAuth=FirebaseAuth.getInstance();
       CurentUserId=mAuth.getCurrentUser().getUid();
       UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
       PostsRef= FirebaseDatabase.getInstance().getReference().child("User Posts");
       LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes");


        mToolBar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Home");

        drawerLayout=(DrawerLayout) findViewById(R.id.drawerlayout);
        Image=(CircleImageView) findViewById(R.id.nav_profile_image);
        //Like=(ImageButton)findViewById(R.id.dislike);
       // Comment=(ImageButton)findViewById(R.id.comment);
        //PutComment=(TextView)findViewById(R.id.dislike_post);
        PostList=(RecyclerView) findViewById(R.id.all_user_post);
        navigationView=(NavigationView) findViewById(R.id.navigation_view);



        PostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        PostList.setLayoutManager(linearLayoutManager);


        Add_Post=(ImageButton) findViewById(R.id.Add_Post_Button);



        actionBarDrawerToggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        navigationView.bringToFront();
        View view=navigationView.inflateHeaderView(navigation_header);


        NavigationHeaderNameImage();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {

                 ItemSelecter(item);
                return false;
            }
        });


        Add_Post.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
              SendToPostActivity();
            }
        });




      DisplayAllUsersPosts();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void ItemSelecter(MenuItem item)
    {
        switch (item.getItemId())
        {

            case R.id.nav_Post:
                SendToPostActivity();
                break;

            case R.id.nav_Profile:
                Intent friendLstintent=new Intent(getApplicationContext(),Person_Profile_Activity.class);
                startActivity(friendLstintent);
                break;

            case R.id.nav_Home:
                Intent Mainintent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(Mainintent);
                break;

            case R.id.nav_Friends:
                Intent friendListintent=new Intent(getApplicationContext(),FirendsListActivity.class);
                startActivity(friendListintent);
                break;

            case R.id.nav_Find_Friends:
                Intent friendintent=new Intent(getApplicationContext(),FindFriendsActivity.class);
                startActivity(friendintent);
                break;

            case R.id.nav_Messages:
                Toast.makeText(getApplicationContext(),"Messages",Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_Settings:
                  Intent intent=new Intent(getApplicationContext(),AccountSettingsActivity.class);
                  startActivity(intent);
                break;

            case R.id.nav_Chat_Rooms:
                Intent Chatintent=new Intent(getApplicationContext(),ChatRoomsActivity.class);
                startActivity(Chatintent);
                break;

            case R.id.nav_Logout:
                UpdateUserState("Offline");
                    LogoutConformationBox();
                break;

        }

    }

    @Override
    protected void onStart()
    {

        super.onStart();
        UpdateUserState("Online");

        FirebaseUser currenUser=mAuth.getCurrentUser();

        if(currenUser==null)
        {
            SendToLoginActivity();
        }
        else
        {
            CheckUserExistence();
        }


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


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Are you want to Exit?");

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });

        builder.show();


    }

    private void SendToLoginActivity()
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void SendToRegisterActivity()
    {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }



      private void CheckUserExistence()
    {
        final String CurrentUserId=mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                if(!(snapshot.hasChild(CurrentUserId)))
                {
                   SendToSetupActivity();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }




    private void SendToSetupActivity()
    {
        Intent intent = new Intent(getApplicationContext(), SetupActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void SendToPostActivity()
    {
        Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



    private void NavigationHeaderNameImage()
    {

        UsersRef.child(CurentUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                if(snapshot.exists())
                {
                    String name=snapshot.child("Full Name").getValue().toString();
                    Toast.makeText(getApplicationContext(),"Name "+name,Toast.LENGTH_LONG).show();
                    Name=(TextView)findViewById(R.id.nav_Profile_name);
                    Name.setText(name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            }
        });

    }




    private void DisplayAllUsersPosts()
    {

        FirebaseRecyclerAdapter<Posts,PostsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                (Posts.class, R.layout.all_post_layout, PostsViewHolder.class, PostsRef)
        {

            @Override
            protected void populateViewHolder(PostsViewHolder postsViewHolder, Posts model, final int i)
            {

                String PostKey = getRef(i).getKey();

                postsViewHolder.setFullname(model.getFullname());
                postsViewHolder.setDescription(model.getDescription());
                postsViewHolder.setDate(model.getDate());
                postsViewHolder.setTime(model.getTime());
                postsViewHolder.setPostimage(MainActivity.this, model.getPostimage());

                postsViewHolder.setLikePostStatus(PostKey);

                postsViewHolder.mView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(getApplicationContext(), ClickPostActivity.class);
                        intent.putExtra("PostKey", PostKey);
                        startActivity(intent);
                    }

                });


                postsViewHolder.Comment.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent=new Intent(getApplicationContext(),CommentsActivity.class);
                        intent.putExtra("PostKey", PostKey);
                        startActivity(intent);
                    }
                });



                postsViewHolder.Likes.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        LikeChekcer=true;
                        LikesRef.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {

                                 if(LikeChekcer)
                                 {
                                     if (snapshot.child(PostKey).hasChild(CurentUserId))
                                     {
                                         LikesRef.child(PostKey).child(CurentUserId).removeValue();
                                         LikeChekcer = false;
                                     } else {
                                         LikesRef.child(PostKey).child(CurentUserId).setValue(true);
                                         LikeChekcer = false;
                                     }

                                 }
                                  }



                             @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {

                            }

                        });



                     }
                });

            }


        };
        firebaseRecyclerAdapter.startListening();
        PostList.setAdapter(firebaseRecyclerAdapter);
        UpdateUserState("Online");

    }




    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {

        ImageButton Likes,Comment;
        TextView PutComment;
        View mView;
        DatabaseReference LikesRef;
        String  currentUserId;
        int CountLikes;


        public PostsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mView=itemView;

            Comment=(ImageButton)mView.findViewById(R.id.comment);
            Likes=(ImageButton)mView.findViewById(R.id.dislike);
            PutComment=(TextView)mView.findViewById(R.id.dislike_post);
            currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
            LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes");

        }

        public void setLikePostStatus(final String PostKey)
        {
                 LikesRef.addValueEventListener(new ValueEventListener()
                 {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot)
                     {

                                if(snapshot.child(PostKey).hasChild(currentUserId))
                                {
                                   CountLikes=(int) snapshot.child(PostKey).getChildrenCount();
                                   Likes.setImageResource(R.drawable.like);
                                   PutComment.setText(Integer.toString(CountLikes)+(" Likes"));

                                }
                                else
                                {
                                    CountLikes=(int) snapshot.child(PostKey).getChildrenCount();
                                    Likes.setImageResource(R.drawable.dislike);
                                    PutComment.setText(Integer.toString(CountLikes)+(" Likes"));
                                }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error)
                     {

                     }

                 });
        }



        public void setPostimage(Context ctx,String postimage)
        {
            ImageView PostImage=(ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(postimage).into(PostImage);
            Toast.makeText(ctx.getApplicationContext(), "Image" +postimage,Toast.LENGTH_LONG).show();

        }

        public void setFullname(String fullname)
        {
            TextView UserName=(TextView) mView.findViewById(R.id.Friend_user_name);
            UserName.setText(fullname);
        }

        public void setTime(String time)
        {
            TextView PostTime=(TextView) mView.findViewById(R.id.Firend_user_Date);
            PostTime.setText(time);
        }

        public void setDate(String date)
        {
            TextView PostDate=(TextView) mView.findViewById(R.id.Friend_user_Time);
            PostDate.setText(date);
        }

        public void setDescription(String description)
        {
            TextView PostDescription=(TextView) mView.findViewById(R.id.post_user_Description);
            PostDescription.setText(description);
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

       UsersRef.child(CurentUserId).child("Users Status").updateChildren(SatausMap);


   }





    private void LogoutConformationBox()
    {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to Logout?");

        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                mAuth.signOut();
                SendToRegisterActivity();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
              dialogInterface.cancel();
            }
        });

        builder.show();

    }







}















