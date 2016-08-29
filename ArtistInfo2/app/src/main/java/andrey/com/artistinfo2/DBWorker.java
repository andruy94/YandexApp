package andrey.com.artistinfo2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Андрей on 01.03.2016.
 */
public class DBWorker {// методы для работы с бд
    public static String DBName = "ArtistDB";
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBWorker(Context context) {
        this.dbHelper = new DBHelper(context, DBName);
    }

    public void open() throws SQLException {
        this.db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null && !db.isOpen())
            db.close();
    }

    public long insertPic(ArtistDscr artistDscr) {
        ContentValues cv=new ContentValues();
        cv.put(DBHelper.TableColumn[0],artistDscr.id);
        cv.put(DBHelper.TableColumn[1],artistDscr.name);
        //cv.put(DBHelper.TableColumn[2],artistDscr.genres );
        cv.put(DBHelper.TableColumn[3],artistDscr.tracks);
        cv.put(DBHelper.TableColumn[4],artistDscr.albums);
        cv.put(DBHelper.TableColumn[5],artistDscr.link);
        cv.put(DBHelper.TableColumn[6],artistDscr.description);
        cv.put(DBHelper.TableColumn[7], artistDscr.cover.getSmallcover());
        cv.put(DBHelper.TableColumn[8], artistDscr.cover.getBigcover());
            return db.insert(DBHelper.TableName, null, cv);

    }

    public long updatePic( ArtistDscr artistDscr) {
        ContentValues cv=new ContentValues();
        cv.put(DBHelper.TableColumn[0],artistDscr.id);
        cv.put(DBHelper.TableColumn[1],artistDscr.name);
        //cv.put(DBHelper.TableColumn[2],artistDscr.genres );
        cv.put(DBHelper.TableColumn[3],artistDscr.tracks);
        cv.put(DBHelper.TableColumn[4],artistDscr.albums);
        cv.put(DBHelper.TableColumn[5],artistDscr.link);
        cv.put(DBHelper.TableColumn[6],artistDscr.description);
        cv.put(DBHelper.TableColumn[7], artistDscr.cover.getSmallcover());
        cv.put(DBHelper.TableColumn[8], artistDscr.cover.getBigcover());
        return db.update(DBHelper.TableName, cv, DBHelper.TableColumn[0] + "= ?",
                new String[]{artistDscr.id+""});
    }


    public List<ArtistDscr> GetAllArtist(){
        List<ArtistDscr> artistDscrList=new ArrayList<>();
        Cursor c = db.query(DBHelper.TableName,null , null, null, null, null, DBHelper.TableColumn[0]+" DESC");
        if(c.moveToFirst()) {
            do {
                ArtistDscr artistDscr=new ArtistDscr();
                int filenameColIndex = c.getColumnIndex(DBHelper.TableColumn[0]);
                artistDscr.id = c.getInt(filenameColIndex);
                artistDscr.name= c.getString(c.getColumnIndex(DBHelper.TableColumn[1]));
               // artistDscr.genres=c.getString(c.getColumnIndex(DBHelper.TableColumn[2]));
                artistDscr.albums=c.getInt(c.getColumnIndex(DBHelper.TableColumn[3]));
                artistDscr.tracks=c.getInt(c.getColumnIndex(DBHelper.TableColumn[4]));
                artistDscr.link=c.getString(c.getColumnIndex(DBHelper.TableColumn[5]));
                artistDscr.description=c.getString(c.getColumnIndex(DBHelper.TableColumn[6]));
                artistDscr.cover.setSmallcover(c.getString(c.getColumnIndex(DBHelper.TableColumn[7])));
                artistDscr.cover.setBigcover(c.getString(c.getColumnIndex(DBHelper.TableColumn[8])));
                artistDscrList.add(artistDscr);
            }while (c.moveToNext());
        }else {
            Log.d(MainActivity.TAG, "cursor=null");
        }
        return artistDscrList;
    }

    static public class DBHelper extends SQLiteOpenHelper {
        public static String[] TableColumn = {
                "id",
                "name",
                "genres",
                "tracks",
                "albums",
                "link",
                "description",
                "smallcover",
                "bigcover",
        };

        public static String TableName = "TArtistDsrc";

        public DBHelper(Context context, String DBName) {
            // конструктор суперкласса
            super(context, DBName, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // создаем таблицу с полями
            db.execSQL("create table " + TableName + " ("
                    + TableColumn[0] + " INTEGER primary key,"
                    + TableColumn[1] + " text,"
                    + TableColumn[2] + " text, "
                    + TableColumn[3] + " INTEGER,"
                    + TableColumn[4] + " INTEGER,"
                    + TableColumn[5] + " text,"
                    + TableColumn[6] + " text,"
                    + TableColumn[7] + " text,"
                    + TableColumn[8] + " text );");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
