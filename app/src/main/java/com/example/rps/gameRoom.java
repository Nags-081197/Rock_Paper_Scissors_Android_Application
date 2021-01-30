package com.example.rps;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class gameRoom extends AppCompatActivity implements View.OnClickListener {

    String roomId, player;
    TextView roomIdTV, playerTV, statusTV, playerConTV ;
    DatabaseReference myRef,myRef_selection,myRef_initial, myRef_exit,myRef_restart, myRef_winnerUpdate;
    FirebaseAuth fAuth;
    String userID;
    String update_key;
    String winner_select;
    Button rockBtn, paperBtn, scissorBtn, exitgameBtn, resetgameBtn;
    private Context mContext;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);

        roomIdTV = findViewById(R.id.roomIdTV);
        playerTV = findViewById(R.id.playerTV);
        statusTV = findViewById(R.id.statusTV);
        playerConTV = findViewById(R.id.playersConTV);

        rockBtn = findViewById(R.id.rockBtn);
        paperBtn = findViewById(R.id.paperBtn);
        scissorBtn = findViewById(R.id.scissorBtn);
        exitgameBtn = findViewById(R.id.exitgameBtn);
        resetgameBtn = findViewById(R.id.resetgameBtn);


        rockBtn.setOnClickListener(this);
        paperBtn.setOnClickListener(this);
        scissorBtn.setOnClickListener(this);
        exitgameBtn.setOnClickListener(this);
        resetgameBtn.setOnClickListener(this);

        mContext = getApplicationContext();
        mActivity = gameRoom.this;

        final gameroom_model gr = new gameroom_model();

        Bundle extra = getIntent().getExtras();

        if (extra != null) {

            roomId = extra.getString("roomId");
            player = extra.getString("player");
            roomIdTV.setText(roomId);
            playerTV.setText(player);

        }

        myRef_initial = FirebaseDatabase.getInstance().getReference().child("gameRoom");
        myRef_initial.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if ((ds.getValue(gameroom_model.class).getRoomId()).equals(Integer.parseInt(roomId))) {
                        gameroom_model gr = ds.getValue(gameroom_model.class);
                        statusTV.setText(gr.getStatus());
                        if(gr.getStatus().equals("Select a Move!")){
                            rockBtn.setEnabled(true);
                            paperBtn.setEnabled(true);
                            scissorBtn.setEnabled(true);
                        }
                        update_key = ds.getKey();

                        if (player.equals("PLAYER 1") && gr.getPlayer2().equals("null") ) {
                            playerConTV.setText("1");
                        } else if (player.equals("PLAYER 2") && gr.getPlayer1().equals("null")) {
                            playerConTV.setText("1");
                        }else{
                            playerConTV.setText("2");

                            if(!(gr.getPlayer1Selection()).equals("null")&& !(gr.getPlayer2Selection()).equals("null") ){
                                winner_select = winnerCheck( gr.getPlayer1Selection(), gr.getPlayer2Selection());

                                myRef_winnerUpdate = FirebaseDatabase.getInstance().getReference().child("gameRoom").child(update_key);
                                myRef_winnerUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot ds) {

                                        if ((ds.getValue(gameroom_model.class).getRoomId()).equals(Integer.parseInt(roomId))) {
                                            gameroom_model gr_winnerUpdate = ds.getValue(gameroom_model.class);
                                            if(winner_select.equals("PLAYER 1 Wins !")){
                                                gr_winnerUpdate.setStatus("PLAYER 1 Wins !");
                                                gr_winnerUpdate.setWinner("PLAYER 1");
                                                gr_winnerUpdate.setLoser("PLAYER 2");
                                                display(gr_winnerUpdate.getPlayer1Selection(),gr_winnerUpdate.getPlayer2Selection(),gr_winnerUpdate.getStatus());
                                            }

                                            else if (winner_select.equals("PLAYER 2 Wins !")){
                                                gr_winnerUpdate.setStatus("PLAYER 2 Wins !");
                                                gr_winnerUpdate.setWinner("PLAYER 2");
                                                gr_winnerUpdate.setLoser("PLAYER 1");
                                                display(gr_winnerUpdate.getPlayer1Selection(),gr_winnerUpdate.getPlayer2Selection(),gr_winnerUpdate.getStatus());
                                            }

                                            else if (winner_select.equals("Its a Tie !")){
                                                gr_winnerUpdate.setStatus("Its a Tie !");
                                                gr_winnerUpdate.setWinner("Its a Tie !");
                                                gr_winnerUpdate.setLoser("Its a Tie !");
                                                display(gr_winnerUpdate.getPlayer1Selection(),gr_winnerUpdate.getPlayer2Selection(),gr_winnerUpdate.getStatus());
                                            }

                                            myRef_winnerUpdate.setValue(gr_winnerUpdate);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                        }




                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rockBtn:
                Toast.makeText(gameRoom.this, "ROCK", Toast.LENGTH_SHORT).show();
                rockBtn.setEnabled(false);
                paperBtn.setEnabled(false);
                scissorBtn.setEnabled(false);
                selectionSet("ROCK");
                break;

            case R.id.paperBtn:
                Toast.makeText(gameRoom.this, "PAPER", Toast.LENGTH_SHORT).show();
                rockBtn.setEnabled(false);
                paperBtn.setEnabled(false);
                scissorBtn.setEnabled(false);
                selectionSet("PAPER");
                break;

            case R.id.scissorBtn:
                Toast.makeText(gameRoom.this, "SCISSOR", Toast.LENGTH_SHORT).show();
                rockBtn.setEnabled(false);
                paperBtn.setEnabled(false);
                scissorBtn.setEnabled(false);
                selectionSet("SCISSOR");
                break;

            case R.id.exitgameBtn:
                final gameroom_model gr_exit = new gameroom_model();
                myRef_exit = FirebaseDatabase.getInstance().getReference().child("gameRoom").child(update_key);
                myRef_exit.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {

                        if ((ds.getValue(gameroom_model.class).getRoomId()).equals(Integer.parseInt(roomId))) {
                            final gameroom_model gr_exit = ds.getValue(gameroom_model.class);;
                            if (player.equals("PLAYER 1")) {

                                final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                builder.setTitle("Please confirm");
                                builder.setMessage("Are you sure you want to Close the Game Room and Exit the Game?");
                                builder.setCancelable(true);

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        myRef_exit.removeValue();
                                        Toast.makeText(mContext,"Game Room Closed Successfully ! ",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Do something when want to stay in the app
//                Toast.makeText(mContext,"thank you",Toast.LENGTH_LONG).show();
                                    }
                                });

                                // Create the alert dialog using alert dialog builder
                                AlertDialog dialog = builder.create();

                                // Finally, display the dialog when user press back button
                                dialog.show();



                            } else if (player.equals("PLAYER 2")) {

                                final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                builder.setTitle("Please confirm");
                                builder.setMessage("Are you sure want to Exit the Game?");
                                builder.setCancelable(true);

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        gr_exit.setPlayer2Selection("null");
                                        gr_exit.setPlayer2("null");
                                        gr_exit.setWinner("null");
                                        gr_exit.setLoser("null");
                                        gr_exit.setStatus("Player 2 Left !!");
                                        myRef_exit.setValue(gr_exit);
                                        Toast.makeText(mContext,"Game Room Closed Successfully ! ",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Do something when want to stay in the app
//                Toast.makeText(mContext,"thank you",Toast.LENGTH_LONG).show();
                                    }
                                });

                                // Create the alert dialog using alert dialog builder
                                AlertDialog dialog = builder.create();

                                // Finally, display the dialog when user press back button
                                dialog.show();

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                break;

            case R.id.resetgameBtn:
                final gameroom_model gr_restart = new gameroom_model();
                myRef_restart = FirebaseDatabase.getInstance().getReference().child("gameRoom").child(update_key);
                myRef_restart.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {

                        if ((ds.getValue(gameroom_model.class).getRoomId()).equals(Integer.parseInt(roomId))) {
                            gameroom_model gr_restart = ds.getValue(gameroom_model.class);;
                            gr_restart.setLoser("null");
                            gr_restart.setWinner("null");
                            gr_restart.setPlayer1Selection("null");
                            gr_restart.setPlayer2Selection("null");
                            rockBtn.setEnabled(true);
                            paperBtn.setEnabled(true);
                            scissorBtn.setEnabled(true);

                            if (player.equals("PLAYER 1") && gr_restart.getPlayer2().equals("null") ) {
                                gr_restart.setStatus("Waiting for Player 2 to Join !");
                            } else if (player.equals("PLAYER 2") && gr_restart.getPlayer1().equals("null")) {
                                gr_restart.setStatus("Waiting for Player 1 to Join !");
                            }else{
                                gr_restart.setStatus("Select a Move!");
                            }
                            myRef_restart.setValue(gr_restart);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        }
    }

    public void selectionSet(final String selection) {

        final gameroom_model gr_update = new gameroom_model();
        myRef_selection = FirebaseDatabase.getInstance().getReference().child("gameRoom").child(update_key);
        myRef_selection.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {

                if ((ds.getValue(gameroom_model.class).getRoomId()).equals(Integer.parseInt(roomId))) {
                    gameroom_model gr_update = ds.getValue(gameroom_model.class);;
                    if (player.equals("PLAYER 1")) {
                        gr_update.setPlayer1Selection(selection);
                        gr_update.setStatus("PLAYER 1 - Move Selected");
                        myRef_selection.setValue(gr_update);
                    } else if (player.equals("PLAYER 2")) {
                        gr_update.setPlayer2Selection(selection);
                        gr_update.setStatus("PLAYER 2 - Move Selected");
                        myRef_selection.setValue(gr_update);


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onBackPressed(){

        final gameroom_model gr_exit = new gameroom_model();
        myRef_exit = FirebaseDatabase.getInstance().getReference().child("gameRoom").child(update_key);
        myRef_exit.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {

                if ((ds.getValue(gameroom_model.class).getRoomId()).equals(Integer.parseInt(roomId))) {
                    final gameroom_model gr_exit = ds.getValue(gameroom_model.class);;
                    if (player.equals("PLAYER 1")) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setTitle("Please confirm");
                        builder.setMessage("Are you sure you want to Close the Game Room and Exit the Game?");
                        builder.setCancelable(true);

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                myRef_exit.removeValue();
                                Toast.makeText(mContext,"Game Room Closed Successfully ! ",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Do something when want to stay in the app
//                Toast.makeText(mContext,"thank you",Toast.LENGTH_LONG).show();
                            }
                        });

                        // Create the alert dialog using alert dialog builder
                        AlertDialog dialog = builder.create();

                        // Finally, display the dialog when user press back button
                        dialog.show();



                    } else if (player.equals("PLAYER 2")) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setTitle("Please confirm");
                        builder.setMessage("Are you sure want to Exit the Game?");
                        builder.setCancelable(true);

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                gr_exit.setPlayer2Selection("null");
                                gr_exit.setPlayer2("null");
                                gr_exit.setWinner("null");
                                gr_exit.setLoser("null");
                                gr_exit.setStatus("Player 2 Left !!");
                                myRef_exit.setValue(gr_exit);
                                Toast.makeText(mContext,"Game Room Closed Successfully ! ",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Do something when want to stay in the app
//                Toast.makeText(mContext,"thank you",Toast.LENGTH_LONG).show();
                            }
                        });

                        // Create the alert dialog using alert dialog builder
                        AlertDialog dialog = builder.create();

                        // Finally, display the dialog when user press back button
                        dialog.show();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public String winnerCheck(String player1selection,String player2selection){
        if ((player1selection.equals("ROCK"))&& (player2selection.equals("ROCK"))){
            return("Its a Tie !");
        }
        else if ((player1selection.equals("ROCK"))&& (player2selection.equals("PAPER"))){
            return("PLAYER 2 Wins !");
        }
        else if ((player1selection.equals("ROCK"))&& (player2selection.equals("SCISSOR"))){
            return("PLAYER 1 Wins !");
        }
        else if ((player1selection.equals("PAPER"))&& (player2selection.equals("ROCK"))){
            return("PLAYER 1 Wins !");
        }
        else if ((player1selection.equals("PAPER"))&& (player2selection.equals("PAPER"))){
            return("Its a Tie !");
        }
        else if ((player1selection.equals("PAPER"))&& (player2selection.equals("SCISSOR"))){
            return("PLAYER 2 Wins !");
        }
        else if ((player1selection.equals("SCISSOR"))&& (player2selection.equals("ROCK"))){
            return("PLAYER 2 Wins !");
        }
        else if ((player1selection.equals("SCISSOR"))&& (player2selection.equals("PAPER"))){
            return("PLAYER 1 Wins !");
        }
        else if ((player1selection.equals("SCISSOR"))&& (player2selection.equals("SCISSOR"))){
            return("Its a Tie !");
        }
        return("null");
    }

    public void display(String player1selection, String player2selection, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(message);
        builder.setMessage("PLAYER 1 - "+player1selection+ "\nPLAYER 2 - "+player2selection);
        builder.setCancelable(true);
        // Create the alert dialog using alert dialog builder
        AlertDialog dialog = builder.create();

        // Finally, display the dialog when user press back button
        dialog.show();
    }




}



