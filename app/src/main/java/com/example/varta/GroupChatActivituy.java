package com.example.varta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.varta.Adapters.ChatAdapter;
import com.example.varta.Models.MessageModel;
import com.example.varta.cryptography.AESCryptoChat;
import com.example.varta.databinding.ActivityGroupChatActivituyBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivituy extends AppCompatActivity {
    AESCryptoChat aes = new AESCryptoChat("lv39eptlvuhaqqsr");
    ActivityGroupChatActivituyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGroupChatActivituyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        getSupportActionBar().hide();
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GroupChatActivituy.this,MainActivity.class);
                startActivity(intent);
            }
        });

    final FirebaseDatabase databse=FirebaseDatabase.getInstance();
    final ArrayList<MessageModel> messageModels=new ArrayList<>();

    final String senderId= FirebaseAuth.getInstance().getUid();
    binding.userName.setText("Friends Group");
    final ChatAdapter adapter=new ChatAdapter(messageModels,this);
    binding.chatRecyclarView.setAdapter(adapter);
    LinearLayoutManager layoutManager=new LinearLayoutManager(this);
    binding.chatRecyclarView.setLayoutManager(layoutManager);



    databse.getReference().child("Group Chat")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            messageModels.clear();
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                MessageModel model=dataSnapshot.getValue(MessageModel.class);
                                messageModels.add(model);

                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });





    binding.send.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String message=binding.etMessage.getText().toString();
            String encryptedMessage = null;

            try {
                encryptedMessage = aes.encrypt(message);
            } catch (Exception e) {
//            Logger.getLogger(AESCrypt.class.getName()).log(Level.SEVERE, null, e);
                e.printStackTrace();
            }
            final MessageModel model=new MessageModel(senderId,encryptedMessage);
            model.setTimestamp(new Date().getTime());
            binding.etMessage.setText("");
            databse.getReference().child("Group Chat")
                    .push()
                    .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
        }
    });

    }
}