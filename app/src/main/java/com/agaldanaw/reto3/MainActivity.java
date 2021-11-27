package com.agaldanaw.reto3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;

    public TicTacToeGame mGame;
    private TextView mInfoTextView;
    private TextView infoHumanWins;
    private TextView infoComputerWins;
    private TextView infoTies;

    public BoardView mBoard;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfoTextView = (TextView) findViewById(R.id.information);
        infoHumanWins = (TextView) findViewById(R.id.humanWins);
        infoComputerWins = (TextView) findViewById(R.id.computerWins);
        infoTies = (TextView) findViewById(R.id.ties);
        findViewById(R.id.playAgain).setOnClickListener(new ButtonPlayAgainClickListener());
        mBoard = (BoardView) findViewById(R.id.board);
        mGame = new TicTacToeGame();

        if (savedInstanceState == null) {
            startGame();
            mBoard.setGame(mGame);
        }

        mBoard.setOnTouchListener(this);

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        // Restore the scores
        mGame.UpdateHumanWins(mPrefs.getInt("mHumanWins", 0));
        mGame.updateComputerWins(mPrefs.getInt("mComputerWins", 0));
        mGame.UpdateTies(mPrefs.getInt("mTies", 0));

        int difficultyLevel = mPrefs.getInt("difficultyLevel", getDifficultyLevelInteger() );
        setDifficultyLevelInteger(difficultyLevel);

        SetTextWins();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGame.setmBoard(savedInstanceState.getCharArray("board"));
        mGame.mGameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        mGame.UpdateHumanWins(savedInstanceState.getInt("mHumanWins"));
        mGame.UpdateTies(savedInstanceState.getInt("mTies"));
        mGame.updateComputerWins(savedInstanceState.getInt("mComputerWins"));
        mGame.currentInitPlayer = savedInstanceState.getChar("mGoFirst");
        int difficultyLevel = savedInstanceState.getInt("difficultyLevel");
        setDifficultyLevelInteger(difficultyLevel);
        mBoard.setGame(mGame);
        mBoard.invalidate();
        SetTextWins();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getmBoard());
        outState.putBoolean("mGameOver", mGame.mGameOver);
        outState.putInt("mHumanWins", Integer.valueOf(mGame.GetHumanWins()));
        outState.putInt("mComputerWins", Integer.valueOf(mGame.GetComputerWins()));
        outState.putInt("mTies", Integer.valueOf(mGame.GetTies()));
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mGoFirst", mGame.currentInitPlayer);
        outState.putInt("difficultyLevel", getDifficultyLevelInteger()  );

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.android);
    }

    @Override
    protected void onStop() {
        super.onStop();
// Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", Integer.valueOf(mGame.GetHumanWins()));
        ed.putInt("mComputerWins", Integer.valueOf(mGame.GetComputerWins()));
        ed.putInt("mTies", Integer.valueOf(mGame.GetTies()));
        ed.putInt("difficultyLevel", getDifficultyLevelInteger()  );

        ed.commit();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }


    public void SetTextWins()
    {
        String human = getResources().getString(R.string.human_wins);
        String computer = getResources().getString(R.string.computer_wins);
        String ties = getResources().getString(R.string.ties);

        infoHumanWins.setText(human + " " +  mGame.GetHumanWins());
        infoComputerWins.setText(computer + " " + mGame.GetComputerWins());
        infoTies.setText(ties + " " + mGame.GetTies());
    }

    private void startGame()
    {
        winner = -1;
        mGame.clearBoard(true);
        mBoard.invalidate();
        SetFirstTurn();
        SetTextWins();
    }

    public void SetFirstTurn()
    {
        char player = mGame.initPlayer();
        if(player == TicTacToeGame.HUMAN_PLAYER)
        {
            mInfoTextView.setText(R.string.first_human);
            computerTurn = false;
        }
        else
        {
            mInfoTextView.setText(R.string.first_computer);
            int winner = mGame.checkForWinner();
            setTextInfo(winner);
            computerTurn = false;
        }
    }

    public void setTextInfo(int winner)
    {
        if (winner == 0) {
            mInfoTextView.setText(R.string.turn_human);
        }
        else
        {
            if (winner == 1) {
                mGame.UpdateTies(-1);
                mInfoTextView.setText(R.string.result_tie);
            }
            else if (winner == 2){
                mGame.UpdateHumanWins(-1);
                mInfoTextView.setText(R.string.result_human_wins);
            }
            else {
                mGame.updateComputerWins(-1);
                mInfoTextView.setText(R.string.result_computer_wins);
            }
            mGame.GameOver();
        }
    }

    static int winner = -1;
    static boolean computerTurn = false;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // Determine which cell was touched
        int col = (int) motionEvent.getX() / mBoard.getBoardCellWidth();
        int row = (int) motionEvent.getY() / mBoard.getBoardCellHeight();
        int pos = row * 3 + col;
        if (!mGame.mGameOver && !computerTurn){
            mHumanMediaPlayer.start();
            mGame.setMove(TicTacToeGame.HUMAN_PLAYER, pos);
            mBoard.invalidate();
            // If no winner yet, let the computer make a move
            winner = mGame.checkForWinner();
            computerTurn = true;
            if (winner == 0) {
                mInfoTextView.setText(R.string.turn_computer);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mComputerMediaPlayer.start();
                        int move = mGame.GetComputerMove();
                        mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                        winner = mGame.checkForWinner();
                        setWinner(winner);
                        computerTurn = false;
                    }
                }, 500);
            }
            else if(winner == 1 || winner == 2)
                setWinner(winner);

        }
        // So we aren't notified of continued events when finger is moved
        return false;
    }

    private void setWinner(int winner)
    {
        setTextInfo(winner);
        SetTextWins();
        mBoard.invalidate();
    }

    private class ButtonPlayAgainClickListener implements OnClickListener {

        public void onClick(View view) {
            mGame.clearBoard(false);
            mBoard.invalidate();
            SetFirstTurn();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startGame();
                return true;
            case R.id.ai_difficulty:
                ShowDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                ShowDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about:
                ShowDialog(DIALOG_ABOUT_ID);
                return true;
            case R.id.reset:
                mGame.ResetScores();
                SetTextWins();
                return true;
        }
        return false;
    }

    private int getDifficultyLevelInteger()
    {
        TicTacToeGame.DifficultyLevel currentLevel = mGame.getDifficultyLevel();
        if(currentLevel == TicTacToeGame.DifficultyLevel.Easy)
            return 0;
        else if(currentLevel == TicTacToeGame.DifficultyLevel.Harder)
            return 1;
        return 2;
    }

    private void setDifficultyLevelInteger(int level)
    {
        if(0 == level)
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if(1 == level)
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
    }

    public void ShowDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_level_easy),
                        getResources().getString(R.string.difficulty_level_hard),
                        getResources().getString(R.string.difficulty_level_expert)};

                builder.setSingleChoiceItems(levels, getDifficultyLevelInteger(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog
                                setDifficultyLevelInteger(item);
                                startGame();
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                dialog = builder.create();
                break;
            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
            case DIALOG_ABOUT_ID:
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                Drawable icon = context.getDrawable(R.drawable.icon);
                builder.setIcon(icon);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();
                dialog.setTitle(R.string.app_name);
                break;
        }

        dialog.show();
    }
}