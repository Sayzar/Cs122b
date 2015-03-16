package com.group9.cs122b.moviequiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

public class ResultsActivity extends ActionBarActivity implements View.OnClickListener
{

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        TextView correctAnswers = (TextView)findViewById(R.id.correctAnswers);
        correctAnswers.append(Integer.toString(QuizTakingActivity.numOfCorrectAnswers));
        TextView inCorrectAnswers = (TextView)findViewById(R.id.incorrectAnswers);
        inCorrectAnswers.append(Integer.toString(QuizTakingActivity.numOfIncorrectAnswers));

        TextView avgTimePerQuestion = (TextView)findViewById(R.id.avgTimePerQuestion);
        avgTimePerQuestion.append(Integer.toString(QuizTakingActivity.calculateAvgTimePerQuestion()));

        TextView returnToMenuButton = (TextView)findViewById(R.id.returnToMenu);
        returnToMenuButton.setOnClickListener(this);

        // save overall quiz data
        //setting preferences
        SharedPreferences prefs = this.getSharedPreferences("quizStatistics", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int totalQuizzesTaken = prefs.getInt("totalQuizzesTaken", 0); //0 is the default value
        totalQuizzesTaken += 1;
        editor.putInt("totalQuizzesTaken", totalQuizzesTaken);

        int totalCorrectAnswers = prefs.getInt("totalCorrectAnswers", 0); //0 is the default value
        totalCorrectAnswers += QuizTakingActivity.numOfCorrectAnswers;
        editor.putInt("totalCorrectAnswers", totalCorrectAnswers);

        int totalIncorrectAnswers = prefs.getInt("totalIncorrectAnswers", 0); //0 is the default value
        totalIncorrectAnswers += QuizTakingActivity.numOfIncorrectAnswers;
        editor.putInt("totalIncorrectAnswers", totalIncorrectAnswers);

        int totalTime = prefs.getInt("totalTime", 0);
        for (Integer eachTime : QuizTakingActivity.timePerQuestion)
        {
            totalTime += eachTime;
        }
        editor.putInt("totalTime", totalTime);

        editor.commit();
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
