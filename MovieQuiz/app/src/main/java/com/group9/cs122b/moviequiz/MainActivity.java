package com.group9.cs122b.moviequiz;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView takeQuizButton = (TextView)findViewById(R.id.takeQuizButton);
        takeQuizButton.setOnClickListener(this);
        TextView quizStatisticsButton = (TextView)findViewById(R.id.quizStatsButton);
        quizStatisticsButton.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.takeQuizButton:
                QuizTakingActivity.setupAndBeginQuiz();
                startActivity(new Intent(getBaseContext(), QuizTakingActivity.class));
                break;
            case R.id.quizStatsButton:
                // TODO: Add quiz stats here.
                break;
            default:
                break;
        }
    }
}
