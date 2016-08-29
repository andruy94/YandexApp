package andrey.com.artistinfo2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Андрей on 18.04.2016.
 */
public class ArtistDscr extends Object implements Comparable<ArtistDscr>,Parcelable {
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("genres")
    @Expose
    public List<String> genres;
    @SerializedName("tracks")
    @Expose
    public int tracks;
    @SerializedName("albums")
    @Expose
    public int albums;
    @SerializedName("link")
    @Expose
    public String link;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("cover")
    @Expose
    public Cover cover;

    public ArtistDscr(){
    }

    @Override
    public String toString() {//для вывода значений в лог
        return id+'_'+name+'_'+genres.toString()+albums+' '+cover.getSmallcover();
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
        dest.writeString(genres.get(0));
        dest.writeInt(tracks);
        dest.writeInt(albums);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeString(cover.getSmallcover());
        dest.writeString(cover.getSmallcover());


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
        genres=new ArrayList<>();
        genres.add(parcel.readString());
        tracks= parcel.readInt();
        albums= parcel.readInt();
        link= parcel.readString();
        description=parcel.readString();
        cover=new Cover(parcel.readString(),parcel.readString());


    }
}
