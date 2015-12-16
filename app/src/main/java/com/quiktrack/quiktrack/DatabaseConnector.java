package com.quiktrack.quiktrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Created by Goudam on 12/16/15.
 */

// DatabaseConnector.java
// Provides easy connection and creation of UserTags database.
public class DatabaseConnector
{
    // database name
    private static final String DATABASE_NAME = "UserTags";
    private SQLiteDatabase database; // database object
    private DatabaseOpenHelper databaseOpenHelper; // database helper

    // public constructor for DatabaseConnector
    public DatabaseConnector(Context context)
    {
        // create a new DatabaseOpenHelper
        databaseOpenHelper =
                new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    } // end DatabaseConnector constructor

    // open the database connection
    public void open() throws SQLException
    {
        // create or open a database for reading/writing
        database = databaseOpenHelper.getWritableDatabase();
    } // end method open

    // close the database connection
    public void close()
    {
        if (database != null)
            database.close(); // close the database connection
    } // end method close

    // inserts a new tag in the database
    public void insertTag(String tag, String item, String path)
    {
        ContentValues newTag = new ContentValues();
        newTag.put("tag", tag);
        newTag.put("item", item);
        newTag.put("path", path);

        open(); // open the database
        database.insert("tags", null, newTag);
        close(); // close the database
    } // end method insertTag

    // inserts a new tag in the database
    public void updateTag(long id, String tag, String item, String path)
    {
        ContentValues editTag = new ContentValues();
        editTag.put("tag", tag);
        editTag.put("item", item);
        editTag.put("path", path);

        open(); // open the database
        database.update("tags", editTag, "_id=" + id, null);
        close(); // close the database
    } // end method updateTag

    // return a Cursor with all tag information in the database
    public Cursor getAllTags()
    {
        return database.query("tags", new String[] {"_id", "tag"},
                null, null, null, null, "tag");
    } // end method getAllTags

    // get a Cursor containing all information about the tag specified
    // by the given id
    public Cursor getOneTag(String tag)
    {
        return database.query(
                "tags", null, "tag=" + tag, null, null, null, null);
    } // end method getOneTag

    // delete the tag specified by the given String name
    public void deleteTag(String tag)
    {
        open(); // open the database
        database.delete("tags", "tag=" + tag, null);
        close(); // close the database
    } // end method deleteTag

    private class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        // public constructor
        public DatabaseOpenHelper(Context context, String name,
                                  CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        } // end DatabaseOpenHelper constructor

        // creates the tags table when the database is created
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            // query to create a new table named tags
            String createQuery = "CREATE TABLE tags" +
                    "(_id integer primary key autoincrement," +
                    "tag TEXT, item TEXT, path TEXT);";
            db.execSQL(createQuery); // execute the query
        } // end method onCreate

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
        } // end method onUpgrade
    } // end class DatabaseOpenHelper
} // end class DatabaseConnector
