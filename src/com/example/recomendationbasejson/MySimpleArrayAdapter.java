package com.example.recomendationbasejson;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList <ArticlesFromListJSON> listOfArticles;

    public MySimpleArrayAdapter(Context context, ArrayList <ArticlesFromListJSON> list, ArrayList<String> stringArray) {
        super(context, R.layout.rowlayout, stringArray);
        this.context = context;
        this.listOfArticles = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        
        
        
        
        TextView textView1 = (TextView) rowView.findViewById(R.id.titleLabel);
        TextView textView2 = (TextView) rowView.findViewById(R.id.authorLabel);
        
        
        textView1.setText(this.listOfArticles.get(position).getTitle());
        textView2.setText(this.listOfArticles.get(position).getAuthor());
        

        return rowView;
    }
}