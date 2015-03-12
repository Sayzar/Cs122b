package com.group9.cs122b.moviequiz;

import java.util.ArrayList;

public class QuestionTemplate
{
    public String question;
    public DataType[] questionDataTypes;
    public DataType answerDataType;

    public QuestionTemplate(String question,
                            DataType[] questionDataTypes,
                            DataType answerDataType)
    {
        this.question = question;
        this.questionDataTypes = questionDataTypes;
        this.answerDataType = answerDataType;
    }

    public enum DataType
    {
        MOVIE,
        STAR,
        YEAR,
        DIRECTOR
    }
}
