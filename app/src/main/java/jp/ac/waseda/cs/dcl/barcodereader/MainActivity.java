package jp.ac.waseda.cs.dcl.barcodereader;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Handler;

import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanSettings;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity implements OnScanListener{

    private BarcodePicker picker;
    Button scanButton;
    Handler handler = new Handler();
    Document document;
    static String uriBefore = "http://www.books.or.jp/ResultDetail.aspx?searchtype=1&isbn=";
    static String uriAfter = "&showcount=20&startindex=0";
    BufferedWriter titleWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScanditLicense.setAppKey("VtawKOreljIjDZcxKIgCQZy4L9gqDP1vlIgsltXkP94");
        ScanSettings settings = ScanSettings.create();


        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_EAN13 , true);
        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_UPCA , true);

        picker = new BarcodePicker(this, settings);

        picker.setOnScanListener(this);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point displaySize = new Point();
        display.getSize(displaySize);

        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(displaySize.x,displaySize.y * 3 / 4);
        rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        addContentView(picker,rParams);
        scanButton = (Button)findViewById(R.id.scanButton);
        handler.post(new Runnable() {
            @Override
            public void run() {
                scanButton.setText("スキャン中");
                scanButton.setEnabled(false);
            }
        });
    }

    @Override
    public void didScan(ScanSession session){

        List<Barcode> barcodeList = session.getNewlyRecognizedCodes();
        final String code = barcodeList.get(0).getData();
        if(!code.substring(0,3).equals("978") && code.length() == 13){
            Toast toast = Toast.makeText(this,code+"はISBNコードではありません",Toast.LENGTH_SHORT);
            toast.show();
            picker.pauseScanning();
            session.clear();
            Handler hd = new Handler();
            hd.postDelayed(new Runnable() {
                @Override
                public void run() {
                    picker.resumeScanning();
                }
            },3000);
            return;
        }

        /* 読み込んだバーコードの処理 */
        List<String> savedBarcode = new ArrayList<String>();

        try {
            BufferedWriter isbnWriter = new BufferedWriter(new OutputStreamWriter(openFileOutput("ISBN.txt", Context.MODE_APPEND)));
            titleWriter = new BufferedWriter(new OutputStreamWriter(openFileOutput("Titles.txt", Context.MODE_APPEND)));
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("ISBN.txt")));
            while(reader.ready()){
                savedBarcode.add(reader.readLine());
            }
            reader.close();
            String scannedCode = code;
            Log.d("Read:","Read!!");
            if(savedBarcode.indexOf(code) == -1){
                isbnWriter.write(code);
                isbnWriter.newLine();
//                final String uri = uriBefore + code + uriAfter;

                AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            String uri = uriBefore + code;
                            document = Jsoup.connect(uri).get();
//                            Log.d("Connect",document.toString());
                            String title = document.getElementById("hlBookTitle").text();
                            if(title.isEmpty()){
                                title = document.getElementById("book").getElementsByTag("h2").text();
                                String kana = document.getElementById("book").getElementsByClass("kana").text();
                                Log.d("kana",kana);
                                title = title.replace(kana,"").trim();
                            }
                            Log.d("Title",title);
                            Log.d("other", document.getElementById("book").getElementsByTag("h2").text());
                            titleWriter.write(title + "(" + code + ")");
                            titleWriter.newLine();
                            titleWriter.close();
                        }catch(IOException ioe){
                            Log.e("IOException",ioe.toString());
                        }
                        return null;
                    }
                };
                task.execute();
//                IsbnSearcher searcher = new IsbnSearcher(code);
//                titleWriter.write(searcher.getTitle());
//                titleWriter.newLine();
                Toast toast = Toast.makeText(this,scannedCode+"を読み取りました",Toast.LENGTH_SHORT);
                toast.show();
            }else{
                Toast toast = Toast.makeText(this,scannedCode+"は読み取り済みです",Toast.LENGTH_SHORT);
                toast.show();
                picker.pauseScanning();
                session.clear();
                Handler hd = new Handler();
                hd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        picker.resumeScanning();
                    }
                },3000);
                return;
            }
            isbnWriter.close();
//            titleWriter.close();
        }catch(Exception e){
        }


        /* 読み取りのポーズ */
        picker.pauseScanning();

        /* 読み取り再開用ボタンの操作 */
        scanButton = (Button)findViewById(R.id.scanButton);
        handler.post(new Runnable() {
            @Override
            public void run() {
                scanButton.setText("スキャン再開");
                scanButton.setEnabled(true);
            }
        });

    }

    public void onScanButtonClick(View view){
        scanButton = (Button)findViewById(R.id.scanButton);
        handler.post(new Runnable() {
            @Override
            public void run() {
                scanButton.setText("スキャン中");
                scanButton.setEnabled(false);
           }
        });
        picker.resumeScanning();
    }

    public void onOutputButtonClick(View view){
        Intent i = new Intent(this,ListActivity.class);
        startActivity(i);
    }

    public void onClearButtonClick(View view){
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setTitle("削除確認");
        alertDlg.setMessage("全てのスキャンデータを削除してもよろしいですか？");
        alertDlg.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){
                        try {
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(openFileOutput("ISBN.txt", Context.MODE_PRIVATE)));
                            writer.close();
                            writer = new BufferedWriter(new OutputStreamWriter(openFileOutput("Titles.txt", Context.MODE_PRIVATE)));
                            writer.close();
                        }catch(Exception e) {
                        }
                    }
                });
        alertDlg.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){
                    }
                });
        alertDlg.create().show();
    }

    @Override
    protected void onResume(){
        picker.startScanning();
        super.onResume();
    }

    @Override
    protected void onPause(){
        picker.stopScanning();
        super.onPause();
    }

}
