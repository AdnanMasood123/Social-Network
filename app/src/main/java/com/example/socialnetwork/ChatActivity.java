package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity
{

    private String Visit_User_Id,RecieverUserName;
    private  Toolbar mtoolbar;
    private TextView RecieverName,LastScreen;
    private EditText TypeMessage;
    private ImageButton SendMessageButton;
    private RecyclerView MessageRecyclerView;
    private DatabaseReference RootRef,UsersRef;
    private FirebaseAuth mAuth;
    private String Sender_Id;

    private String SaveCurrentDate,SaveCurrentTime;

    private List<Messages>  MessageList=new ArrayList<Messages>();
    private LinearLayoutManager linearLayoutManager;
    private  MessagesAdaptor messagesAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth=FirebaseAuth.getInstance();
        Sender_Id=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();


        TypeMessage=(EditText)findViewById(R.id.input_group_message);
        SendMessageButton=(ImageButton)findViewById(R.id.send_message_button);
        LastScreen=(TextView)findViewById(R.id.chat_Last_Screen);



        Visit_User_Id=getIntent().getExtras().get("Visit_User_Id").toString();
        RecieverUserName=getIntent().getExtras().get("name").toString();

         RecieverName=(TextView)findViewById(R.id.chat_name);
         RecieverName.setText(RecieverUserName);


        messagesAdaptor=new MessagesAdaptor(MessageList);
        MessageRecyclerView= (RecyclerView) findViewById(R.id.Group_chat_Display);

        linearLayoutManager=new LinearLayoutManager(this);
        MessageRecyclerView.setHasFixedSize(true);
        MessageRecyclerView.setLayoutManager(linearLayoutManager);
        MessageRecyclerView.setAdapter(messagesAdaptor);



        FetchLastSreenData();

        FetchMessages();

         SendMessageButton.setOnClickListener(new View.OnClickListener()
         {
             @Override
             public void onClick(View view)
             {
                    SendMessageToUser();
                    TypeMessage.setText("");
             }
         });







    }


    private void SendMessageToUser()
    {

       // UpdateUserState("Online");

        String Message=TypeMessage.getText().toString();

        if(TextUtils.isEmpty(Message))
        {
            Toast.makeText(getApplicationContext(),"Please Write Message First",Toast.LENGTH_LONG).show();
        }
        else
        {
            String Message_Sender_Ref="Messages/"+ Sender_Id+ "/" + Visit_User_Id;
            String Message_Reciever_Ref="Messages/"+ Visit_User_Id+ "/" + Sender_Id;

            DatabaseReference User_Message_Key=RootRef.child("Messages").child(Sender_Id).child(Visit_User_Id).push();

            String Message_Push_Id=User_Message_Key.getKey();

            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            SaveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            SaveCurrentTime = currentTime.format(calFordDate.getTime());


            Map MessageTextBody=new HashMap();

            MessageTextBody.put("message",Message);
            MessageTextBody.put("date",SaveCurrentDate);
            MessageTextBody.put("time",SaveCurrentTime);
            MessageTextBody.put("type","Text");
            MessageTextBody.put("from",Sender_Id);


            Map MessageBodyDetails=new HashMap();
            MessageBodyDetails.put(Message_Sender_Ref+ "/" + Message_Push_Id +"/" , MessageTextBody);
            MessageBodyDetails.put(Message_Reciever_Ref+ "/" + Message_Push_Id +"/" , MessageTextBody);

            RootRef.updateChildren(MessageBodyDetails).addOnCompleteListener(new OnCompleteListener()
                    {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"Message Sent SuccessFully",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String message=task.getException().toString();
                        Toast.makeText(getApplicationContext(),""+message,Toast.LENGTH_LONG).show();
                    }

                }
            });

        }

    }



    private void FetchMessages()
    {



        RootRef.child("Messages")
                .child(Sender_Id).child(Visit_User_Id)
                .addChildEventListener(new ChildEventListener()
                {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if(snapshot.exists())
                {
                    Messages messages=snapshot.getValue(Messages.class);
                    MessageList.add(messages);
                    messagesAdaptor.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void FetchLastSreenData()
    {
        RootRef.child("Users").child(Visit_User_Id)
                .addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                  if(snapshot.exists())
                  {
                      final String type=snapshot.child("Users Status").child("Status").getValue().toString();
                      final String date=snapshot.child("Users Status").child("Date").getValue().toString();
                      final String time=snapshot.child("Users Status").child("Time").getValue().toString();



                      if(type.equals("Online"))
                      {
                          LastScreen.setText("Online");
                      }
                      else
                      {
                          LastScreen.setText("Last Seen" +" "+date +" "+time);
                      }

                  }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
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

        UsersRef.child("Users").child(Sender_Id).child("Users Status").updateChildren(SatausMap);


    }


}