package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "employees.db";
    private static final String KEY_SURNAME = "surname";
    private static final String KEY_NAME = "name";
    private static final String KEY_THIRDNAME = "thirdname";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ID = "id";
    private static final String KEY_TEACHER_ID="teacher_id";
    private static final String KEY_OWNER_ID="owner_id";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_JOB = "job";

    private static final String EMPLOYEES_TABLE_NAME = "Employees";
    private static final String COMMENTS_TABLE_NAME = "Comments";

    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static String getDatabaseCorrectString(String field)
    {
        return "'" + field + "'";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try
        {
            sqLiteDatabase.execSQL(getCreateEmployeesTableQuery());
            sqLiteDatabase.execSQL(getCreateCommentsTableQuery());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public void addNewEmployee(Employee employee) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, employee.getEmail());
        values.put(KEY_SURNAME, employee.getSurname());
        values.put(KEY_NAME, employee.getName());
        values.put(KEY_THIRDNAME, employee.getThirdName());
        values.put(KEY_JOB, employee.getJob());
        values.put(KEY_PASSWORD, employee.getPassword());

        database.insert(EMPLOYEES_TABLE_NAME, null, values);
        database.close();
    }

    public User getUser(String email)
    {
        SQLiteDatabase database = this.getReadableDatabase();

        String rawQuery = "SELECT " + KEY_ID + ", " + KEY_JOB + " FROM " + EMPLOYEES_TABLE_NAME + " WHERE " +
                KEY_EMAIL + "=" + email;

        Cursor userCursor = database.rawQuery(rawQuery, null);

        User user = null;

        if (userCursor.moveToFirst())
        {
            do
            {
                user = new User(Integer.parseInt(userCursor.getString(0)), userCursor.getString(1));
            } while(userCursor.moveToNext());
        }

        this.freeResources(userCursor, database);
        return user;
    }

    public void addComment(String comment, int ownerId, int teacherId)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TEACHER_ID, teacherId);
        values.put(KEY_OWNER_ID, ownerId);
        values.put(KEY_COMMENT, comment);

        database.insert(COMMENTS_TABLE_NAME,  null, values);
        database.close();
    }

    public void deleteComment(int id)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(COMMENTS_TABLE_NAME, KEY_ID + "=?", new String[] {String.valueOf(id)});

        database.close();
    }

    public void updateComment(int id, String newComment)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COMMENT, newComment);

        database.update(COMMENTS_TABLE_NAME, values, KEY_ID + "=?", new String[] {String.valueOf(id)});

        database.close();
    }

    public ArrayList<CommentsContext> getTeachersCommentsList(int teacherId)
    {
        ArrayList<CommentsContext> teacherComments = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor teacherCursor = database.query(COMMENTS_TABLE_NAME, new String[] {KEY_ID, KEY_OWNER_ID, KEY_COMMENT},
                KEY_TEACHER_ID + "=?", new String[] {String.valueOf(teacherId)},
                null, null, null, null);

        if (teacherCursor.moveToFirst())
        {
            do
            {
                teacherComments.add(new CommentsContext(Integer.parseInt(teacherCursor.getString(0)),
                        Integer.parseInt(teacherCursor.getString(1)),
                        teacherCursor.getString(2)));
            } while (teacherCursor.moveToNext());
        }

        this.freeResources(teacherCursor, database);
        return teacherComments;
    }

    public ArrayList<Teacher> getTeachersList(String teacherJobName)
    {
        ArrayList<Teacher> teacherArrayList = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor teachersCursor = database.query(EMPLOYEES_TABLE_NAME,
                new String[] {KEY_ID, KEY_SURNAME, KEY_NAME, KEY_THIRDNAME},
                KEY_JOB + "=?", new String[] {teacherJobName}, null, null,
                null, null);

        if (teachersCursor.moveToFirst())
        {
            do
            {
                teacherArrayList.add(new Teacher(
                        Integer.parseInt(teachersCursor.getString(0)),
                        teachersCursor.getString(1),
                        teachersCursor.getString(2),
                        teachersCursor.getString(3)
                ));
            } while (teachersCursor.moveToNext());
        }

        this.freeResources(teachersCursor, database);
        return teacherArrayList;
    }

    public int getMaxEmployeeId()
    {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT " + "MAX" + "(" + KEY_ID + ")" + " FROM " + EMPLOYEES_TABLE_NAME + ";";
        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();

        int maxId = Integer.parseInt(cursor.getString(0));
        this.freeResources(cursor, database);

        return maxId;
    }

    public int getMaxCommentsId()
    {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT " + "MAX" + "(" + KEY_ID + ")" + " FROM " + COMMENTS_TABLE_NAME + ";";
        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();

        int maxId = Integer.parseInt(cursor.getString(0));
        this.freeResources(cursor, database);

        return maxId;
    }

    public boolean isUserEmailExists(String email) {
        SQLiteDatabase database = this.getReadableDatabase();

        String rawQuery = "SELECT " + KEY_EMAIL + " FROM " + EMPLOYEES_TABLE_NAME + " WHERE " +
                KEY_EMAIL + "=" + email;

        Cursor employeesCursor = database.rawQuery(rawQuery, null);

        if (!employeesCursor.moveToFirst()) {
            return false;
        }

        this.freeResources(employeesCursor, database);
        return true;
    }

    public boolean isUserFIOExists(String surname, String name, String thirdName) {
        SQLiteDatabase database = this.getReadableDatabase();

        String rawQuery = "SELECT " + KEY_SURNAME + ", " + KEY_NAME + ", " + KEY_THIRDNAME + " FROM " +
                EMPLOYEES_TABLE_NAME +
                " WHERE " + KEY_SURNAME + "=" + surname + " AND " + KEY_NAME + "=" + name + " AND "
                + KEY_THIRDNAME + "=" + thirdName;

        Cursor cursor = database.rawQuery(rawQuery, null);

        if (!cursor.moveToFirst()) {
            return false;
        }

        this.freeResources(cursor, database);
        return true;
    }

    public boolean isPasswordValid(String password, String email) {
        SQLiteDatabase database = this.getReadableDatabase();

        String query = "SELECT " + KEY_EMAIL + ", " + KEY_PASSWORD + " FROM " + EMPLOYEES_TABLE_NAME +
                " WHERE " + KEY_EMAIL + " = " + email + " AND " + KEY_PASSWORD + " = " + password;

        Cursor employeesCursor = database.rawQuery(query, null);

        if (!employeesCursor.moveToFirst()) {
            return false;
        }

        this.freeResources(employeesCursor, database);
        return true;
    }

    private String getCreateEmployeesTableQuery()
    {
        return "create table if not exists " + EMPLOYEES_TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_SURNAME + " TEXT NOT NULL," +
                KEY_NAME + " TEXT NOT NULL," +
                KEY_THIRDNAME + " TEXT NOT NULL," +
                KEY_JOB + " TEXT NOT NULL," +
                KEY_EMAIL + " TEXT NOT NULL," +
                KEY_PASSWORD + " TEXT NOT NULL" + ");";
    }

    private String getCreateCommentsTableQuery()
    {
        return "create table if not exists " + COMMENTS_TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_TEACHER_ID + " INTEGER NOT NULL," +
                KEY_OWNER_ID + " INTEGER NOT NULL," +
                KEY_COMMENT + " TEXT NOT NULL,"
                + "FOREIGN KEY " + "(" + KEY_TEACHER_ID + ")" + " REFERENCES " +
                EMPLOYEES_TABLE_NAME + "(" + KEY_ID + "),"
                + "FOREIGN KEY " + "(" + KEY_OWNER_ID + ")" + " REFERENCES " +
                EMPLOYEES_TABLE_NAME + "(" + KEY_ID + ")" + ");";
    }

    private void freeResources(Cursor cursor, SQLiteDatabase database)
    {
        cursor.close();
        database.close();
    }
}