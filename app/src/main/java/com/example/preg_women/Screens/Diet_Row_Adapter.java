package com.example.preg_women.Screens;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.preg_women.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Diet_Row_Adapter extends BaseAdapter {
    private final ArrayList<String> title_list;
    private final ArrayList<String> content_list;
    int count;
    private DocumentReference store;

    public Diet_Row_Adapter( ArrayList<String> lists, ArrayList<String> content,int count) {
        this.title_list= lists;
        this.content_list= content;
        this.count=count;

    }
    @Override
    public int getCount() {
        return count;
    }
    @Override
    public Object getItem(int i) {
        return i;
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(viewGroup.getContext(), R.layout.diet_row,null);
        TextView txt = v.findViewById(R.id.diet_title);
        TextView txt2 = v.findViewById(R.id.diet_content);
        txt.setText(title_list.get(i));
        txt2.setText(content_list.get(i));
        return v;
    }

}