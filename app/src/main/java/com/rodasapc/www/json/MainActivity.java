package com.rodasapc.www.json;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends Activity {


    //Link da Stock Info
    static String yahooStockInfo =  "https://query.yahooapis.com/v1/public/" +
                                    "yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22" +
                                    "MSFT" + //MSFT = Microsoft
                                    "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";



    //Statics para Guardar a Info
    static String stockSymbol = "";
    static String stockDaysLow = "";
    static String stockDaysHigh = "";
    static String stockChange = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Call da cena
        new MyAsyncTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyAsyncTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... arg0) {


            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httppost = new HttpPost(yahooStockInfo);
            httppost.setHeader("Content-type", "application/json");

            InputStream inputStream = null;
            String result = null;

            try{
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();


                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder theStringBuilder = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null){
                    theStringBuilder.append(line + "\n");
                }

                result = theStringBuilder.toString();


            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                try{
                    if(inputStream != null) inputStream.close();

                } catch (Exception e){

                }

            }


            JSONObject jsonObject;

            try{

                jsonObject = new JSONObject(result);

                JSONObject queryJSONObject = jsonObject.getJSONObject("query");
                JSONObject resultsJSONObject = queryJSONObject.getJSONObject("results");
                JSONObject quoteJSONObject = resultsJSONObject.getJSONObject("quote");

                stockSymbol     = quoteJSONObject.getString("symbol");
                stockDaysLow    = quoteJSONObject.getString("DaysLow");
                stockDaysHigh   = quoteJSONObject.getString("DaysHigh");
                stockChange     = quoteJSONObject.getString("Change");

            }catch (JSONException e){
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            TextView line1 = (TextView) findViewById(R.id.line1);
            TextView line2 = (TextView) findViewById(R.id.line2);
            TextView line3 = (TextView) findViewById(R.id.line3);

            line1.setText("Stock: " + stockSymbol + " : " + stockChange );
            line2.setText("DaysLow: " + stockDaysLow);
            line3.setText("DaysHigh: " + stockDaysHigh);



        }
    }
}
