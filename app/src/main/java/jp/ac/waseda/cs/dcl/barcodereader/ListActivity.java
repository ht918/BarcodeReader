package jp.ac.waseda.cs.dcl.barcodereader;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setBarcodeList();
    }

    public void onBackButtonClick(View view){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    void setBarcodeList(){
        List<String> barcodeList = new ArrayList<String>();
        try{
            Log.d("SetBarcodeList","Start!");
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("Titles.txt")));
            while(reader.ready()){
                String str = reader.readLine();
                Log.d("ReadLine",str);
                barcodeList.add(str);
            }
            reader.close();
        }catch (Exception e){
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,barcodeList);
        ListView list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
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
                        setBarcodeList();
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
}
