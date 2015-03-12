package com.group9.cs122b.moviequiz;

import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.Toast;
import com.group9.cs122b.moviequiz.QuestionTemplate.DataType;


public class QuizTakingActivity extends ActionBarActivity implements View.OnClickListener {

    public static int numOfCorrectAnswers;
    public static int numOfIncorrectAnswers;
    private static ArrayList<Integer> timePerQuestion = new ArrayList<>();
    private Timer checkForOutOfTimeTimer;

    public static int calculateAvgTimePerQuestion()
    {
        int totalTime = 0;
        for (Integer eachTime : timePerQuestion)
        {
            totalTime += eachTime;
        }

        return totalTime / timePerQuestion.size();
    }

    private static Random random = new Random();
    private TextView mBackButton;

    private long questionStartTime;

    private static TextView mTimeLabel;
    private static Handler mHandler = new Handler();
    private static long mStart;
    private static final long duration = 10000;
    private static boolean outOfTime = false;

    private static Runnable updateTask = new Runnable() {
        public void run() {
            long now = SystemClock.uptimeMillis();
            long elapsed = duration - (now - mStart);

            if (elapsed > 0) {
                int seconds = (int) (elapsed / 1000);
                int minutes = seconds / 60;
                seconds     = seconds % 60;

                if (mTimeLabel != null)
                {
                    if (seconds < 10) {
                        mTimeLabel.setText("" + minutes + ":0" + seconds);
                    } else {
                        mTimeLabel.setText("" + minutes + ":" + seconds);
                    }
                }

                mHandler.postAtTime(this, now + 1000);
            }
            else {
                mHandler.removeCallbacks(this);
                outOfTime = true;
            }
        }
    };

    public static ArrayList<QuestionTemplate> allQuestionTemplates = new ArrayList<QuestionTemplate>() {{
        add(new QuestionTemplate("Who directed the movie X?", new QuestionTemplate.DataType[]{DataType.MOVIE}, DataType.DIRECTOR));
        add(new QuestionTemplate("When was the movie X released?", new DataType[]{DataType.MOVIE}, DataType.YEAR));
        add(new QuestionTemplate("Which star was in the movie X?", new DataType[]{DataType.MOVIE}, DataType.STAR));
        add(new QuestionTemplate("Which star wasn't in the movie X?", new DataType[]{DataType.MOVIE}, DataType.STAR));
        add(new QuestionTemplate("In which movie did the stars X and Y appear together?", new DataType[]{DataType.STAR, DataType.STAR}, DataType.MOVIE));
        add(new QuestionTemplate("Who directed the star X?", new DataType[]{DataType.STAR}, DataType.DIRECTOR));
        add(new QuestionTemplate("Who didn't direct the star X?", new DataType[]{DataType.STAR}, DataType.DIRECTOR));
        add(new QuestionTemplate("Which star appears in both movies X and Y?", new DataType[]{DataType.MOVIE, DataType.MOVIE}, DataType.STAR));
        add(new QuestionTemplate("Which star did not appear in the same movie with star X?", new DataType[]{DataType.STAR}, DataType.STAR));
        add(new QuestionTemplate("Who directed the star X in year Y?", new DataType[]{DataType.STAR, DataType.YEAR}, DataType.DIRECTOR));
    }};

    private int correctAnswerResId;

    public static void setupAndBeginQuiz()
    {
        numOfCorrectAnswers = 0;
        numOfIncorrectAnswers = 0;
        timePerQuestion.clear();

        mStart = SystemClock.uptimeMillis();
        mHandler.post(updateTask);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiztaking);

        Question question = generateQuestion();

        TextView questionText = (TextView)findViewById(R.id.question);
        questionText.setText(question.question);

        TextView answer1Button = (TextView)findViewById(R.id.answer1);
        answer1Button.setOnClickListener(this);
        TextView answer2Button = (TextView)findViewById(R.id.answer2);
        answer2Button.setOnClickListener(this);
        TextView answer3Button = (TextView)findViewById(R.id.answer3);
        answer3Button.setOnClickListener(this);
        TextView answer4Button = (TextView)findViewById(R.id.answer4);
        answer4Button.setOnClickListener(this);

        // Random placement of correct answer is handled here.
        switch (random.nextInt(4))
        {
            case 0:
                answer1Button.setText(question.correctAnswer);
                answer2Button.setText(question.wrongAnswers[0]);
                answer3Button.setText(question.wrongAnswers[1]);
                answer4Button.setText(question.wrongAnswers[2]);
                correctAnswerResId = R.id.answer1;
                break;
            case 1:
                answer1Button.setText(question.wrongAnswers[0]);
                answer2Button.setText(question.correctAnswer);
                answer3Button.setText(question.wrongAnswers[1]);
                answer4Button.setText(question.wrongAnswers[2]);
                correctAnswerResId = R.id.answer2;
                break;
            case 2:
                answer1Button.setText(question.wrongAnswers[0]);
                answer2Button.setText(question.wrongAnswers[1]);
                answer3Button.setText(question.correctAnswer);
                answer4Button.setText(question.wrongAnswers[2]);
                correctAnswerResId = R.id.answer3;
                break;
            case 3:
                answer1Button.setText(question.wrongAnswers[0]);
                answer2Button.setText(question.wrongAnswers[1]);
                answer3Button.setText(question.wrongAnswers[2]);
                answer4Button.setText(question.correctAnswer);
                correctAnswerResId = R.id.answer4;
                break;
        }

        mTimeLabel = (TextView)this.findViewById(R.id.timeLabel);
        checkForOutOfTimeTimer = new Timer();
        checkForOutOfTimeTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (outOfTime)
                {
                    checkForOutOfTimeTimer.cancel();
                    System.out.println("DONE!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    startActivity(new Intent(getBaseContext(), ResultsActivity.class));
                }
            }
        }, 1000, 1000);

        // Retrieve the button, change its value and add an event listener
        this.mBackButton = (TextView)this.findViewById(R.id.backButton);
        this.mBackButton.setOnClickListener(this);

        questionStartTime = System.currentTimeMillis();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == correctAnswerResId)
        {
            Toast.makeText(this, "CORRECT!", Toast.LENGTH_SHORT).show();
            numOfCorrectAnswers++;
            goToNextQuestion();
        }
        else if (v.getId() == R.id.answer1 || v.getId() == R.id.answer2 ||
                 v.getId() == R.id.answer3 || v.getId() == R.id.answer4)
        {
            Toast.makeText(this, "WRONG!", Toast.LENGTH_SHORT).show();
            numOfIncorrectAnswers++;
            goToNextQuestion();
        }
        else if (v.getId() == R.id.backButton)
        {
            mHandler.removeCallbacks(updateTask);
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(), MainActivity.class));
    }

    private Question generateQuestion()
    {
        // TODO: We can put code to obtain server data here. Need to write something that parses the question string
        // and replaces the X's and Y's with the appropriate data.
        int randomQuestionIndex = random.nextInt(allQuestionTemplates.size());
        switch (randomQuestionIndex)
        {
            case 0:
                // Server calls? Don't forget break;
            default:
                return new Question(allQuestionTemplates.get(randomQuestionIndex).question,
                        "right answer", new String[]{"wrong answer 1", "wrong answer 2", "wrong answer 3", "wrong answer 4"});
        }
    }

    private void goToNextQuestion()
    {
        timePerQuestion.add((int)(System.currentTimeMillis() - questionStartTime));
        checkForOutOfTimeTimer.cancel();

        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                startActivity(new Intent(getBaseContext(), QuizTakingActivity.class));
            }
        }, 500);
    }
}

