package andrey.com.artistinfo2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by Андрей on 19.04.2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{


    public static class PersonViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView genres;
        TextView albums;
        ImageView photo;
        ProgressBar progressBar;
        LinearLayout linearLayout;

        PersonViewHolder(View itemView) {// находим все элементы
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name);
            genres = (TextView)itemView.findViewById(R.id.genres);
            photo = (ImageView)itemView.findViewById(R.id.photo);
            albums=(TextView)itemView.findViewById(R.id.albums);
            linearLayout=(LinearLayout)  itemView.findViewById(R.id.LL1);
            progressBar=(ProgressBar) itemView.findViewById(R.id.progressBar);

        }
    }

    protected Context context;
    protected List<ArtistDscr> artistDscrList ;// массив наших даных

    RVAdapter(List<ArtistDscr> artistDscrList,Context context){
        this.artistDscrList = artistDscrList;
        this.context=context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, final int i) {
        personViewHolder.name.setText(artistDscrList.get(i).name);
        personViewHolder.albums.setText(String.format(context.getString(R.string.alb),
                artistDscrList.get(i).albums, artistDscrList.get(i).tracks));
        personViewHolder.genres.setText(artistDscrList.get(i).genres);
        personViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, infoAcivity.class);
                intent.putExtra("Extra", artistDscrList.get(i));//упаковываем объект (Parceble) в интент
                context.startActivity(intent);
            }
        });
        if(MainActivity.isConnected(context)){// есть ли подключение
        Picasso.with(context).load(artistDscrList.get(i).smallcover).into(personViewHolder.photo
                , new Callback() {// попытаемся скачать изображние
            @Override
            public void onSuccess() {// наша затея удалась сохраним изображение
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = ((BitmapDrawable) personViewHolder.photo.getDrawable()).getBitmap();
                        SaveImg(bitmap,personViewHolder.name.getText().toString(),
                                context.getString(R.string.dir));
                    }
                }).start();
            }

            @Override
            public void onError() {//не удалась скачать, покажем оишбку
                Picasso.with(context).load(R.drawable.error).into(personViewHolder.photo);
            }
        });
        }
        else{// нет подключение
            Picasso.with(context).load(new File(Environment.getExternalStorageDirectory().getPath() +
                    context.getString(R.string.dir) +
                    artistDscrList.get(i).name)).error(R.drawable.error).into(personViewHolder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return artistDscrList.size();
    }

    public  static void SaveImg(Bitmap bitmap,String filename,String dir){//метод для сохранения изображений
        File folderToSave = new File(Environment.getExternalStorageDirectory().getPath() + dir);
        folderToSave.mkdir();// Делаем директорию, если иещё нет или случайно удалил
        File file = new File(folderToSave, filename);
        try {
            OutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();// принудительная очитска буфера
            fOut.close();
        } catch (IOException e) {//если не удалось сохранить файл, ничего страшного
            Log.e(MainActivity.TAG, e.toString());
        }
    }

}





