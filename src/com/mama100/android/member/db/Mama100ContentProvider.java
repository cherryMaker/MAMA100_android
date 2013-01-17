package com.mama100.android.member.db;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * @author anhua.yan
 * 2012-11-20
 * 
 */
public class Mama100ContentProvider extends ContentProvider {

    private static final String DATABASE_NAME = "mama100.db";

    private static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "com.mama100.android.db.Mama100ContentProvider";

    private static final UriMatcher sUriMatcher;

    private static HashMap<String, String> projectionMap;

    // URI MATCH： 全部消息查询
    private static final int MSGS = 1;

    // URI MATCH：具体某个消息查询
    private static final int MSGID = 2;

    
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // CREATE INCOME TABLE
            db.execSQL("CREATE TABLE " + MessageHistoryTable.TABLE_NAME + " (" + MessageHistoryTable.ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," 
            		+ MessageHistoryTable.MESSAGE_ID + " INTEGER," 
                    + MessageHistoryTable.MAMA100_ID + " INTEGER,"
                    + MessageHistoryTable.FROM + " TEXT,"
                    
                     /****************************************
         * 消息主体格式 --START
         ****************************************/
                    + MessageHistoryTable.TITLE + " TEXT," 
                    + MessageHistoryTable.SHORT_DESC + " TEXT," 
                    + MessageHistoryTable.READ_STATUS + " TEXT," 
                    + MessageHistoryTable.CREATED_TIME + " TEXT," 
                    + MessageHistoryTable.READ_TIME + " TEXT);"); 
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("LOG_TAG", "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");

            // 更新前先杀掉以前旧的表
            db.execSQL("DROP TABLE IF EXISTS " + MessageHistoryTable.TABLE_NAME);

            // 创建新的数据库表结构
            onCreate(db);
        }

    }

    private DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        // 初始化数据库帮助类
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;

        switch (sUriMatcher.match(uri)) {
            case MSGS:
                // 执行标准删除
                count = db.delete(MessageHistoryTable.TABLE_NAME, where, whereArgs);
                break;
            case MSGID:
                // 从传进来的path路径里获取对应的msgid
                String ssid = uri.getPathSegments().get(MessageHistoryTable.MSGID_PATH_POSITION);

                // HERE THE USER WANTS TO DELETE A SPECIFIC CITIZEN
                String finalWhere = MessageHistoryTable.ID + " = " + ssid;

                // IF USER SPECIFIES WHERE FILTER THEN APPEND TOGETHER
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }

                count = db.delete(MessageHistoryTable.TABLE_NAME, finalWhere, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MSGS:
                return MessageHistoryTable.CONTENT_TYPE;
            case MSGID:
                return MessageHistoryTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // ONLY GENERAL CITIZENS URI IS ALLOWED FOR INSERTS
        // DOESN'T MAKE SENSE TO SPECIFY A SINGLE CITIZEN FOR INSERTS
        if (sUriMatcher.match(uri) != MSGS) { throw new IllegalArgumentException("Unknown URI " + uri); }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(MessageHistoryTable.TABLE_NAME, MessageHistoryTable.TITLE, values);
        if (rowId > 0) {
            Uri citizenUri = ContentUris.withAppendedId(MessageHistoryTable.CONTENT_URI, rowId);

            // NOTIFY CONTEXT OF THE CHANGE
            getContext().getContentResolver().notifyChange(citizenUri, null);
            return citizenUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MessageHistoryTable.TABLE_NAME);
        
        switch (sUriMatcher.match(uri)) {
            case MSGS:
                qb.setProjectionMap(projectionMap);
                break;
            case MSGID:
                String ssid = uri.getPathSegments().get(MessageHistoryTable.MSGID_PATH_POSITION);
                qb.setProjectionMap(projectionMap);

                // APPEND WHERE FILTER FOR QUERYING BY SPECIFIC SSID
                qb.appendWhere(MessageHistoryTable.ID + "=" + ssid);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // REGISTERS NOTIFICATION LISTENER WITH GIVEN CURSOR
        // ALLOWS CURSOR TO KNOW WHEN UNDERLYING DATA HAS CHANGED
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case MSGS:
                // GENERAL UPDATE ON ALL CITIZENS
                count = db.update(MessageHistoryTable.TABLE_NAME, values, where, whereArgs);
                break;
            case MSGID:
                // FROM INCOMING URI GET SSID
                String ssid = uri.getPathSegments().get(MessageHistoryTable.MSGID_PATH_POSITION);

                // HERE THE USER WANTS TO UPDATE A SPECIFIC CITIZEN
                String finalWhere = MessageHistoryTable.ID + " = " + ssid;

                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }

                // PERFORM THE UPDATE ON THE SPECIFIC CITIZEN
                count = db.update(MessageHistoryTable.TABLE_NAME, values, finalWhere, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    // INSTANTIATE AND SET STATIC VARIABLES
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "msg", MSGS); //一批消息
        sUriMatcher.addURI(AUTHORITY, "msg/#", MSGID); //具体一个消息

        // PROJECTION MAP USED FOR ROW ALIAS
        projectionMap = new HashMap<String, String>();
        projectionMap.put(MessageHistoryTable.ID, MessageHistoryTable.ID);
        projectionMap.put(MessageHistoryTable.MESSAGE_ID, MessageHistoryTable.MESSAGE_ID);
        projectionMap.put(MessageHistoryTable.MAMA100_ID, MessageHistoryTable.MAMA100_ID);
        projectionMap.put(MessageHistoryTable.FROM, MessageHistoryTable.FROM);
        
        /****************************************
         * 消息主体格式 --START
         ****************************************/
        projectionMap.put(MessageHistoryTable.TITLE, MessageHistoryTable.TITLE);
        projectionMap.put(MessageHistoryTable.SHORT_DESC, MessageHistoryTable.SHORT_DESC);
        projectionMap.put(MessageHistoryTable.READ_STATUS, MessageHistoryTable.READ_STATUS);
        projectionMap.put(MessageHistoryTable.CREATED_TIME, MessageHistoryTable.CREATED_TIME);
        projectionMap.put(MessageHistoryTable.READ_TIME, MessageHistoryTable.READ_TIME);
        
        
    }
}
