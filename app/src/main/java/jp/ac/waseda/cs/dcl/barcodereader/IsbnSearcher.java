package jp.ac.waseda.cs.dcl.barcodereader;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class IsbnSearcher{
    Document document;
    static String uriBefore = "http://www.books.or.jp/ResultDetail.aspx?searchtype=1&isbn=";
    String uri;

    public IsbnSearcher(String code){
        this.uri = uriBefore + code;
        try {
            document = Jsoup.connect(uri).get();
        }catch(IOException ioe){
            Log.e("IOException",ioe.toString());
        }
    }

    public String getTitle(){
        String title = document.getElementById("hlBookTitle").text();
        if(title.isEmpty()){
            title = document.getElementById("book").getElementsByTag("h2").text();
            String kana = document.getElementById("book").getElementsByClass("kana").text();
            Log.d("kana",kana);
            title = title.replace(kana,"").trim();
        }
        return title;
    }

}
