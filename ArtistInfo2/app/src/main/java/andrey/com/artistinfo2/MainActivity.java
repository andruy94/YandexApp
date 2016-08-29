package andrey.com.artistinfo2;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private DBWorker dbWorker;
    //protected  DownloadTask downloadTask;
    private RecyclerView rv;
    public static String TAG="TAG";// для отсеживания логов
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context= getApplication();
        setContentView(R.layout.activity_main);
        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);//устанавливается, если списко не будет менять размер, для экномии памяти
        dbWorker=new DBWorker(this);
        try {
            dbWorker.open();
        } catch (SQLException e) {
            Log.e(MainActivity.TAG,e.toString());
        }

        //downloadTask=new DownloadTask(this);
       // downloadTask.execute(getString(R.string.Url));

        ServiceGenerator.getInstance().getArtistDscr().enqueue(new Callback<List<ArtistDscr>>() {
            @Override
            public void onResponse(Call<List<ArtistDscr>> call, Response<List<ArtistDscr>> response) {
                ArrayList<ArtistDscr> artistDscrs=new ArrayList<>(response.body());
                Collections.sort(artistDscrs);// отстортируем наш список по id, наверно id показывает в каком порядке были добавлены записи
                RVAdapter adapter = new RVAdapter(artistDscrs,getApplicationContext());
                rv.setAdapter(adapter);
                //скоремм информацию о заугрзки
                findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                findViewById(R.id.textView).setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<List<ArtistDscr>> call, Throwable t) {
                Log.e("TAG",t.toString());
            }
        });
    }


    /*public class DownloadTask extends AsyncTask<String, List<ArtistDscr>, List<ArtistDscr>> {
        private DBWorker dbWorker;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        Activity context;
        boolean No_internet=false;

        public DownloadTask(Activity context){
            dbWorker=new DBWorker(context);
            try {
                dbWorker.open();
            } catch (SQLException e) {
                Log.e(MainActivity.TAG,e.toString());
            }
            this.context=context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected List<ArtistDscr> doInBackground(String... urls) {// тут входные параметры
            String Surl=urls[0];
            List<ArtistDscr> artistDscrList;
            if(isConnected(context)) {
                try {
                    URL url = new URL(Surl);
                    // получаем данные с внешнего ресурса
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");// в нашем случае серверу всё равно что за метод
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    // получили нашу строчку
                    Log.d(MainActivity.TAG, buffer.toString());
                    JSONObject jsonObj;
                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(buffer.toString());
                    } catch (JSONException e) {
                        return null;
                    }

                    artistDscrList = new ArrayList<>(jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++)
                        try {
                            jsonObj = jsonArray.getJSONObject(i);
                            ArtistDscr artistDscr = new ArtistDscr();
                            artistDscr.id = jsonObj.getInt("id");
                            artistDscr.name = jsonObj.getString("name");
                            buffer = new StringBuffer();
                            buffer.append(jsonObj.getJSONArray("genres").getString(0));
                            for (int j = 1; j < jsonObj.getJSONArray("genres").length(); j++) {
                                buffer.append(", " + jsonObj.getJSONArray("genres").getString(j));
                            }
                            artistDscr.genres = (buffer.toString());
                            artistDscr.tracks = jsonObj.getInt("tracks");
                            artistDscr.albums = jsonObj.getInt("albums");
                            artistDscr.link = jsonObj.getString("link");
                            artistDscr.description = jsonObj.getString("description");
                            artistDscr.smallcover = jsonObj.getJSONObject("cover").getString("small");
                            artistDscr.bigcover = jsonObj.getJSONObject("cover").getString("big");
                            artistDscrList.add(artistDscr);
                            if (dbWorker.insertPic(artistDscr) < 0)// если запись уже есть, то обновим её
                                dbWorker.updatePic(artistDscr);
                        } catch (JSONException e) {
                            Log.e(MainActivity.TAG, "не можем взять JSONARRAy");
                            // если не получиться распарсить JSON то идём дальше;
                        }
                } catch (IOException e) {
                    Log.d(MainActivity.TAG, "проблемы с соединением");
                    // показываем, что насохраняли
                    artistDscrList = dbWorker.GetAllArtist();
                    dbWorker.close();
                }
            }else {
                // показываем, что насохраняли
                artistDscrList = dbWorker.GetAllArtist();
                dbWorker.close();
                No_internet = true;
            }
            return artistDscrList;
        }

        protected void onPostExecute(List<ArtistDscr> result) {
            if(No_internet)
                Toast.makeText(getApplicationContext(),R.string.No_internet,Toast.LENGTH_SHORT).show();
            Collections.sort(result);// отстортируем наш список по id, наверно id показывает в каком порядке были добавлены записи
            RVAdapter adapter = new RVAdapter(result,context);
            rv.setAdapter(adapter);
            //скоремм информацию о заугрзки
            context.findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
            context.findViewById(R.id.textView).setVisibility(View.INVISIBLE);
        }
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                /*if(downloadTask.getStatus()== AsyncTask.Status.FINISHED){//если идёт загрзку, то не смысла заново начинать
                    rv.setAdapter(null);//скроем наш список и покажем загрузку
                    findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
                    findViewById(R.id.textView).setVisibility(View.VISIBLE);
                    downloadTask=new DownloadTask(this);// начёнм загрузку JSON
                    downloadTask.execute(getString(R.string.Url));
                }else
                    Toast.makeText(this,getString(R.string.msg),Toast.LENGTH_SHORT).show();
                    return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public static boolean  isConnected(Context context) {// проверим есть ли интернет
        ConnectivityManager connMgr = (ConnectivityManager)  context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
