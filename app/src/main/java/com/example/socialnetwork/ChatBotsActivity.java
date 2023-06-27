package com.example.socialnetwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatBotsActivity extends AppCompatActivity
{
    private Toolbar mToolbar;

    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton SendMessageButton;

    private ArrayList<ChatsModel> chatsModelArrayList;
    private ChatsAdapter chatsAdapter;

    private  final  String BOT_KEY="Bot";
    private  final  String USER_KEY="user";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bots);

        recyclerView=(RecyclerView)findViewById(R.id.chatbot_recylcerView);
        editText=(EditText)findViewById(R.id.input_group_message);
        SendMessageButton=(ImageButton)findViewById(R.id.send_message_button);

        chatsModelArrayList=new ArrayList<>();
        chatsAdapter=new ChatsAdapter(chatsModelArrayList,this);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(chatsAdapter);

        mToolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Chat Bot");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SendMessageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(editText.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please write message",Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                  getBotResponse(editText.getText().toString());
                  editText.setText("");
                }

            }
        });





    }

    private void getBotResponse(String message)
    {
        chatsModelArrayList.add(new ChatsModel(message,USER_KEY));
        chatsAdapter.notifyDataSetChanged();

        String URL="http://api.brainshop.ai/get?bid=159370&key=bT6lmy2fjjCECkSy&uid=[uid]&msg="+message;
        String BASE_URL="http://api.brainshop.ai/";


        Retrofit retrofit=new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitApi retrofitApi=retrofit.create(RetrofitApi.class);
        Call<MsgModel> call=retrofitApi.getMessage(URL);
        call.enqueue(new Callback<MsgModel>()
        {
            @Override
            public void onResponse(Call<MsgModel> call, Response<MsgModel> response)
            {
                if(response.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(),"SuccessFull",Toast.LENGTH_LONG).show();
                    MsgModel model=response.body();
                    chatsModelArrayList.add(new ChatsModel(model.getCnt(),BOT_KEY));
                    chatsAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<MsgModel> call, Throwable t)
            {
                chatsModelArrayList.add(new ChatsModel("Revert you key",BOT_KEY));
                chatsAdapter.notifyDataSetChanged();
            }

        });

    }


}