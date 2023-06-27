package com.example.socialnetwork;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class ChatsAdapter extends RecyclerView.Adapter
{

    private ArrayList<ChatsModel> chatsModelArrayList;
    private Context context;

    public ChatsAdapter(ArrayList<ChatsModel> chatsModelArrayList, Context context)
    {
        this.chatsModelArrayList = chatsModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;

        switch (viewType)
        {
            case 0:

                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_background,parent,false);
                return  new UsersViewHolder(view);

            case 1:

                view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_message_item,parent,false);
                return  new BotsViewHolder(view);

        }

        return  null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ChatsModel chatsModel=chatsModelArrayList.get(position);
        switch (chatsModel.getSender())
        {
            case "user":
                ((UsersViewHolder)holder).UserTv.setText(chatsModel.getMessage());
                break;

            case "Bot":
                ((BotsViewHolder)holder).BotTv.setText(chatsModel.getMessage());
                break;
        }

    }


    @Override
    public int getItemViewType(int position)
    {
        switch (chatsModelArrayList.get(position).getSender())
        {
            case  "user":
                return 0;
            case "Bot":
                return  1;
            default:
                return  -1;
        }

    }

    @Override
    public int getItemCount()
    {
        return chatsModelArrayList.size();
    }



    public static class UsersViewHolder extends  RecyclerView.ViewHolder
    {
        TextView UserTv;
        public UsersViewHolder(@NonNull View itemView)
        {
            super(itemView);
            UserTv= itemView.findViewById(R.id.idTvUser);
        }

    }



    public static class BotsViewHolder extends  RecyclerView.ViewHolder
    {
        TextView BotTv;

        public BotsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            BotTv=itemView.findViewById(R.id.idTvBot);
        }
    }



}
