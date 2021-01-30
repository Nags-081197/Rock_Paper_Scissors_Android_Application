package com.example.rps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    Button createRoom;
    Button joinRoom;
    DatabaseReference myRef,myRef_update,myRef_updatefull;
    FirebaseAuth fAuth;
    String userID;
    String update_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createRoom = findViewById(R.id.createRoom);
        joinRoom = findViewById(R.id.joinRoom);
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();



        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int roomId = genRoomid();
                myRef = FirebaseDatabase.getInstance().getReference().child("gameRoom");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean flag = checkroomId(roomId, snapshot);
                        if(flag){

                            gameroom_model gr_push = new gameroom_model();
                            gr_push.setCreator(userID);
                            gr_push.setPlayer1(userID);
                            gr_push.setLoser("null");
                            gr_push.setWinner("null");
                            gr_push.setPlayer2("null");
                            gr_push.setPlayer1Selection("null");
                            gr_push.setPlayer2Selection("null");
                            gr_push.setStatus("Waiting for Player 2 to Join !");
                            gr_push.setRoomId(roomId);
                            myRef.push().setValue(gr_push);
                            Intent intent = new Intent(getApplicationContext(), gameRoom.class);
                            intent.putExtra("roomId",String.valueOf(gr_push.getRoomId()));
                            intent.putExtra("player","PLAYER 1");
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        joinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText joinRoomEV = new EditText(v.getContext());
                final AlertDialog.Builder joinRoomDialog = new AlertDialog.Builder(v.getContext());
                joinRoomDialog.setTitle("JOIN ROOM : ");
                joinRoomDialog.setMessage("ENTER ROOM ID TO JOIN:");
                joinRoomDialog.setView(joinRoomEV);

                joinRoomDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String regex = "[0-9]+";
                        final String roomidx = joinRoomEV.getText().toString().trim();
                        if (TextUtils.isEmpty(roomidx)) {
                            Toast.makeText(MainActivity.this,"Room ID is required !",Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!roomidx.matches(regex)) {
                            Toast.makeText(MainActivity.this,"Please Enter a Valid Room ID !",Toast.LENGTH_SHORT).show();
                            return;
                        } else {

                            myRef_updatefull = FirebaseDatabase.getInstance().getReference().child("gameRoom");
                            myRef_updatefull.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    boolean flag = checkroomId(Integer.parseInt(roomidx), snapshot);
                                    if (!flag) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            if ((ds.getValue(gameroom_model.class).getRoomId()).equals(Integer.parseInt(roomidx))) {

                                                gameroom_model gr = ds.getValue(gameroom_model.class);
                                                update_key = ds.getKey();
                                                myRef_update = FirebaseDatabase.getInstance().getReference().child("gameRoom").child(update_key);
                                                myRef_update.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot ds) {

                                                        if ((ds.getValue(gameroom_model.class).getRoomId()).equals(Integer.parseInt(roomidx))) {
                                                            gameroom_model gr_update = ds.getValue(gameroom_model.class);
                                                            ;
                                                            if ((gr_update.getPlayer1()).equals(userID)) {
                                                                Intent intent = new Intent(getApplicationContext(), gameRoom.class);
                                                                intent.putExtra("roomId", String.valueOf(gr_update.getRoomId()));
                                                                intent.putExtra("player", "PLAYER 1");
                                                                startActivity(intent);


                                                            } else if ((gr_update.getPlayer2()).equals("null") || (gr_update.getPlayer2()).equals(userID)) {
                                                                gr_update.setPlayer2(userID);
                                                                gr_update.setStatus("Select a Move!");
                                                                myRef_update.setValue(gr_update);
                                                                Intent intent = new Intent(getApplicationContext(), gameRoom.class);
                                                                intent.putExtra("roomId", String.valueOf(gr_update.getRoomId()));
                                                                intent.putExtra("player", "PLAYER 2");
                                                                startActivity(intent);

                                                            } else {
                                                                Toast.makeText(MainActivity.this, "Game Room Full", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }

                                        }


                                    }
                                    else{
                                        Toast.makeText(MainActivity.this,"Room ID Does Not Exist !",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                    }
                });

                joinRoomDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                joinRoomDialog.create().show();

            }
        });

    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),activity_login.class));
        finish();
    }

    public int genRoomid (){
        int n = 10000 + new Random().nextInt(90000);
        return(n);
    }

    public boolean checkroomId(int roomid , DataSnapshot dataSnapshot){
        final gameroom_model gr = new gameroom_model();


        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            gr.setRoomId(snapshot.getValue(gameroom_model.class).getRoomId());

            if(gr.getRoomId().equals(roomid)){
                return false;
            }
    }
        return true;
    }


}


