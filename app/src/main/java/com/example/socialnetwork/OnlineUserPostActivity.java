package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class OnlineUserPostActivity extends AppCompatActivity
{

    private Toolbar mToolbar;
    private RecyclerView PostList;

    private FirebaseAuth mAuth;
    private String CurrentUserId;
    public boolean LikeChekcer=false;
    private DatabaseReference postsRef,LikesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_user_post);

        mAuth=FirebaseAuth.getInstance();
        CurrentUserId=mAuth.getCurrentUser().getUid();
        postsRef= FirebaseDatabase.getInstance().getReference().child("User Posts");
        LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes");

        PostList=(RecyclerView)findViewById(R.id.My_Posts_RecyclerView);

        mToolbar=(Toolbar)findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Posts");


        PostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        PostList.setLayoutManager(linearLayoutManager);

        DisplayMyPosts();

    }


    private void DisplayMyPosts()
    {
        Query query=postsRef.orderByChild("uid").startAt(CurrentUserId).endAt(CurrentUserId + "\uf8ff");

        FirebaseRecyclerAdapter<Posts, MainActivity.PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, MainActivity.PostsViewHolder>
                (Posts.class, R.layout.all_post_layout, MainActivity.PostsViewHolder.class, query)
        {

            @Override
            protected void populateViewHolder(MainActivity.PostsViewHolder postsViewHolder, Posts model, final int i)
            {

                String PostKey = getRef(i).getKey();

                postsViewHolder.setFullname(model.getFullname());
                postsViewHolder.setDescription(model.getDescription());
                postsViewHolder.setDate(model.getDate());
                postsViewHolder.setTime(model.getTime());
                postsViewHolder.setPostimage(OnlineUserPostActivity.this, model.getPostimage());

                postsViewHolder.setLikePostStatus(PostKey);



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
                                    if (snapshot.child(PostKey).hasChild(CurrentUserId))
                                    {
                                        LikesRef.child(PostKey).child(CurrentUserId).removeValue();
                                        LikeChekcer = false;
                                    } else {
                                        LikesRef.child(PostKey).child(CurrentUserId).setValue(true);
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


            }

        };

        PostList.setAdapter(firebaseRecyclerAdapter);
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



        public void setPostimage(Context ctx, String postimage)
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






}