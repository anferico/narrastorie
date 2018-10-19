package it.unipi.di.sam.carriage.narrastorie;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StoriesDatabaseHelper extends SQLiteOpenHelper
{

    private static final String DB_NAME = "NarrastorieDB";
    private static final int DB_VERSION = 1;

    public static final String TABLE_STORIES = "Stories";

    public static final String STORY_ID = "_id";
    public static final String STORY_NAME = "StoryName";
    public static final String STORY_CHARACTERS = "StoryCharacters";
    public static final String STORY_FILEPATH = "StoryFilePath";

    public StoriesDatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE_STORIES = "" +
            "CREATE TABLE " + TABLE_STORIES +
            "(" +
                STORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                STORY_NAME + " TEXT," +
                STORY_CHARACTERS + " TEXT," +
                STORY_FILEPATH + " TEXT," +
                "CONSTRAINT story_name_unique UNIQUE (" + STORY_NAME + ")" +
            ")";

        db.execSQL(CREATE_TABLE_STORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    { }

    public Cursor getDefaultCursor()
    {
        return getWritableDatabase().query(
                StoriesDatabaseHelper.TABLE_STORIES,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

}
