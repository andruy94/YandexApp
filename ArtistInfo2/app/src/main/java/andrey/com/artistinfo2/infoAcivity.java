package andrey.com.artistinfo2;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class infoAcivity extends AppCompatActivity implements View.OnClickListener{
    protected ArtistDscr artistDscr;
    Bitmap bitmap;// вынесено для потока
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);
        TextView textView=(TextView) findViewById(R.id.description);
        final ImageView imageView=(ImageView) findViewById(R.id.IvArtist);
        artistDscr=getIntent().getParcelableExtra("Extra");
        Log.d(MainActivity.TAG, artistDscr.description);
        ((TextView) findViewById(R.id.genres2)).setText(String.format(this.getString(R.string.genr), artistDscr.genres));
        ((TextView) findViewById(R.id.albums2)).setText(String.format(this.getString(R.string.alb), artistDscr.albums,
                artistDscr.tracks));

        textView.setText(artistDscr.description);
        TextView tvlink=((TextView) findViewById(R.id.link));
        tvlink.setText(String.format(this.getString(R.string.link), artistDscr.link));
        tvlink.setOnClickListener(this);
        getSupportActionBar().setTitle(artistDscr.name);//установить заголок, как имя Артиста


        textView.setText(artistDscr.description);
        getSupportActionBar().setTitle(artistDscr.name);
        if(MainActivity.isConnected(this)){
        Picasso.with(this).load(artistDscr.cover.getBigcover()).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                try {// null если быстро выйдем
                    findViewById(R.id.progressBar3).setVisibility(View.INVISIBLE);//скроем прогрес бар
                    bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                } catch (NullPointerException e) {// если ошибка то ничего стращного
                }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RVAdapter.SaveImg(bitmap, artistDscr.name + 'b',//b- что не путать с обычными картинками
                                    getApplicationContext().getString(R.string.dir));
                        }
                    }).start();

            }

            @Override
            public void onError() {// м б будут ошибки в сети, тогда можно попытаться так грузануть
                Picasso.with(getApplicationContext()).load(R.drawable.error).into(imageView);
            }
        });
        }else{
            Picasso.with(getApplicationContext()).load(new File(Environment.getExternalStorageDirectory().getPath()
                    + getApplicationContext().getString(R.string.dir)) +
                    artistDscr.name + 'b').error(R.drawable.error).into(imageView);
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.link:
                Uri link = Uri.parse(artistDscr.link);
                startActivity(new Intent(Intent.ACTION_VIEW, link));
        }

    }
}
