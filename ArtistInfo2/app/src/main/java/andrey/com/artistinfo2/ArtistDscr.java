package andrey.com.artistinfo2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Андрей on 18.04.2016.
 */
public class ArtistDscr extends Object implements Comparable<ArtistDscr>,Parcelable {
    public int id;
    public String name;
    public String genres;
    public int tracks;
    public int albums;
    public String link;
    public String description;
    public String smallcover;
    public String bigcover;

    public ArtistDscr(){
    }

    @Override
    public String toString() {//для вывода значений в лог
        return id+'_'+name+'_'+genres.toString()+albums+' '+smallcover;
    }

    @Override
    public int compareTo(ArtistDscr another) {// чтоб сортировать наш список
        return this.id-another.id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(genres);
        dest.writeInt(tracks);
        dest.writeInt(albums);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeString(smallcover);
        dest.writeString(bigcover);


    }

    public static final Parcelable.Creator<ArtistDscr> CREATOR = new Parcelable.Creator<ArtistDscr>() {
        // распаковываем объект из Parcel
        public ArtistDscr createFromParcel(Parcel in) {
            return new ArtistDscr(in);
        }

        public ArtistDscr[] newArray(int size) {
            return new ArtistDscr[size];
        }
    };
    // конструктор, считывающий данные из Parcel
    private ArtistDscr(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        genres=parcel.readString();
        tracks= parcel.readInt();
        albums= parcel.readInt();
        link= parcel.readString();
        description=parcel.readString();
        smallcover=parcel.readString();
        bigcover=parcel.readString();

    }
}
