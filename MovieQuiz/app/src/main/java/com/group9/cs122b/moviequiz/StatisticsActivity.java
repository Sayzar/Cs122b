package com.group9.cs122b.moviequiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class StatisticsActivity extends ActionBarActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //setting preferences
        SharedPreferences prefs = this.getSharedPreferences("quizStatistics", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        TextView tv_quizzesTaken = (TextView)findViewById(R.id.quizzesTaken);
        int totalQuizzesTaken = prefs.getInt("totalQuizzesTaken", 0); //0 is the default value
        tv_quizzesTaken.append(Integer.toString(totalQuizzesTaken));

        TextView tv_correctAnswers = (TextView)findViewById(R.id.correctAnswers);
        int totalCorrectAnswers = prefs.getInt("totalCorrectAnswers", 0); //0 is the default value
        tv_correctAnswers.append(Integer.toString(totalCorrectAnswers));

        TextView tv_inCorrectAnswers = (TextView)findViewById(R.id.incorrectAnswers);
        int totalIncorrectAnswers = prefs.getInt("totalIncorrectAnswers", 0); //0 is the default value
        tv_inCorrectAnswers.append(Integer.toString(totalIncorrectAnswers));

        TextView tv_avgTimePerQuestion = (TextView)findViewById(R.id.avgTimePerQuestion);
        int totalTime = prefs.getInt("totalTime", 0);
        double avgTime = totalTime / ((double)(totalCorrectAnswers + totalIncorrectAnswers));
        tv_avgTimePerQuestion.append(Double.toString(avgTime));

        TextView returnToMenuButton = (TextView)findViewById(R.id.returnToMenu);
        returnToMenuButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.returnToMenu)
        {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(), MainActivity.class));
    }
}
