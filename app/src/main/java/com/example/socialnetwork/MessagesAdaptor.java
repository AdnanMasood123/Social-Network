package com.example.socialnetwork;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdaptor extends RecyclerView.Adapter<MessagesAdaptor.MessageViewHolder>
{
    private DatabaseReference UsersDatabaseRef;
    private List<Messages> UserMessagesList;
    private FirebaseAuth mAuth;

    public MessagesAdaptor(List<Messages> UserMessageList)
    {
        this.UserMessagesList=UserMessageList;
    }


    public class MessageViewHolder extends  RecyclerView.ViewHolder
    {
        public TextView SenderMessageText,RecieverMessageText;
        public CircleImageView Image;

        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            SenderMessageText=(TextView)itemView.findViewById(R.id.sender_message_text);
            RecieverMessageText=(TextView)itemView.findViewById(R.id.reciever_message_text);
            Image=(CircleImageView) itemView.findViewById(R.id.message_profile_image);

        }

    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
      View V= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_of_user,parent,false);
      mAuth=FirebaseAuth.getInstance();

      return  new MessageViewHolder(V);

    }


    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position)
    {

          String MessageSenderId=mAuth.getCurrentUser().getUid();
          Messages messages=UserMessagesList.get(position);

          String fromuserid=messages.getFrom();
          String FromMessageTyep=messages.getType();


            if(FromMessageTyep.equals("Text"))
            {

                 holder.RecieverMessageText.setVisibility(View.INVISIBLE);
                 holder.Image.setVisibility(View.INVISIBLE);

               if(fromuserid.equals(MessageSenderId))
                {
                  holder.SenderMessageText.setBackgroundResource(R.drawable.sender_background);
                  holder.SenderMessageText.setTextColor(Color.WHITE);
                  holder.SenderMessageText.setGravity(Gravity.LEFT);
                  holder.SenderMessageText.setText(messages.getMessage());


               }
                else
                    {

                    holder.SenderMessageText.setVisibility(View.INVISIBLE);

                  holder.RecieverMessageText.setVisibility(View.VISIBLE);
                  holder.Image.setVisibility(View.VISIBLE);

                  holder.RecieverMessageText.setBackgroundResource(R.drawable.reciever_background);
                  holder.RecieverMessageText.setTextColor(Color.BLACK);
                  holder.RecieverMessageText.setGravity(Gravity.LEFT);
                  holder.RecieverMessageText.setText(messages.getMessage());

                   }

           }
    }


    @Override
    public int getItemCount()
    {
        return UserMessagesList.size();
    }





}
