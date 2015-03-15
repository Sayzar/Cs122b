package com.group9.cs122b.moviequiz;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable
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
    public int describeContents() {
        return 0;
    }

    /** save object in parcel */
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(question);
        out.writeString(correctAnswer);
        out.writeStringArray(wrongAnswers);
    }

    public static final Parcelable.Creator<Question> CREATOR
            = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    /** recreate object from parcel */
    private Question(Parcel in) {
        this.question = in.readString();
        this.correctAnswer = in.readString();
        this.wrongAnswers = in.createStringArray();
    }
}
