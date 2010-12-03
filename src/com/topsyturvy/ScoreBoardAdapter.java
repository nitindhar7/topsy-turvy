/*
 * Copyright (C) 2010 Topsy-Turvy
 *
 * Authors:		Nitin Dhar (nitindhar7@yahoo.com)
 * 				Mayank Jain (mjain01@students.poly.edu)
 * 				Chintan Jain (cjain01@students.poly.edu)
 * 
 * Date: 		10/20/2010
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in 
 * the Software without restriction, including without limitation the rights to 
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS 
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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