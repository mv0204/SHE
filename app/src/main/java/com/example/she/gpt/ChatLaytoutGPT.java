package com.example.she.gpt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.she.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatLaytoutGPT extends AppCompatActivity {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    RecyclerView recyclerView;
    EditText chatMsg;
    ImageButton sendButton;
    TextView textView;
    ArrayList<MessageModelGPT> messageModelList;
    MessageAdapterGPT messageAdapter;
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_laytout_gpt);
        messageModelList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        chatMsg = findViewById(R.id.chatMsg);
        sendButton = findViewById(R.id.sendBtn);
        textView = findViewById(R.id.noMSG);


        messageAdapter = new MessageAdapterGPT(messageModelList, getApplicationContext());

        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String q = chatMsg.getText().toString().trim();
                addToChat(q, MessageModelGPT.SENT_BY_ME);
                callAPI(q);
                textView.setVisibility(View.GONE);
                chatMsg.setText("");

            }
        });

    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageModelList.add(new MessageModelGPT(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });

    }

    void addResponse(String response) {
        messageModelList.remove(messageModelList.size() - 1);
        addToChat(response, MessageModelGPT.SENT_BY_BOT);
    }

    void callAPI(String question) {

        messageModelList.add(new MessageModelGPT("typing...", MessageModelGPT.SENT_BY_BOT));

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("role", "user");
            jsonObject.put("content", question);

            jsonArray.put(jsonObject);
            jsonBody.put("messages", jsonArray);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Oops!! Failed to load msg due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    addResponse("Oops!! Failed to load msg due to>>> " + response.body().string());

                }
            }
        });

    }
}