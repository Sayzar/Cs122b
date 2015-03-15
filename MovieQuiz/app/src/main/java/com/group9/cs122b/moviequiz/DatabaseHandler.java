package com.group9.cs122b.moviequiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by Cesar Ramirez on 3/13/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movieDatabase";
    private static final String TABLE_MOVIES = "movies";
    private static final String TABLE_STARS = "stars";
    private static final String TABLE_STARSMOVIES = "stars_in_movies";

    private DatabaseHandler db;
    private static String DB_PATH = "/data/data/com.group9.cs122b.moviequiz/databases/";
    private static String DB_NAME = "movies";
    private SQLiteDatabase dataBase;
    private final Context context;
    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    public void createDatabase() throws IOException{
        boolean dbExists = checkDataBase();
        if(dbExists) {
            //do nothing -db already exists
        }
         else{
            this.getReadableDatabase();
            try
            {
                copyDataBase();
            }catch(IOException e)
            {
                throw new Error("Error copying data");
            }

        }
    }

    private void copyDataBase() throws IOException{
        InputStream is = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;

        OutputStream os = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outpute file
        byte[] bugger = new byte[1024];
        int length;
        while((length = is.read(bugger))>0)
        {
            os.write(bugger, 0, length);
        }
        os.flush();
        os.close();
        is.close();
    }
    private boolean checkDataBase()
    {
        SQLiteDatabase checkDB = null;
        try
        {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.OPEN_READONLY);
        } catch(SQLiteException e){
            //database doesnt exist yet
        }

        if(checkDB != null)
            checkDB.close();

        return checkDB != null ? true : false;
    }

    public void openDataBase() throws SQLException{
        String path = DB_PATH+DB_NAME;
        dataBase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
    }
    @Override
    public synchronized void close()
    {
        if(dataBase!=null)
            dataBase.close();
        super.close();
    }


    //[0] title
    //[1] right director
    //[2-4] wrong
    public String[] getQuestion1Answers()
    {
        String query = "SELECT title, director FROM movies ORDER BY RANDOM() LIMIT 1";
        String[] results = new String[6];
        String director = "";
        Cursor cursor = dataBase.rawQuery(query,null);
        if(cursor!= null)
        {      cursor.moveToFirst();
               results[0] = cursor.getString(0);
               results[1] = cursor.getString(1);
               director = results[1];
              cursor.close();
        }

        //get random 4 directors
        String dirQuery = "SELECT director FROM movies WHERE director !='"+director+"' ORDER BY RANDOM() LIMIT 4";
        cursor = dataBase.rawQuery(dirQuery,null);
        if(cursor!= null)
        {
                cursor.moveToFirst();
                results[2] = cursor.getString(0);
                cursor.moveToNext();
                results[3] = cursor.getString(0);
                cursor.moveToNext();
                results[4] = cursor.getString(0);
                cursor.moveToNext();
                results[5] = cursor.getString(0);
                 cursor.close();
        }
        return results;
    }

    public String[] getQuestion2Answers()
    {
        String query = "SELECT title, year FROM movies ORDER BY RANDOM() LIMIT 1";
        String[] results = new String[6];
        String year = "";
        Cursor cursor = dataBase.rawQuery(query,null);
        if(cursor!= null)
        {      cursor.moveToFirst();
            results[0] = cursor.getString(0);
            results[1] = cursor.getString(1);
            year = results[1];
            cursor.close();
        }

        //get random 4 years
        String dirQuery = "SELECT m.year FROM movies m WHERE m.year != '"+year+"' GROUP BY m.year ORDER BY RANDOM() LIMIT 4";
        cursor = dataBase.rawQuery(dirQuery,null);
        if(cursor!= null)
        {
            if(cursor.moveToFirst())
                results[2] = cursor.getString(0);
            if(cursor.moveToNext())
                results[3] = cursor.getString(0);
            if(cursor.moveToNext())
                results[4] = cursor.getString(0);
            if(cursor.moveToNext())
                results[5] = cursor.getString(0);
            cursor.close();
        }

        return results;
    }
    public String[] getQuestion3Answers()
    {
        String[] results = new String[6];
        Cursor cursor;
        String mID = "";

        String movieQuery = "SELECT title, first_name, last_name, m.id FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                    + "ON m.id = sm.movie_id AND sm.star_id = s.id) ORDER BY RANDOM() LIMIT 1";
            cursor = dataBase.rawQuery(movieQuery, null);
            if (cursor != null ) {
                if (cursor.moveToFirst()) {
                    results[0] = cursor.getString(0);
                    results[1] = cursor.getString(1) + " " + cursor.getString(2);
                    mID = cursor.getString(3);
                }
                cursor.close();
            }

        String wrongQuery = "SELECT  first_name, last_name FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE m.id != '" + mID + "' ORDER BY RANDOM() LIMIT 4";
        cursor = dataBase.rawQuery(wrongQuery, null);
        if(cursor != null)
        {
            if(cursor.moveToFirst())
                results[2] = cursor.getString(0) + " " + cursor.getString(1);
            if(cursor.moveToNext())
                results[3] = cursor.getString(0) + " " + cursor.getString(1);
            if(cursor.moveToNext())
                results[4] = cursor.getString(0) + " " + cursor.getString(1);
            if(cursor.moveToNext())
                results[5] = cursor.getString(0) + " " + cursor.getString(1);

            cursor.close();
        }
        return results;
    }
    public String[] getQuestion4Answers()
    {
        String[] results = new String[6];
        Cursor cursor;
        String mID = "";

        String movieQuery = "SELECT sm.movie_id, title FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) GROUP BY sm.movie_id HAVING count(*) >3 ORDER BY RANDOM() LIMIT 1";
        cursor = dataBase.rawQuery(movieQuery, null);
        if (cursor != null ) {

                 if (cursor.moveToFirst()) {
                     results[0] = cursor.getString(1);
                     mID = cursor.getString(0);
                 }
            cursor.close();
        }

        String starQuery = "SELECT first_name, last_name FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE sm.movie_id = '"+mID+"' GROUP BY sm.star_id ORDER BY RANDOM() LIMIT 4";

        cursor = dataBase.rawQuery(starQuery, null);
        if(cursor != null) {
            if (cursor.moveToFirst())
                results[2] = cursor.getString(0) + " " + cursor.getString(1);
            if (cursor.moveToNext())
                results[3] = cursor.getString(0) + " " + cursor.getString(1);
            if (cursor.moveToNext())
                results[4] = cursor.getString(0) + " " + cursor.getString(1);
            if (cursor.moveToNext())
                results[5] = cursor.getString(0) + " " + cursor.getString(1);

            cursor.close();
        }
        String wrongQuery = "SELECT first_name, last_name FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE sm.movie_id != '" + mID + "' ORDER BY RANDOM() LIMIT 1";
        cursor = dataBase.rawQuery(wrongQuery, null);
        if(cursor != null)
        {
            if (cursor.moveToFirst()) {
                    if(cursor.moveToFirst())
                    {
                        results[1] = cursor.getString(0) + " " + cursor.getString(1);
                    }
                cursor.close();
            }
        }
        return results;
    }
    public String[] getQuestion5Answers()
    {
        String[] results = new String[7];
        Cursor cursor;
        String mID = "";
        //"In which movie did the stars X and Y appear together?"
        String movieQuery = "SELECT sm.movie_id, title FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) GROUP BY sm.movie_id HAVING count(*) >2 ORDER BY RANDOM() LIMIT 1";
        cursor = dataBase.rawQuery(movieQuery, null);
        if(cursor != null)
        {
            if(cursor.moveToFirst())
            {
                mID = cursor.getString(0);
                results[2] = cursor.getString(1);
            }
            cursor.close();
        }

        String starQuery = "SELECT first_name, last_name FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE sm.movie_id = '"+mID+"' GROUP BY sm.star_id ORDER BY RANDOM() LIMIT 2";
        cursor = dataBase.rawQuery(starQuery, null);
        if(cursor != null)
        {
            if(cursor.moveToFirst())
                results[0] = cursor.getString(0) + " " + cursor.getString(1);
            if(cursor.moveToNext())
                results[1] = cursor.getString(0) + " " + cursor.getString(1);
            cursor.close();
        }


        String wrongQuery = "SELECT title FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE sm.movie_id != '" + mID + "' ORDER BY RANDOM() LIMIT 4";
        cursor = dataBase.rawQuery(wrongQuery, null);
        if(cursor != null)
        {
            if(cursor.moveToFirst())
                results[3] = cursor.getString(0);
            if(cursor.moveToNext())
                results[4] = cursor.getString(0);
            if(cursor.moveToNext())
                results[5] = cursor.getString(0);
            if(cursor.moveToNext())
                results[6] = cursor.getString(0);

            cursor.close();
        }
        return results;
    }
    public String[] getQuestion6Answers()
    {
        String[] results = new String[6];
        Cursor cursor;
        String mID = "";
        String sID = "";
        String movieQuery = "SELECT director, first_name, last_name, m.id, s.id FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) ORDER BY RANDOM() LIMIT 1";
        cursor = dataBase.rawQuery(movieQuery, null);
        if (cursor != null ) {
            if (cursor.moveToFirst()) {
                results[0] = cursor.getString(1) + " " + cursor.getString(2);
                results[1] = cursor.getString(0);
                mID = cursor.getString(3);
                sID = cursor.getString(4);
            }
            cursor.close();
        }

        String wrongQuery = "SELECT director FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE m.id != '" + mID + "' AND s.id != '" +sID+"' ORDER BY RANDOM() LIMIT 4";
        cursor = dataBase.rawQuery(wrongQuery, null);
        if(cursor != null)
        {
            if(cursor.moveToFirst())
                results[2] = cursor.getString(0);
            if(cursor.moveToNext())
                results[3] = cursor.getString(0);
            if(cursor.moveToNext())
                results[4] = cursor.getString(0);
            if(cursor.moveToNext())
                results[5] = cursor.getString(0);

            cursor.close();
        }
        return results;
    }
    public String[] getQuestion7Answers()
    {
        String[] results = new String[6];
        Cursor cursor;
        String sID = "";
        String movieQuery = "SELECT first_name, last_name, s.id, director FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) GROUP BY s.id HAVING count(*) >3 ORDER BY RANDOM() LIMIT 4";
        cursor = dataBase.rawQuery(movieQuery,null);
        if(cursor != null)
        {
            if(cursor.moveToFirst()) {
                results[0] = cursor.getString(0) + " " + cursor.getString(1);
                sID = cursor.getString(2);
                results[2] = cursor.getString(3);
            }
            if(cursor.moveToNext())
                results[3] = cursor.getString(3);
            if(cursor.moveToNext())
                results[4] = cursor.getString(3);
            if(cursor.moveToNext())
                results[5] = cursor.getString(3);
            cursor.close();

        }
        String wrongQuery = "SELECT director FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE sm.star_id != '" + sID + "' ORDER BY RANDOM() LIMIT 1";
        cursor = dataBase.rawQuery(wrongQuery, null);
        if(cursor != null)
        {
            if(cursor.moveToFirst())
                results[1] = cursor.getString(0);
            cursor.close();
        }
        return  results;

    }
    public String[] getQuestion8Answers()
    {   String[] results = new String[7];
        String sID = "";
        Cursor cursor;
        String movieQuery = "SELECT first_name, last_name, s.id, title FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) GROUP BY s.id HAVING count(*) > 2 ORDER BY RANDOM() LIMIT 2";
        cursor = dataBase.rawQuery(movieQuery, null);
        if(cursor != null)
        {
            if(cursor.moveToFirst()) {
                results[0] = cursor.getString(3);
                results[2] = cursor.getString(0) + " " + cursor.getString(1);
                sID = cursor.getString(2);
            }
            if(cursor.moveToNext())
            {
                results[1] = cursor.getString(3);
            }
            cursor.close();
        }
        String wrongQuery = "SELECT first_name, last_name FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE sm.star_id != '" + sID + "' ORDER BY RANDOM() LIMIT 4";
        cursor = dataBase.rawQuery(wrongQuery, null);
        if(cursor != null)
        {
            if(cursor.moveToFirst())
                results[3] = cursor.getString(0) + " " + cursor.getString(1);
            if(cursor.moveToNext())
                results[4] = cursor.getString(0) + " " + cursor.getString(1);
            if(cursor.moveToNext())
                results[5] = cursor.getString(0) + " " + cursor.getString(1);
            if(cursor.moveToNext())
                results[6] = cursor.getString(0) + " " + cursor.getString(1);
            cursor.close();
        }

        return results;
    }
    public String[] getQuestion9Answers()
    {   String[] results = new String[6];
        Cursor cursor;
        String mID = "";
        String sID = "";
        String movieQuery = "SELECT m.id FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) GROUP BY sm.movie_id HAVING count(*) >4 ORDER BY RANDOM() LIMIT 1";
        cursor = dataBase.rawQuery(movieQuery, null);
        if(cursor != null)
        {
            if(cursor.moveToFirst())
                mID = cursor.getString(0);

            cursor.close();
        }

        String starQuery = "SELECT first_name, last_name, s.id FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE sm.movie_id = '"+ mID + "' ORDER BY RANDOM() LIMIT 5";
        cursor = dataBase.rawQuery(starQuery, null);
        if(cursor != null)
        {
            if(cursor.moveToFirst()) {
                sID = cursor.getString(2);
                results[0] = cursor.getString(0) + " " + cursor.getString(1);
            }
            if(cursor.moveToNext())
                 results[2] = cursor.getString(0) + " " + cursor.getString(1);
            if(cursor.moveToNext())
                results[3] = cursor.getString(0) + " " + cursor.getString(1);
            if(cursor.moveToNext())
                 results[4] = cursor.getString(0) + " " + cursor.getString(1);
             if(cursor.moveToNext())
                results[5] = cursor.getString(0) + " " + cursor.getString(1);
            cursor.close();
        }


        String wrongQuery = "SELECT first_name, last_name FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE sm.movie_id != '" + mID + "' AND sm.star_id != '" + sID+"' ORDER BY RANDOM() LIMIT 1";
        cursor = dataBase.rawQuery(wrongQuery, null);
        if(cursor != null)
        {
            if(cursor.moveToFirst())
            {
                results[1] = cursor.getString(0) + " " + cursor.getString(1);
                cursor.close();
            }
        }
        return results;
    }
    public String[] getQuestion10Answers()
    {
        String[] results = new String[7];
        Cursor cursor;
        String mID = "";
        String sID = "";

        String movieQuery = "SELECT director, first_name, last_name, m.id, s.id, year FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) ORDER BY RANDOM() LIMIT 1";
        cursor = dataBase.rawQuery(movieQuery, null);
        if (cursor != null ) {
            if (cursor.moveToFirst()) {
                results[0] = cursor.getString(1) + " " + cursor.getString(2);
                results[1] = cursor.getString(5);
                results[2] = cursor.getString(0);
                mID = cursor.getString(3);
                sID = cursor.getString(4);
            }
            cursor.close();
        }

        String wrongQuery = "SELECT director FROM (movies m JOIN stars_in_movies sm " + "JOIN stars s "
                + "ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE m.id != '" + mID + "' AND s.id != '" + sID +"' ORDER BY RANDOM() LIMIT 4";
        cursor = dataBase.rawQuery(wrongQuery, null);
        if(cursor != null)
        {
            if(cursor.moveToFirst())
                results[3] = cursor.getString(0);
            if(cursor.moveToNext())
                results[4] = cursor.getString(0);
            if(cursor.moveToNext())
                results[5] = cursor.getString(0);
            if(cursor.moveToNext())
                results[6] = cursor.getString(0);

            cursor.close();
        }
        return results;
    }



}
