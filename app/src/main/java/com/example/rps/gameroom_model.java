package com.example.rps;

public class gameroom_model {

    private String creator;
    private String loser;
    private String player1;
    private String player1Selection;
    private String player2;
    private String player2Selection;
    private Integer roomId;
    private String winner;
    private String status;

    public gameroom_model(String creator, String loser, String player1, String player1Selection, String player2, String player2Selection, Integer roomId, String winner) {
        this.creator = creator;
        this.loser = loser;
        this.player1 = player1;
        this.player1Selection = player1Selection;
        this.player2 = player2;
        this.player2Selection = player2Selection;
        this.roomId = roomId;
        this.winner = winner;
        this.status = status;
    }

    public gameroom_model(){

    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLoser() {
        return loser;
    }

    public void setLoser(String loser) {
        this.loser = loser;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer1Selection() {
        return player1Selection;
    }

    public void setPlayer1Selection(String player1Selection) {
        this.player1Selection = player1Selection;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getPlayer2Selection() {
        return player2Selection;
    }

    public void setPlayer2Selection(String player2Selection) {
        this.player2Selection = player2Selection;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "gameroom_model{" +
                "creator='" + creator + '\'' +
                ", loser='" + loser + '\'' +
                ", player1='" + player1 + '\'' +
                ", player1Selection='" + player1Selection + '\'' +
                ", player2='" + player2 + '\'' +
                ", player2Selection='" + player2Selection + '\'' +
                ", roomId='" + roomId + '\'' +
                ", winner='" + winner + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
