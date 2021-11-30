package com.agaldanaw.reto3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Board> list;

    public MyAdapter(Context context, int layout, ArrayList<Board> boards)
    {
        this.context = context;
        this.layout = layout;
        this.list = boards;
    }

//    public void updateList(ArrayList<Board> boards)
//    {
//        this.list.clear();
//        for(int i = 0; i < boards.size(); i++)
//        {
//            this.list.add(boards.get(i));
//        }
//        this.notifyDataSetChanged();
//    }

    @Override
    public int getCount() {
        if(list == null)
            return 0;
        return this.list.size();
    }

    @Override
    public Board getItem(int i) {
        return this.list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        LayoutInflater inflater = LayoutInflater.from(this.context);
        v = inflater.inflate(R.layout.row_boards, null);

        String id = list.get(i).id;
        ((TextView)v.findViewById(R.id.title)).setText(id);
        return v;
    }
}




