package com.agaldanaw.reto3;

import java.util.Random;

public class TicTacToeGame {

    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';

    private int computerWins = 0;
    private int humanWins = 0;
    private int ties = 0;

    public boolean mGameOver = false;

    private char mBoard[] = {'1','2','3','4','5','6','7','8','9'};
    public static final int BOARD_SIZE = 9;

    private Random mRand;

    public char currentInitPlayer;

    // The computer's difficulty levels
    public enum DifficultyLevel {Easy, Harder, Expert};
    // Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    public TicTacToeGame() {
        mRand = new Random();
    }

    public char[] getmBoard() {
        return mBoard;
    }

    public void setmBoard(char[] board)
    {
        this.mBoard = board;
    }

    /**
     * Return computerWins
     * */

    public String GetComputerWins()
    {
        return Integer.toString(computerWins);
    }

    /**
     * set computerWins
     * */

    public void updateComputerWins( int computer)
    {
        if(computer < 0)
            ++computerWins;
        else
            computerWins = computer;
    }

    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }

    public void ResetScores()
    {
        computerWins = 0;
        humanWins = 0;
        ties = 0;
    }

    /**
     * Return humanWins
     * */

    public String GetHumanWins()
    {
        return Integer.toString(humanWins);
    }

    /**
     * set humanWins
     * */

    public void UpdateHumanWins(int human)
    {
        if(human < 0)
            ++humanWins;
        else
            humanWins = human;
    }

    /**
     * Return ties
     * */

    public String GetTies()
    {
        return Integer.toString(ties);
    }

    /**
     * set ties
     * */

    public void UpdateTies(int ties)
    {
        if(ties < 0)
            ++this.ties;
        else
            this.ties = ties;
    }

    /**
     * Check for a winner and return a status value indicating who has won.
     * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won,
     * or 3 if O won.
     */
    public int checkForWinner() {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    public int GetComputerMove()
    {
        int move = -1;
        if (mDifficultyLevel == DifficultyLevel.Easy)
            move = getRandomMove();
        else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        }
        else if (mDifficultyLevel == DifficultyLevel.Expert) {
            // Try to win, but if that's not possible, block.
            // If that's not possible, move anywhere.
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        }
        return move;
    }

    public int getRandomMove()
    {
        // Generate random move
        int move = -1;
        do
        {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);

        return move;
    }

    public char getBoardOccupant(int position)
    {
        return mBoard[position];
    }

    public int getBlockingMove()
    {
        // See if there's a move O can make to block X from winning
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];   // Save the current number
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    //mBoard[i] = COMPUTER_PLAYER;
                    mBoard[i] = curr;
                    return i;
                }
                else
                    mBoard[i] = curr;
            }
        }

        return -1;
    }

    /** Return the best move for the computer to make. You must call setMove()
     * to actually make the computer move to that location.
     * @return The best move for the computer to make (0-8).
     */
    public int getWinningMove()
    {
        // First see if there's a move O can make to win
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    mBoard[i] = curr;
                    return i;
                }
                else
                    mBoard[i] = curr;
            }
        }

        return -1;
    }

    public char initPlayer()
    {
        mGameOver = false;
        int player = mRand.nextInt(2);
        if(player < 1)
        {
            //computer turn
            int move = GetComputerMove();
            setMove(COMPUTER_PLAYER, move);
            currentInitPlayer = COMPUTER_PLAYER;
            return COMPUTER_PLAYER;
        }
        currentInitPlayer = HUMAN_PLAYER;
        return HUMAN_PLAYER;
    }

    public void setInitPlayer(char player)
    {
        this.currentInitPlayer = player;
    }

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    public void clearBoard(boolean eraseWins)
    {
        if(eraseWins)
        {
            computerWins = 0;
            humanWins = 0;
            ties = 0;
        }
        mBoard = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};
    }

    public void GameOver()
    {
        mGameOver = true;
    }


    /** Set the given player at the given location on the game board.
     * The location must be available, or the board will not be changed.
     *
     * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - The location (0-8) to place the move
     */
    public void setMove(char player, int location)
    {
        if(mBoard[location] != HUMAN_PLAYER && mBoard[location] != COMPUTER_PLAYER )
        {
            mBoard[location] = player;
        }
    }
}
