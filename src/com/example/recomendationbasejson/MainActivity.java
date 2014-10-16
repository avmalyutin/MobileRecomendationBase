package com.example.recomendationbasejson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

	SwipeRefreshLayout mSwipeRefreshLayout;
	
	ArrayList <ArticlesFromListJSON> articlesList = new ArrayList <ArticlesFromListJSON>();
	ArrayList <String> labels = new ArrayList <String>();
	ListView listView;
	
	private String errorString = "";
	
	public static String ipAddress = "http://192.168.43.122:8081";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		 mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
	     mSwipeRefreshLayout.setOnRefreshListener(this);
	     // делаем повеселее
	     mSwipeRefreshLayout.setColorScheme(R.color.blue, R.color.orange, R.color.green, R.color.purple);

	     listView = (ListView) findViewById(android.R.id.list);
	     
	     labels.add("No articles yet");
	     
	     // классический адаптер

	     listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels));
	      
	     listView.setOnItemClickListener(new OnItemClickListener() {
	         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	        	 if(!articlesList.isEmpty()){
	        		 int identificator = articlesList.get(position).getIdent();
	        		 Intent intent = new Intent(getApplicationContext(), ShowInFoAboutArticleActivity.class); 
				     intent.putExtra("ID",  identificator);
				     startActivity(intent);
	        		  
	        	 }
	         }
	     });
	     
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		// говорим о том, что собираемся начать
        Toast.makeText(this, "Обновляем...", Toast.LENGTH_LONG).show();
        // начинаем показывать прогресс
        
        new LoadAllArticles().execute();
        
	}


	 class LoadAllArticles extends AsyncTask<String, String, String> {
		 
	        /**
	         * Перед началом фонового потока Show Progress Dialog
	         * */
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            mSwipeRefreshLayout.setRefreshing(true);
	        }
	 
	        /**
	         * Получаем все продукт из url
	         * */
	        protected String doInBackground(String... args) {
	           
	            String buffer = "";
	            try {
	            	buffer = MainActivity.this.readListOfArticles(MainActivity.ipAddress + "/contactmanager/api/confirm/");
	            	Log.e("myLogs", buffer);
	            	
	            	articlesList = MainActivity.this.getListArticles(buffer);
	            	labels = MainActivity.this.makeLabels(articlesList);

	            
	            } catch (ClientProtocolException e) {
	            	MainActivity.this.errorString = "Protocol error";
	                e.printStackTrace();
	            } catch (IOException e) {
	            	MainActivity.this.errorString = "IO error";
	                e.printStackTrace();
	            } catch (Exception ex){
	            	MainActivity.this.errorString = "Just error, dunnot know why";
	            	ex.printStackTrace();
	            }
	 			
	            return buffer;
	        }
	 
	        /**
	         * После завершения фоновой задачи закрываем прогрес диалог
	         * **/
	        protected void onPostExecute(String file_url) {
	        	
	        	
	        	if(MainActivity.this.errorString.length() == 0){
	        		MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(MainActivity.this, articlesList, labels);
		        	listView.setAdapter(adapter);
		        	listView.invalidate();
	        	}else{
	        		Toast.makeText(MainActivity.this, MainActivity.this.errorString, Toast.LENGTH_LONG).show();
	        		MainActivity.this.errorString = "";
	        	}
	        
	            // закрываем прогресс диалог после получение все продуктов
	        	mSwipeRefreshLayout.setRefreshing(false);	
	        }
	    }
	 
	 public String readListOfArticles(String path) throws ClientProtocolException, IOException {
		    StringBuilder builder = new StringBuilder();
		    HttpClient client = new DefaultHttpClient();
		    
		    HttpGet httpGet = new HttpGet(path);
		    
		    HttpResponse response = client.execute(httpGet);
		    StatusLine statusLine = response.getStatusLine();
		    int statusCode = statusLine.getStatusCode();
		    if (statusCode == 200) {
		        HttpEntity entity = response.getEntity();
		        InputStream content = entity.getContent();
		        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		        String line;
		        while ((line = reader.readLine()) != null) {
		          builder.append(line);
		        }
		      } else {
		        throw new ClientProtocolException();
		      }
		    
		    return builder.toString();
		  }
	
	
	
	public ArrayList<ArticlesFromListJSON> getListArticles(String JSONarray) throws Exception{
		
		
		ArrayList <ArticlesFromListJSON> buffer = new ArrayList<ArticlesFromListJSON>();
		
		JSONArray jsonArray = new JSONArray(JSONarray);
		
		for (int i = 0; i < jsonArray.length(); i++) {
			ArticlesFromListJSON article = new ArticlesFromListJSON();
			
			JSONObject jsonObject = jsonArray.getJSONObject(i);
		    
			article.setAuthor(jsonObject.getString("author"));
			article.setTitle(jsonObject.getString("title"));
			article.setIdent(jsonObject.getInt("ident"));
		
			buffer.add(article);
		}
		
		return buffer;
		
	}
	

	 public ArrayList <String> makeLabels(ArrayList <ArticlesFromListJSON> response){
		 
		 ArrayList <String> buff = new ArrayList <String>();
		 for(ArticlesFromListJSON art : response){
			 buff.add(art.getTitle());
		 }
		 
		 return buff;
		 
	 }
	 
}
