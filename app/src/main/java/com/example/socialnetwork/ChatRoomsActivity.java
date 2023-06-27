package com.example.socialnetwork;


import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChatRoomsActivity extends AppCompatActivity
{
    private ListView ListView;
    private Toolbar mToolbar;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> List_of_rooms=new ArrayList<>();

    private ImageButton AddRoomButton;
    private  EditText inputChatRoomField;

    private String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms);


        mToolbar=(Toolbar)findViewById(R.id.Chat_Rooms_Toolbar);
        AddRoomButton=(ImageButton)findViewById(R.id.send_message_button);
        ListView=(ListView)findViewById(R.id.chat_Rooms_ListView);
        inputChatRoomField=(EditText)findViewById(R.id.input_group_message);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat Room Names");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,List_of_rooms);
        ListView.setAdapter(arrayAdapter);

        Request_UserName();


        AddRoomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DatabaseReference mRef= FirebaseDatabase.getInstance().getReference(inputChatRoomField.getText().toString());
                mRef.setValue("");
                Toast.makeText(getApplicationContext(),"Room Name Saved",Toast.LENGTH_LONG).show();
                inputChatRoomField.setText("");

            }
        });



        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference(inputChatRoomField.getText().toString());
        mRef.addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Set<String> set=new HashSet<>();
                Iterator i=snapshot.getChildren() .iterator();

                while (i.hasNext())
                {
                    set.add(((DataSnapshot)i.next()).getKey());
                }

                List_of_rooms.clear();
                List_of_rooms.addAll(set);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }

        });



        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                 Intent messages=new Intent(getApplicationContext(),MessagesActivity.class);
                 messages.putExtra("User Name",UserName);
                 messages.putExtra("Room Name",((TextView)view).getText().toString());
                 startActivity(messages);
            }
        });


    }




    private void Request_UserName()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Enter a User Name");

        final EditText InputField=new EditText(this);
        builder.setView(InputField);
        InputField.setHint("Enter Your Name");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                UserName=InputField.getText().toString();
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();

                Request_UserName();
            }
        });

        builder.show();


    }


}