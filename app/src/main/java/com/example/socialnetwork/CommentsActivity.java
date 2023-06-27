package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.PropertyName;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity
{

    private String post_key;
    private EditText commentField;
    private ImageButton button;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference UsersRef,PostsRef;
    private String SaveCurrentDate,SaveCurrentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        post_key=getIntent().getExtras().get("PostKey").toString();

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef=FirebaseDatabase.getInstance().getReference().child("User Posts").child(post_key).child("Comments");




        commentField=(EditText)findViewById(R.id.comment_Field);
        button=(ImageButton) findViewById(R.id.Add_Comment_button);
        recyclerView=(RecyclerView)findViewById(R.id.RecylerView_Comments);



        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                UsersRef.child(currentUserId).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                             String userName=snapshot.child("name").getValue().toString();
                             ValidateComment(userName);
                             commentField.setText("");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });

            }

        });

    }

    private void ValidateComment(String userName)
    {
        String comment=commentField.getText().toString();

        if(TextUtils.isEmpty(comment))
        {
            Toast.makeText(getApplicationContext(),"Please Add Some Comment",Toast.LENGTH_LONG).show();
        }
        else
        {

            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            SaveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            SaveCurrentTime = currentTime.format(calFordDate.getTime());

            String RandomKey=   currentUserId+SaveCurrentDate+SaveCurrentTime;


            HashMap commentsMap=new HashMap();

            commentsMap.put("Uid ",currentUserId);
            commentsMap.put("Name ",userName);
            commentsMap.put("Comment ",comment);
            commentsMap.put("Date ",SaveCurrentDate);
            commentsMap.put("Time ",SaveCurrentTime);

           PostsRef.child(RandomKey).updateChildren(commentsMap)
                   .addOnCompleteListener(new OnCompleteListener()
                   {
               @Override
               public void onComplete(@NonNull Task task)
               {

                   if(task.isSuccessful())
                   {
                       Toast.makeText(getApplicationContext(),"Comment Stored in Database Succesfully",Toast.LENGTH_LONG).show();
                   }
                   else
                   {
                       String error=task.getException().toString();
                       Toast.makeText(getApplicationContext()," "+error, Toast.LENGTH_LONG).show();
                   }
               }
           });

        }

    }


    @Override
    protected void onStart()
    {
        super.onStart();



        FirebaseRecyclerAdapter<CommentsGetter,CommentsViewHoler> recyclerAdapter=new FirebaseRecyclerAdapter<CommentsGetter, CommentsViewHoler>
                (CommentsGetter.class,R.layout.all_comments_layout,CommentsViewHoler.class,PostsRef)
        {
            @Override
            protected void populateViewHolder(CommentsViewHoler commentsViewHoler, CommentsGetter commentsGetter, int i)
            {
                commentsViewHoler.setComment(commentsGetter.getComment());
                commentsViewHoler.setName(commentsGetter.getName());
                commentsViewHoler.setDate(commentsGetter.getDate());
                commentsViewHoler.setTime(commentsGetter.getTime());
            }
        };
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);


    }


    public static class CommentsViewHoler extends  RecyclerView.ViewHolder
    {
        public TextView simpleText,comt;
        public View mView;
        public CommentsViewHoler(View itemView)
        {
            super(itemView);
            mView=itemView;

             comt=(TextView)mView.findViewById(R.id.comment_Text);

        }

        public void setComment(String comment)
        {

            comt.setText(comment);
        }

        public void setDate(String date)
        {

            TextView Dte=(TextView)mView.findViewById(R.id.comment_date);
            Dte.setText("date "+date);
        }

        public void setTime(String time)
        {
            TextView Tim=(TextView)mView.findViewById(R.id.comment_Time);
            Tim.setText("time "+time);
        }

        public void setName(String name)
        {
            TextView username=(TextView)mView.findViewById(R.id.comment_user_name);
            username.setText(name);

        }

    }



}