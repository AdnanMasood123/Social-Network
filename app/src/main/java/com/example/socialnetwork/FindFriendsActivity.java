package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

public class FindFriendsActivity extends AppCompatActivity
{
    private EditText Search;
    private ImageButton SearchButton;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private String currentUserId;
    String  SearchInputBox;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        Search=(EditText) findViewById(R.id.search_field);
        SearchButton=(ImageButton)findViewById(R.id.search);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView_friends);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        SearchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                 SearchInputBox=Search.getText().toString();
                Toast.makeText(getApplicationContext(),"Searing for user",Toast.LENGTH_LONG).show();
               DisplayAllUsers();

            }
        });

    }



    private void DisplayAllUsers()
    {
        Query query=UsersRef.orderByChild("name").startAt(SearchInputBox).endAt(SearchInputBox+"\uf8ff");


        FirebaseRecyclerAdapter<Users,UsersViewHolder> recyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>
                (Users.class,R.layout.all_users_display,UsersViewHolder.class,query)
        {

            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, final  int i)
            {

                       usersViewHolder.setName(users.getName());
                       usersViewHolder.setStatus(users.getStatus());


                       usersViewHolder.view.setOnClickListener(new View.OnClickListener()
                       {
                           @Override
                           public void onClick(View view)
                           {

                               String Visit_usr_id=getRef(i).getKey();
                               Intent intent=new Intent(getApplicationContext(),Person_Profile_Activity.class);
                               intent.putExtra("Visit_User_Id",Visit_usr_id);
                               startActivity(intent);
                           }
                       });


            }
        };
        recyclerView.setAdapter(recyclerAdapter);

    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder
    {
        View view;

        public UsersViewHolder(@NonNull View itemView)
        {
            super(itemView);
            view = itemView;
        }


        public void setName(String name)
        {
            TextView Name = (TextView) view.findViewById(R.id.Friend_user_name);
            Name.setText(name);
        }

        public void setStatus(String status)
        {
            TextView Status = (TextView) view.findViewById(R.id.Firend_user_Date);
            Status.setText(status);
        }

    }

}