package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String UserName, RoomName;
    private DatabaseReference Root;
    private ImageButton SendMessage;
    private EditText InputMessage;
    private TextView DisplayMessage;
    String temp_key;

    @Override


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        SendMessage = (ImageButton) findViewById(R.id.send_message_button_Messages);
        InputMessage = (EditText) findViewById(R.id.input_message);
        DisplayMessage = (TextView) findViewById(R.id.Messages_Text);


        UserName = getIntent().getExtras().getString("User Name").toString();
        RoomName = getIntent().getExtras().getString("Room Name").toString();

        mToolbar=(Toolbar)findViewById(R.id.Chat_Rooms_Toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(RoomName);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Root = FirebaseDatabase.getInstance().getReference().child(RoomName);

        SendMessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                Map<String, Object> uniquekeymap = new HashMap<String, Object>();
                temp_key = Root.push().getKey();
                Root.updateChildren(uniquekeymap);


                DatabaseReference UsersRef = Root.child(temp_key);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", UserName);
                map.put("Message", InputMessage.getText().toString());
                UsersRef.updateChildren(map);

                InputMessage.setText("");

                Toast.makeText(getApplicationContext(), "Message Stored SuccessFully", Toast.LENGTH_LONG).show();

            }
        });


        Root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DisplayMessages(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });


    }

    String chatMessage, chatName;

    private void DisplayMessages(DataSnapshot snapshot)
    {
        Iterator i = snapshot.getChildren().iterator();

        while (i.hasNext()) {
            chatMessage = (String) ((DataSnapshot) i.next()).getValue();
            chatName = (String) ((DataSnapshot) i.next()).getValue();

            DisplayMessage.append(chatName + ":-   " + chatMessage + "\n\n");
        }

    }


}

