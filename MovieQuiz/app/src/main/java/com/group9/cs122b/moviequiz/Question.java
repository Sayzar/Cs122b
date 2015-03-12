package com.group9.cs122b.moviequiz;

public class Question
{
    public String question;
    public String correctAnswer;
    public String[] wrongAnswers;

    public Question(String question, String correctAnswer, String[] wrongAnswers)
    {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.wrongAnswers = wrongAnswers;
    }
}
