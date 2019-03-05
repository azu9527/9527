package mg.studio.weatherappdesign;


import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isNetworkAvailable(this.getApplicationContext())){
            new DownloadUpdate().execute();
        }else {
            Toast.makeText(MainActivity.this,"UnConnect",Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manger.getActiveNetworkInfo();
            if(info != null){
                return info.isConnected();
            }else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public void btnClick(View view) {
        if(isNetworkAvailable(this.getApplicationContext())){
            new DownloadUpdate().execute();
        }else {
            Toast.makeText(MainActivity.this,"UnConnect",Toast.LENGTH_SHORT).show();
        }
    }

    String []temp=new String[5];
    String []type=new String[5];
    String []time=new String[5];
    String []day=new String[5];

    public void icon(String type,int botton){
        if(type.equals("晴")){ ((ImageButton)findViewById(botton)).setBackgroundResource(R.drawable.sunny_small); }
        else if(type.equals("阴")){ ((ImageButton)findViewById(botton)).setBackgroundResource(R.drawable.partly_sunny_small); }
        else if(type.equals("小雨")){ ((ImageButton)findViewById(botton)).setBackgroundResource(R.drawable.rain_small); }
        else {((ImageButton)findViewById(botton)).setBackgroundResource(R.drawable.windy_small);}
    }

    public void days(String day,int textview){
        ((TextView)findViewById(textview)).setText(day);
    }

    public int bkcolor(String type){
        if(type.equals("晴")){ findViewById(R.id.linearlayout).setBackgroundColor(Color.rgb(232,62,12));return Color.rgb(232,62,12); }
        else if(type.equals("阴")){ findViewById(R.id.linearlayout).setBackgroundColor(Color.rgb(252,202,0)); return Color.rgb(252,202,0);}
        else if(type.equals("小雨")){ findViewById(R.id.linearlayout).setBackgroundColor(Color.GRAY); return  Color.GRAY;}
        else {findViewById(R.id.linearlayout).setBackgroundColor(Color.rgb(0,160,201));return Color.rgb(0,160,201);}
    }

    public String week(String day){
        if(day.equals("星期一")){return "Monday";}
        else if(day.equals("星期二")){return "Tuesday";}
        else if(day.equals("星期三")){return "Wednesday";}
        else if(day.equals("星期四")){return "Thursday";}
        else if(day.equals("星期五")){return "Friday";}
        else if(day.equals("星期六")){return "Saturday";}
        else {return "Sunday";}
    }

    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            //String stringUrl = "https://mpianatra.com/Courses/forecast.json";
            String stringUrl = "http://t.weather.sojson.com/api/weather/city/101043700?tdsourcetag=s_pctim_aiomsg";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    Log.d("TAG", line);
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            //Update the temperature displayed
            try {
                clear();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray list = jsonObject.getJSONObject("data").getJSONArray("forecast");
                for (int i =0;i<5;i++){
                    temp[i]=list.getJSONObject(i).get("high").toString();
                    temp[i]=temp[i].substring(2,temp[i].indexOf("℃")-2);
                    type[i]=list.getJSONObject(i).get("type").toString();
                    time[i]=list.getJSONObject(i).get("ymd").toString();
                    day[i]=list.getJSONObject(i).get("week").toString();
                    day[i]=week(day[i]);
                }

                ((TextView)findViewById(R.id.tv_date)).setText(time[0]);
                ((TextView)findViewById(R.id.temperature_of_the_day)).setText(temp[0]);
                ((TextView)findViewById(R.id.textview)).setText(day[0]);
                bkcolor(type[0]);
                if(type[0].equals("晴")){ ((ImageView)findViewById(R.id.img_weather_condition)).setImageResource(R.drawable.sunny_small); }
                else if(type[0].equals("阴")){ ((ImageView)findViewById(R.id.img_weather_condition)).setImageResource(R.drawable.partly_sunny_small); }
                else if(type[0].equals("小雨")){ ((ImageView)findViewById(R.id.img_weather_condition)).setImageResource(R.drawable.rain_small); }
                else {((ImageView)findViewById(R.id.img_weather_condition)).setImageResource(R.drawable.windy_small);}

                icon(type[1],R.id.bt0);
                icon(type[2],R.id.bt1);
                icon(type[3],R.id.bt2);
                icon(type[4],R.id.bt3);
                days(day[1],R.id.t0);
                days(day[2],R.id.t1);
                days(day[3],R.id.t2);
                days(day[4],R.id.t3);

                Toast.makeText(MainActivity.this,"The app updated",Toast.LENGTH_SHORT).show();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    public void clear(){
        findViewById(R.id.linear0).setBackgroundColor(Color.WHITE);
        findViewById(R.id.linear1).setBackgroundColor(Color.WHITE);
        findViewById(R.id.linear2).setBackgroundColor(Color.WHITE);
        findViewById(R.id.linear3).setBackgroundColor(Color.WHITE);
    }

    public void bt0(View view) {
        clear();
        ImageButton b = (ImageButton)findViewById(R.id.bt0) ;
        ((ImageView)findViewById(R.id.img_weather_condition)).setImageDrawable(b.getBackground());
        if(isNetworkAvailable(this.getApplicationContext())){
            bkcolor(type[1]);
            findViewById(R.id.linear0).setBackgroundColor(bkcolor(type[1]));
            ((TextView)findViewById(R.id.tv_date)).setText(time[1]);
            ((TextView)findViewById(R.id.temperature_of_the_day)).setText(temp[1]);
            ((TextView)findViewById(R.id.textview)).setText(day[1]);
        }
        else {
            Toast.makeText(MainActivity.this,"UnConnect",Toast.LENGTH_SHORT).show();
        }
    }

    public void bt1(View view) {
        clear();
        ImageButton b = (ImageButton)findViewById(R.id.bt1) ;
        ((ImageView)findViewById(R.id.img_weather_condition)).setImageDrawable(b.getBackground());
        if(isNetworkAvailable(this.getApplicationContext())){
            bkcolor(type[2]);
            findViewById(R.id.linear1).setBackgroundColor(bkcolor(type[2]));
            ((TextView)findViewById(R.id.tv_date)).setText(time[2]);
            ((TextView)findViewById(R.id.temperature_of_the_day)).setText(temp[2]);
            ((TextView)findViewById(R.id.textview)).setText(day[2]);
        }
        else {
            Toast.makeText(MainActivity.this,"UnConnect",Toast.LENGTH_SHORT).show();
        }
    }

    public void bt2(View view) {
        clear();
        ImageButton b = (ImageButton)findViewById(R.id.bt2) ;
        ((ImageView)findViewById(R.id.img_weather_condition)).setImageDrawable(b.getBackground());
        if(isNetworkAvailable(this.getApplicationContext())){
            bkcolor(type[3]);
            findViewById(R.id.linear2).setBackgroundColor(bkcolor(type[3]));
            ((TextView)findViewById(R.id.tv_date)).setText(time[3]);
            ((TextView)findViewById(R.id.temperature_of_the_day)).setText(temp[3]);
            ((TextView)findViewById(R.id.textview)).setText(day[3]);
        }
        else {
            Toast.makeText(MainActivity.this,"UnConnect",Toast.LENGTH_SHORT).show();
        }
    }

    public void bt3(View view) {
        clear();
        ImageButton b = (ImageButton)findViewById(R.id.bt3) ;
        ((ImageView)findViewById(R.id.img_weather_condition)).setImageDrawable(b.getBackground());
        if(isNetworkAvailable(this.getApplicationContext())){
            bkcolor(type[4]);
            findViewById(R.id.linear3).setBackgroundColor(bkcolor(type[4]));
            ((TextView)findViewById(R.id.tv_date)).setText(time[4]);
            ((TextView)findViewById(R.id.temperature_of_the_day)).setText(temp[4]);
            ((TextView)findViewById(R.id.textview)).setText(day[4]);
        }
        else {
            Toast.makeText(MainActivity.this,"UnConnect",Toast.LENGTH_SHORT).show();
        }
    }
}
