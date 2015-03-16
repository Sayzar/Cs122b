package com.group9.cs122b.moviequiz;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.Toast;
import com.group9.cs122b.moviequiz.QuestionTemplate.DataType;


public class QuizTakingActivity extends ActionBarActivity implements View.OnClickListener {

    public static int numOfCorrectAnswers;
    public static int numOfIncorrectAnswers;
    public static final long duration = 180000;
    public static ArrayList<Integer> timePerQuestion = new ArrayList<>();
    private Timer checkForOutOfTimeTimer;

    private DatabaseHandler db;


    public static int calculateAvgTimePerQuestion()
    {
        int totalTime = 0;
        for (Integer eachTime : timePerQuestion)
        {
            totalTime += eachTime;
        }

        if(timePerQuestion.size() == 0)
            return 0;

        return totalTime / timePerQuestion.size();
    }

    private static Random random = new Random();
    private TextView mBackButton;

    private long questionStartTime;

    private static TextView mTimeLabel;
    private static Handler mHandler = new Handler();
    private static long mStart;
    private static long elapsed;
    private static boolean outOfTime = false;

    private Question currentQuestion;
    private static long resumeTime;
    private static Runnable updateTask = new Runnable() {
        public void run() {

                long now = SystemClock.uptimeMillis();
                elapsed = duration - (now - mStart);
                resumeTime = elapsed;

                if (elapsed > 0) {
                    int seconds = (int) (elapsed / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;

                    if (mTimeLabel != null) {
                        if (seconds < 10) {
                            mTimeLabel.setText("" + minutes + ":0" + seconds);
                        } else {
                            mTimeLabel.setText("" + minutes + ":" + seconds);
                        }
                    }

                    mHandler.postAtTime(this, now + 1000);
                } else {
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
        if(savedInstanceState != null)
        {
            mStart = savedInstanceState.getLong("mStart");
            elapsed = savedInstanceState.getLong("elapsed");
            outOfTime = savedInstanceState.getBoolean("outOfTime");
            questionStartTime = savedInstanceState.getLong("questionStartTime");
            correctAnswerResId = savedInstanceState.getInt("correctAnswerResId");
            currentQuestion = savedInstanceState.getParcelable("currentQuestion");
            numOfCorrectAnswers = savedInstanceState.getInt("numOfCorrectAnswers");
            numOfIncorrectAnswers = savedInstanceState.getInt("numOfIncorrectAnswers");
            timePerQuestion = savedInstanceState.getIntegerArrayList("timePerQuestion");
        }

        try
        {
            db = new DatabaseHandler(this);
            db.createDatabase();
        }catch(IOException e)
        {
            throw new Error("Unable to create database.");
        }
        try
        {
            db.openDataBase();
        }
        catch(SQLException e)
        {

        }

        Question question = null;
        if(currentQuestion == null) {
            question = generateQuestion();
            currentQuestion = question;
        }
        else
        {
            question = currentQuestion;
        }

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
                    db.close();
                    outOfTime = false;
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
    public  void onPause()
    {
        super.onPause();
        mHandler.removeCallbacks(updateTask);

    }

    @Override
    public void onResume()
    {
        super.onResume();
        mStart = SystemClock.uptimeMillis() + elapsed - duration;
        mHandler.post(updateTask);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(), MainActivity.class));
    }



    private Question generateQuestion()
    {
        // and replaces the X's and Y's with the appropriate data.
        int randomQuestionIndex = random.nextInt(allQuestionTemplates.size());
        //int randomQuestionIndex = 8;
        switch (randomQuestionIndex)
        {
            //"Who directed the movie X?"
            case 0:
                // Server calls? Don't forget break;
                String[] Q1 = db.getQuestion1Answers();
                String Question1 = allQuestionTemplates.get(randomQuestionIndex).question.replace("X", Q1[0]);
                return new Question(Question1,
                        Q1[1], new String[]{Q1[2], Q1[3], Q1[4], Q1[5]});
           //"When was the movie X released?"
            case 1:
                String[] Q2 = db.getQuestion2Answers();
                String Question2 = allQuestionTemplates.get(randomQuestionIndex).question.replace("X", Q2[0]);
                return new Question(Question2,
                       Q2[1], new String[]{Q2[2], Q2[3], Q2[4], Q2[5]});

            // "Which star was in the movie X?"
            case 2:
                String[] Q3 = db.getQuestion3Answers();
                String Question3 = allQuestionTemplates.get(randomQuestionIndex).question.replace("X", Q3[0]);
                return new Question(Question3,
                        Q3[1], new String[]{Q3[2], Q3[3], Q3[4], Q3[5]});
            // "Which star wasn't in the movie X?"
            case 3:
                String[] Q4 = db.getQuestion4Answers();
                String Question4 = allQuestionTemplates.get(randomQuestionIndex).question.replace("X", Q4[0]);
                return new Question(Question4,
                        Q4[1], new String[]{ Q4[2], Q4[3], Q4[4], Q4[5]});

           //"In which movie did the stars X and Y appear together?"
            case 4:
                String[] Q5 = db.getQuestion5Answers();
                String Question5 = allQuestionTemplates.get(randomQuestionIndex).question.replace("X", Q5[0]);
                Question5 = Question5.replace("Y", Q5[1]);
                return new Question(Question5,
                        Q5[2], new String[]{Q5[3], Q5[4], Q5[5], Q5[5]});

            //"Who directed the star X?"
            case 5:
                String[] Q6 = db.getQuestion6Answers();
                String Question6 = allQuestionTemplates.get(randomQuestionIndex).question.replace("X", Q6[0]);
                return new Question(Question6,
                        Q6[1], new String[]{Q6[2], Q6[3], Q6[4], Q6[5]});
            //"Who didn't direct the star X?"
            case 6:
                String[] Q7 = db.getQuestion7Answers();
                String Question7 = allQuestionTemplates.get(randomQuestionIndex).question.replace("X", Q7[0]);
                return new Question(Question7,
                        Q7[1], new String[]{Q7[2], Q7[3], Q7[4], Q7[5]});

           //"Which star appears in both movies X and Y?"
            case 7:
                String[] Q8 = db.getQuestion8Answers();
                String Question8 = allQuestionTemplates.get(randomQuestionIndex).question.replace("X", Q8[0]);
                Question8 = Question8.replace("Y", Q8[1]);
                return new Question(Question8,
                        Q8[2], new String[]{Q8[3], Q8[4], Q8[5], Q8[6]});

            //"Which star did not appear in the same movie with star X?"
            case 8:
                String[] Q9 = db.getQuestion9Answers();
                String Question9 = allQuestionTemplates.get(randomQuestionIndex).question.replace("X", Q9[0]);
                return new Question(Question9,
                        Q9[1], new String[]{Q9[2], Q9[3], Q9[4], Q9[5]});
            //"Who directed the star X in year Y?"
            case 9:
                String[] Q10 = db.getQuestion10Answers();
                String Question10 = allQuestionTemplates.get(randomQuestionIndex).question.replace("X", Q10[0]);
                Question10 = Question10.replace("Y", Q10[1]);
                return new Question(Question10,
                        Q10[2], new String[]{Q10[3], Q10[4], Q10[5], Q10[6]});
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
//        private static TextView mTimeLabel;
//        private static Handler mHandler = new Handler();
//        private static long mStart;
//        private static long elapsed;
//        private static final long duration = 180000;
//        private static boolean outOfTime = false;
        //        public static int numOfCorrectAnswers;
//        public static int numOfIncorrectAnswers;
//        private static ArrayList<Integer> timePerQuestion = new ArrayList<>();
//        private Timer checkForOutOfTimeTimer;
        savedInstanceState.putLong("mStart", mStart);
        savedInstanceState.putLong("elapsed", elapsed);
        savedInstanceState.putBoolean("outOfTime", outOfTime);
        savedInstanceState.putLong("questionStartTime", questionStartTime);
        savedInstanceState.putInt("correctAnswerResId", correctAnswerResId);
        savedInstanceState.putParcelable("currentQuestion", currentQuestion);
        savedInstanceState.putInt("numOfCorrectAnswers", numOfCorrectAnswers);
        savedInstanceState.putInt("numOfIncorrectAnswers", numOfIncorrectAnswers);
        savedInstanceState.putIntegerArrayList("timePerQuestion", timePerQuestion);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        mStart = savedInstanceState.getLong("mStart");
        elapsed = savedInstanceState.getLong("elapsed");
        outOfTime = savedInstanceState.getBoolean("outOfTime");
        questionStartTime = savedInstanceState.getLong("questionStartTime");
        correctAnswerResId = savedInstanceState.getInt("correctAnswerResId");
        currentQuestion = savedInstanceState.getParcelable("currentQuestion");
        numOfCorrectAnswers = savedInstanceState.getInt("numOfCorrectAnswers");
        numOfIncorrectAnswers = savedInstanceState.getInt("numOfIncorrectAnswers");
        timePerQuestion = savedInstanceState.getIntegerArrayList("timePerQuestion");

    }


}

