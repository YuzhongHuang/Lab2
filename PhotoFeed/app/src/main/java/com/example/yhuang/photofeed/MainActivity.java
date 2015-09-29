package com.example.yhuang.photofeed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yhuang.photofeed.Database.PhotoHelper;
import com.example.yhuang.photofeed.HttpRequest.HttpHandler;
import com.example.yhuang.photofeed.HttpRequest.ImageLoadTask;
import com.example.yhuang.photofeed.HttpRequest.SuccessCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    Button search;
    Button pre;
    Button next;
    Button feed;
    Button delete;
    ImageView imageview;
    ArrayList<String> items;
    PhotoHelper mDbHelper;

    int cur_page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = new ArrayList<>();
        editText = (EditText) findViewById(R.id.editText);
        search = (Button) findViewById(R.id.button);
        pre = (Button) findViewById(R.id.pre_btn);
        next = (Button) findViewById(R.id.next_btn);
        feed = (Button) findViewById(R.id.feed_btn);
        delete = (Button) findViewById(R.id.delete);
        imageview = (ImageView) findViewById(R.id.imageView);
        mDbHelper = new PhotoHelper(this);
        mDbHelper = new PhotoHelper(this);

        openSearch();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the search text in edit text
                String searchText = editText.getText().toString();
                makeRequestWithCallback(searchText, items);
                editText.setText("");
            }
        });

        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!items.isEmpty() && cur_page > 0) {
                    cur_page -= 1;
                    new ImageLoadTask(items.get(cur_page), imageview).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Already the first page.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!items.isEmpty() && cur_page < items.size() - 1) {
                    cur_page += 1;
                    new ImageLoadTask(items.get(cur_page), imageview).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Already the last page.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!items.isEmpty()) {
                    PhotoHelper.WriteIntoDatabase(mDbHelper, items.get(cur_page));
                    Toast.makeText(getApplicationContext(), "Added to feed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing to feed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!items.isEmpty()) {
                    PhotoHelper.DeleteItemDatabase(mDbHelper, items.get(cur_page));
                    Toast.makeText(getApplicationContext(), "Deleted from feed", Toast.LENGTH_SHORT).show();
                    openMyFeed();
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_feed:
                openMyFeed();
                return true;
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openSearch() {
        delete.setVisibility(View.GONE);
        feed.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        cur_page = 0;
        items.clear();
        imageview.setImageResource(0);
    }

    public void openMyFeed() {
        delete.setVisibility(View.VISIBLE);
        feed.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
        cur_page = 0;
        items.clear();
        imageview.setImageResource(0);

        PhotoHelper.SyncWithDatabase(mDbHelper, items);
        if (items.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You haven't feed anything yet", Toast.LENGTH_SHORT).show();
        } else {
            new ImageLoadTask(items.get(cur_page), imageview).execute();
        }
    }

    public void makeRequestWithCallback(String searchText,final ArrayList<String> itemlist) {
        HttpHandler handler = new HttpHandler(MainActivity.this);
        handler.searchWithCallback(searchText, new SuccessCallback() {
            @Override
            public void callback(boolean success, JSONObject response) {
                if (success) {
                    Log.d("Success", Boolean.toString(success));
                    try {
                        JSONArray items = response.getJSONArray("items");

                        itemlist.clear();
                        cur_page = 0;

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject itemObject = items.getJSONObject(i);
                            String link = itemObject.getString("link");
                            itemlist.add(link);
                        }

                        new ImageLoadTask(itemlist.get(0), imageview).execute();

                    } catch (Exception err) {
                        Log.e("Error: ", err.getMessage());
                    }


                } else {
                    Log.d("Failure", Boolean.toString(success));
                }
            }
        });
    }

}

