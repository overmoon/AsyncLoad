package my.fun.asyncload;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请READ_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        setContentView(R.layout.activity_main);



        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/test_pic";
        File files = new File(filePath);
        String[] filenames = files.list();
        List list =new ArrayList<String>();
        for(int i =0 ; i< filenames.length; i++){
            Uri uri = Uri.fromFile(new File(filePath+File.separator+filenames[i]));
            list.add(uri.toString());
        }
        ListView listView = (ListView) findViewById(R.id.listView);
        ListAdapter listAdapter = new ListAdapter(this,  list);

        listView.setAdapter(listAdapter);
    }




}
