package com.group9.cs122b.moviequiz;

import android.content.Intent;
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
