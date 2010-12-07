package com.topsyturvy;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScoreBoardAdapter extends BaseAdapter {
    
	private Context context;
    private List<ScoreBoard> scores;

    public ScoreBoardAdapter(Context context, List<ScoreBoard> scores) {
        this.context = context;
        this.scores = scores;
    }

    public int getCount() {
        return scores.size();
    }

    public Object getItem(int position) {
        return scores.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
    	LayoutInflater inflater;
    	ScoreBoard entry;
    	
    	entry = scores.get(position);
        
        if (convertView == null) {
        	inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.players_row, null);
        }
        
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "FORTE.TTF");
        
        TextView playerListName = (TextView)convertView.findViewById(R.id.playerListName);
        playerListName.setText(entry.getName());
        playerListName.setTypeface(tf);

        TextView playerListTopScore = (TextView) convertView.findViewById(R.id.playerListTopScore);
        playerListTopScore.setText(Integer.toString(entry.getTopScore()));
        playerListTopScore.setTypeface(tf);

        return convertView;
    }
}