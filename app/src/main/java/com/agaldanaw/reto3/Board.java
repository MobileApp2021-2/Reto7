package com.agaldanaw.reto3;

public class Board {
    public String id;
    public String group;
    public boolean available;
    public String firstPlayerId;
    public String secondPlayerId;
    public int ties;
    public int firstPlayerWins;
    public int secondPlayerWins;
    public int gamesPlayed;

    public Board(String Id
            ,String Group
            ,boolean Available
            ,String FirstPlayerId
            ,String SecondPlayerId
            ,int Ties
            ,int  FirstPlayerWins
            ,int SecondPlayerWins
            ,int GamesPlayed)
    {
        this.id = Id;
        this.group = Group;
        this.available = Available;
        this.firstPlayerId = FirstPlayerId;
        this.secondPlayerId = SecondPlayerId;
        this.ties = Ties;
        this.firstPlayerWins = FirstPlayerWins;
        this.secondPlayerWins = SecondPlayerWins;
        this.gamesPlayed = GamesPlayed;
    }
}
