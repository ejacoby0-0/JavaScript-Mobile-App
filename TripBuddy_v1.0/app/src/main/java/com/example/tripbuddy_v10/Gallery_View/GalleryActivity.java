package com.example.tripbuddy_v10.Gallery_View;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tripbuddy_v10.R;
import com.example.tripbuddy_v10.Storage.Database;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    Button btnShowTrips, btnShowMemories;
    ListView lvTrips;
    GridView gridViewMemories;

    Database dbHelper;

    ArrayAdapter<String> tripAdapter;
    ArrayList<String> tripList;

    ArrayList<MemoryItem> memoryList;
    MemoryAdapter memoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gallery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnShowTrips = findViewById(R.id.btnShowTrips);
        btnShowMemories = findViewById(R.id.btnShowMemories);
        lvTrips = findViewById(R.id.lvTrips);
        gridViewMemories = findViewById(R.id.gridViewMemories);

        dbHelper = new Database(this);

        tripList = new ArrayList<>();
        memoryList = new ArrayList<>();

        // Trips button clicked
        btnShowTrips.setOnClickListener(v -> {
            loadTripsFromDB();
            lvTrips.setVisibility(View.VISIBLE);
            gridViewMemories.setVisibility(View.GONE);
        });

        // Memories button clicked
        btnShowMemories.setOnClickListener(v -> {
            loadMemoriesFromDB();
            gridViewMemories.setVisibility(View.VISIBLE);
            lvTrips.setVisibility(View.GONE);
        });

        // On trip click (show Toast for now, later can open edit)
        lvTrips.setOnItemClickListener((parent, view, position, id) -> {
            String tripSummary = tripList.get(position);
            Toast.makeText(this, "Trip selected:\n" + tripSummary, Toast.LENGTH_SHORT).show();
        });

        // On memory click (show Toast with caption)
        gridViewMemories.setOnItemClickListener((parent, view, position, id) -> {
            MemoryItem item = memoryList.get(position);
            Toast.makeText(this, "Memory: " + item.caption, Toast.LENGTH_SHORT).show();
        });
    }


    private void loadTripsFromDB() {
        tripList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Database.TABLE_TRIPS, null);

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No trips found", Toast.LENGTH_SHORT).show();
        }

        while (cursor.moveToNext()) {
            String destination = cursor.getString(cursor.getColumnIndexOrThrow(Database.COL_DESTINATION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(Database.COL_DATE));
            String summary = cursor.getString(cursor.getColumnIndexOrThrow(Database.COL_SUMMARY));
            tripList.add(destination + " (" + date + ")\n" + summary);
        }
        cursor.close();

        tripAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tripList);
        lvTrips.setAdapter(tripAdapter);
    }


    private void loadMemoriesFromDB() {
        memoryList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Database.TABLE_MEMORIES, null);

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No memories found", Toast.LENGTH_SHORT).show();
        }

        while (cursor.moveToNext()) {
            String uriString = cursor.getString(cursor.getColumnIndexOrThrow(Database.COL_IMAGE_URI));
            String caption = cursor.getString(cursor.getColumnIndexOrThrow(Database.COL_CAPTION));

            Uri uri = Uri.parse(uriString);
            memoryList.add(new MemoryItem(uri, caption));
        }
        cursor.close();

        memoryAdapter = new MemoryAdapter(this, memoryList);
        gridViewMemories.setAdapter(memoryAdapter);
    }


    // Helper class for memory
    static class MemoryItem {
        Uri imageUri;
        String caption;
        MemoryItem(Uri uri, String cap) {
            imageUri = uri;
            caption = cap;
        }
    }


    //Memory Adapter
    public class MemoryAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<GalleryActivity.MemoryItem> memoryList;

        public MemoryAdapter(Context ctx, ArrayList<GalleryActivity.MemoryItem> list) {
            context = ctx;
            memoryList = list;
        }

        @Override
        public int getCount() {
            return memoryList.size();
        }

        @Override
        public Object getItem(int position) {
            return memoryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_memory, parent, false);
            }

            ImageView img = convertView.findViewById(R.id.imgMemory);
            TextView caption = convertView.findViewById(R.id.txtCaption);

            GalleryActivity.MemoryItem item = memoryList.get(position);
            img.setImageURI(item.imageUri);
            caption.setText(item.caption);

            return convertView;
        }
    }
}