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

import com.example.recomendationbasejson.MainActivity.LoadAllArticles;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ShowInFoAboutArticleActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

	TextView titleLabel;
	TextView authorLabel;
	TextView dateOfCreationLabel;
	TextView stageLabel;
	TextView categoryLabel;
	TextView typeLabel;
	TextView contentOfArticle;
	
	SwipeRefreshLayout mSwipeRefreshLayout;
	
	int identifier = 0;
	
	private String errorString = "";
	
	ArticleFullJSON article;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_about_article);
		
		titleLabel = (TextView) findViewById(R.id.titleOfArticle);
		authorLabel = (TextView) findViewById(R.id.authorOfArticle);
		dateOfCreationLabel = (TextView) findViewById(R.id.DateOfCreation);
		stageLabel = (TextView) findViewById(R.id.developmentStage);
		categoryLabel = (TextView) findViewById(R.id.softwareCategory);
		typeLabel = (TextView) findViewById(R.id.typeOfSoftware);
		contentOfArticle = (TextView) findViewById(R.id.contentOfArticle);
		
		
		
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshTwo);
	    mSwipeRefreshLayout.setOnRefreshListener(this);
	    // делаем повеселее
	    //mSwipeRefreshLayout.setColorScheme(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED);
		
	    Intent intent = getIntent();
	    this.identifier = intent.getExtras().getInt("ID");
	    
		
	    new LoadArticle().execute();
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Обновляем...", Toast.LENGTH_LONG).show();
        // начинаем показывать прогресс
        
        //new LoadArticle().execute();
	}
	
	
	
	class LoadArticle extends AsyncTask<String, String, String> {
		 
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
            	buffer = ShowInFoAboutArticleActivity.this.readArticle(MainActivity.ipAddress + "/contactmanager/api/shownewarticle/" + identifier);
            	Log.e("myLogs", buffer);
            	
            	article = ShowInFoAboutArticleActivity.this.getArticle(buffer);

            
            } catch (ClientProtocolException e) {
            	ShowInFoAboutArticleActivity.this.errorString = "Protocol error";
                e.printStackTrace();
            } catch (IOException e) {
            	ShowInFoAboutArticleActivity.this.errorString = "IO error";
                e.printStackTrace();
            } catch (Exception ex){
            	ShowInFoAboutArticleActivity.this.errorString = "Just error, dunnot know why";
            	ex.printStackTrace();
            }
 			
            return buffer;
        }
 
        /**
         * После завершения фоновой задачи закрываем прогрес диалог
         * **/
        protected void onPostExecute(String file_url) {
        	
        	
        	if(ShowInFoAboutArticleActivity.this.errorString.length() == 0){
        		setLabels();
        	}else{
        		Toast.makeText(ShowInFoAboutArticleActivity.this, ShowInFoAboutArticleActivity.this.errorString, Toast.LENGTH_LONG).show();
        		ShowInFoAboutArticleActivity.this.errorString = "";
        	}
        
            // закрываем прогресс диалог после получение все продуктов
        	mSwipeRefreshLayout.setRefreshing(false);	
        }
    }
 
	
	public void setLabels(){
		
		if(article == null) return;
		
		titleLabel.setText(article.getTitle());
		authorLabel.setText(article.getAuthor());
		dateOfCreationLabel.setText(article.getDatOfCreation());
		stageLabel.setText(article.getStage());
		categoryLabel.setText(article.getCategory());
		typeLabel.setText(article.getType());
		contentOfArticle.setText(article.getContent());
		
	}
	
	
 public String readArticle(String path) throws ClientProtocolException, IOException {
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



public ArticleFullJSON getArticle(String JSONarray) throws Exception{
	
	
	ArticleFullJSON articleBuffer = new ArticleFullJSON();
	
	JSONObject jsonObject = new JSONObject(JSONarray);
	
	
	articleBuffer.setAuthor(jsonObject.getString("author"));
	articleBuffer.setTitle(jsonObject.getString("title"));
	articleBuffer.setIdent(jsonObject.getInt("ident"));
	articleBuffer.setDatOfCreation(jsonObject.getString("datOfCreation"));
	articleBuffer.setStage(jsonObject.getString("stage"));
	articleBuffer.setCategory(jsonObject.getString("category"));
	articleBuffer.setType(jsonObject.getString("type"));
	articleBuffer.setContent(jsonObject.getString("content"));

	
	return articleBuffer;
	
}

}
