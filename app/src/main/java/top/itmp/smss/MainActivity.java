package top.itmp.smss;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button read_sms = null;
    private Button fake_sms = null;
    private List<HashMap> smslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smslist = new ArrayList<HashMap>();

        read_sms = (Button)findViewById(R.id.sms_read);
        fake_sms = (Button)findViewById(R.id.sms_fake);

        read_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver cr = getContentResolver();
                Cursor cursor = cr.query(Uri.parse("content://sms"), new String[]{"address", "date", "body", "type"},
                        null, null, null);
                while (cursor.moveToNext()) {
                    HashMap sms = new HashMap<>();
                    /*
                    String address = cursor.getString(0);
                    long date = cursor.getLong(1);
                    String body = cursor.getString(2);
                    String type = cursor.getString(3);
                    */
                    sms.put("address", cursor.getString(0));
                    sms.put("date", cursor.getLong(1));
                    sms.put("body", cursor.getString(2));
                    sms.put("type", cursor.getString(3));
                    smslist.add(sms);
                    //smslist.add(sms);
                    Log.e("TAG", sms.toString());
                }
                XmlSerializer xs = Xml.newSerializer();
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "sms.xml");
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(file);
                    xs.setOutput(fos, "utf-8");

                    xs.startDocument("utf-8", true);
                    xs.startTag(null, "message");

                    for (HashMap sms : smslist) {
                        xs.startTag(null, "sms");

                        xs.startTag(null, "body");
                        xs.text(sms.get("body").toString());
                        xs.endTag(null, "body");

                        xs.startTag(null, "date");
                        xs.text(sms.get("date").toString());
                        xs.endTag(null, "date");

                        xs.startTag(null, "type");
                        xs.text(sms.get("type").toString());
                        xs.endTag(null, "type");

                        xs.startTag(null, "address");
                        xs.text(sms.get("address").toString());
                        xs.endTag(null, "address");

                        xs.endTag(null, "sms");
                    }

                    xs.endTag(null, "message");
                    xs.endDocument();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        fake_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(){
                    @Override
                    public void run() {
                        ContentResolver cr = getContentResolver();
                        ContentValues values = new ContentValues();
                        values.put("address", 95533);
                        values.put("type", 1);
                        values.put("date", System.currentTimeMillis());
                        values.put("body", "您尾号为9999的信用卡收到1,000,000RMB转账，请注意查收");
                        cr.insert(Telephony.Sms.Sent.CONTENT_URI, values);
                    }
                };
                t.start();
            }
        });
    }
}
