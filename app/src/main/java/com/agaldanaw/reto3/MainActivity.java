package com.agaldanaw.reto3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;

    public static String idPlayer;

    private SharedPreferences mPrefs;
    private RequestQueue queue;
    private ArrayList<Board> _list;
    private ListView _listView;

    public static String URL_API_GET_BOARDS = "http://tictactoeapi-dev.us-east-1.elasticbeanstalk.com/api/TicTacToe/GetAvailableBoards";
    public static String URL_API_AVAILABLE_BOARD = "http://tictactoeapi-dev.us-east-1.elasticbeanstalk.com/api/TicTacToe/IsBoardAvailable/";
    public static String URL_API_CREATE_BOARD = "http://tictactoeapi-dev.us-east-1.elasticbeanstalk.com/api/TicTacToe/CreateBoard/";
    public static String URL_HUB = "http://tictactoeapi-dev.us-east-1.elasticbeanstalk.com/tictactoe";
//
//    public static String URL_API_GET_BOARDS = "https://0b05-2800-484-6d87-e310-6114-2f98-b49-2fb4.ngrok.io/api/TicTacToe/GetAvailableBoards";
//    public static String URL_API_AVAILABLE_BOARD = "https://0b05-2800-484-6d87-e310-6114-2f98-b49-2fb4.ngrok.io/api/TicTacToe/IsBoardAvailable/";
//    public static String URL_API_CREATE_BOARD = "https://0b05-2800-484-6d87-e310-6114-2f98-b49-2fb4.ngrok.io/api/TicTacToe/CreateBoard/";
//    public static String URL_HUB = "https://0b05-2800-484-6d87-e310-6114-2f98-b49-2fb4.ngrok.io/tictactoe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        _listView = (ListView)findViewById(R.id.listBoards);
        TextView empylist = (TextView)findViewById(R.id.emptyList);
        _listView.setEmptyView(empylist);


        findViewById(R.id.createBoard).setOnClickListener(new ButtonCreateBoardClickListener(this));

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        idPlayer = mPrefs.getString("idPlayer", RandomstringUUID.GetGUID());
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putString("idPlayer", idPlayer);
        ed.commit();
        GetRequest(this);
    }



    public void GetRequest(Context context)
    {
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET
                , URL_API_GET_BOARDS
                , null
                , new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        _list = GetList(response);
                        fillListview();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ShowToast(error.toString(), context);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Context context = this;
        queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET
                , URL_API_AVAILABLE_BOARD + _list.get(i).id
                , null
                , new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        goToBoard(response, i);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ShowToast(error.toString(), context);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);


    }

    public void goToBoard(JSONObject response, int position)
    {
        try {
            if(response.getBoolean("available"))
            {
                Intent intent = new Intent(this, BoardActivity.class);
                intent.putExtra("boardId", _list.get(position).id);
                intent.putExtra("secondPlayer", idPlayer);
                intent.putExtra("groupName",  _list.get(position).group);
                startActivity(intent);
            }
            else
            {
                ShowToast("esta sala ya no está disponible", this);
                GetRequest(this);
            }
        }catch(Exception e)
        {

        }


    }


    private class ButtonCreateBoardClickListener implements View.OnClickListener {
        private boolean _clicked = false;
        private Context _context;
        public ButtonCreateBoardClickListener(Context context)
        {
            _context = context;
        }
        public void onClick(View view) {
            try {
                if(!_clicked)
                {
                    _clicked = true;
                    queue = Volley.newRequestQueue(_context);
                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST
                            , URL_API_CREATE_BOARD + idPlayer
                            , null
                            , new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    CreatedBoard(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    ShowToast(error.toString(), _context);
                                }
                            }
                    );
                    request.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(request);
                }
            }catch(Exception e) { // ignore
            }
            finally {
                _clicked = false;
            }
        }
    }

    public void CreatedBoard(JSONObject board)
    {
        try {
            Intent intent = new Intent(this, BoardActivity.class);
            intent.putExtra("boardId", board.getString("id"));
            intent.putExtra("groupName",   board.getString("group"));
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void fillListview() {
        MyAdapter adapter = new MyAdapter(this, android.R.layout.simple_list_item_1, _list);
        _listView.setAdapter(adapter);
        _listView.setOnItemClickListener(this);
    }

    private void ShowToast(String message, Context context) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private ArrayList<Board> GetList(JSONArray json)
    {
        try {
            ArrayList<Board> list = new ArrayList<Board>();
            for (int i = 0; i < json.length(); ++i)
            {
                JSONObject board = null;
                board = json.getJSONObject(i);
                list.add(new Board(board.getString("id"), board.getString("group"), board.getBoolean("available"),
                        board.getString("firstPlayerId"), board.getString("secondPlayerId"), board.getInt("ties"),
                        board.getInt("firstPlayerWins"), board.getInt("secondPlayerWins"), board.getInt("gamesPlayed")
                        )
                    );
            }
            return list;
        }
        catch (Exception e){}
        return new ArrayList<>();
    }

    private char[] getcharArray(JSONArray currentGame) {
        char[] ans = new char[TicTacToeGame.BOARD_SIZE];
        for (int i = 0; i < currentGame.length(); ++i)
        {
            try {
                char first = currentGame.get(i).toString().charAt(0);
                ans[i] = first;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ans;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quit:
                ShowDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.refresh:
                GetRequest(this);
                return true;
            case R.id.about:
                ShowDialog(DIALOG_ABOUT_ID);
                return true;
        }
        return false;
    }

    public void ShowDialogInfo(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
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

    public void ShowDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
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

