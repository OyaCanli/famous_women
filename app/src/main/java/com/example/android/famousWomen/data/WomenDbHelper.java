package com.example.android.famousWomen.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import com.example.android.famousWomen.data.WomenContract.WomenEntry;
import com.example.android.famousWomen.data.WomenContract.QuizEntry;
import com.example.android.famousWomen.model.QuizQuestion;
import com.example.android.famousWomen.model.Woman;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Oya on 13-03-18.
 */

public class WomenDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = WomenDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DB_NAME = "famous_women.db";

    //Database version. If you change the database schema, you must increment the database version.
    private static final int DB_VERSION = 1;

    //File path for the database
    private static String DB_PATH;

    private SQLiteDatabase db;

    private final Context mContext;

    //Constructor
    public WomenDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Create an empty database on the system and rewrite it with your own database.
    public void createDataBase() throws IOException {
        //First check whether database already exists
        boolean dbExist = checkDataBase();
        if(!dbExist){
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        DB_PATH = mContext.getFilesDir().getPath();
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //database doesn't exist yet.
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
        //Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(DB_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDatabase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public ArrayList<Woman> getWomenList(){
        ArrayList<Woman> womenList = new ArrayList<>();
        try {
            openDatabase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        Cursor cursor = db.query(
                WomenEntry.TABLE_NAME,   // The table to query
                null,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                WomenEntry.COLUMN_WOMAN_NAME);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(WomenEntry.COLUMN_WOMAN_NAME);
        int professionColumnIndex = cursor.getColumnIndex(WomenEntry.COLUMN_WOMAN_PROFESSION);
        int detailsColumnIndex = cursor.getColumnIndex(WomenEntry.COLUMN_WOMAN_DETAILS);
        int listImageColumnIndex = cursor.getColumnIndex(WomenEntry.COLUMN_LIST_IMAGE_ID);
        int portraitImageColumnIndex = cursor.getColumnIndex(WomenEntry.COLUMN_PORTRAIT_IMAGE_ID);
        int flagImageColumnIndex = cursor.getColumnIndex(WomenEntry.COLUMN_FLAG_IMAGE_ID);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            womenList.add(new Woman(cursor.getString(nameColumnIndex), cursor.getString(professionColumnIndex), cursor.getString(detailsColumnIndex),
                    cursor.getString(listImageColumnIndex), cursor.getString(portraitImageColumnIndex), cursor.getString(flagImageColumnIndex)));
            cursor.moveToNext();
        }
        cursor.close();
        if(db != null) db.close();
        return womenList;
    }

    public ArrayList<QuizQuestion> getQuestions(){
        ArrayList<QuizQuestion> questionList = new ArrayList<>();
        try {
            openDatabase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        Cursor cursor = db.query(
                QuizEntry.TABLE_NAME,   // The table to query
                null,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                "RANDOM()",
                "5");

        int questionColumnIndex = cursor.getColumnIndex(QuizEntry.COLUMN_QUESTION);
        int option1ColumnIndex = cursor.getColumnIndex(QuizEntry.COLUMN_OPTION1);
        int option2ColumnIndex = cursor.getColumnIndex(QuizEntry.COLUMN_OPTION2);
        int option3ColumnIndex = cursor.getColumnIndex(QuizEntry.COLUMN_OPTION3);
        int correctOptionColumnIndex = cursor.getColumnIndex(QuizEntry.COLUMN_CORRECT_OPTION);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            questionList.add(new QuizQuestion(cursor.getString(questionColumnIndex), cursor.getString(option1ColumnIndex), cursor.getString(option2ColumnIndex),
                    cursor.getString(option3ColumnIndex), cursor.getInt(correctOptionColumnIndex)));
            cursor.moveToNext();
        }
        cursor.close();
        if(db != null) db.close();
        return questionList;
    }
}
