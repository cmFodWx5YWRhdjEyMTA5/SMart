package com.sahakarmart.Activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.sahakarmart.Database.DBManager;
import com.sahakarmart.Database.DatabaseHelper;
import com.sahakarmart.R;


public class Notification extends AppCompatActivity {


    private static final String TAG = Notification.class.getSimpleName();

    public DBManager dbManager;
    TextView idTextView, titleTextView, descTextView;
    public ListView listView;

    public SimpleCursorAdapter adapter;

    private DatabaseHelper dbHelper;


    private Context context;
    private SQLiteDatabase database;



    final String[] from = new String[]{DatabaseHelper._ID,
            DatabaseHelper.MESSAGE, DatabaseHelper.TIME};

    final int[] to = new int[]{R.id.txtNotificationid, R.id.txtNotificationtitle, R.id.txtNotificationdesc};
    private long _id;

    int idt;
    String url,msg;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbar = (Toolbar)findViewById(R.id.NotificationToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        context= getApplicationContext();


        dbManager = new DBManager(this);
        dbManager.open();
        final Cursor cursor = dbManager.fetch();


        // get Data from notification
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();


        Log.e(TAG,"Notification cursor Values :"+cursor.getCount());


        listView = (ListView) findViewById(R.id.Notification_list_view);
        listView.setEmptyView(findViewById(R.id.empty));



        adapter = new SimpleCursorAdapter(this, R.layout.list_item_notification, cursor, from, to, 0);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
