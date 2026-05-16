package com.example.tripbuddy_v10.Memory_Creation;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tripbuddy_v10.Aesthetics.FullScreenImageActivity;
import com.example.tripbuddy_v10.R;
import com.example.tripbuddy_v10.Storage.Database;

import java.util.ArrayList;

public class MemoryActivity extends AppCompatActivity {

    private Button btnPlay, btnPause, btnStop, btnAddMemory;
    private MediaPlayer mediaPlayer;

    //New variable for memory post
    private static final int PICK_IMAGE = 100;
    ArrayList<MemoryPost> posts = new ArrayList<>();
    MemoryAdapter adapter;
    GridView gvMemoryPost;
    private Uri imageUri;
    private String caption;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btnPlay=findViewById(R.id.btnPlay);
        btnPause=findViewById(R.id.btnPause);
        btnStop=findViewById(R.id.btnStop);
        mediaPlayer= MediaPlayer.create(this,R.raw.song1);
        //New UI element (for memory post)
        btnAddMemory=findViewById(R.id.btnAddMemory);

        //Initializing the UI elements
        gvMemoryPost = findViewById(R.id.gvMemoryPost);
        adapter = new MemoryAdapter(this, posts);
        gvMemoryPost.setAdapter(adapter);

        // Add new memory post when tapping grid
        gvMemoryPost.setOnItemClickListener((parent, view, position, id) -> {
            pickImageFromGallery();
        });


        //4.1 Music player
        // Play button
        btnPlay.setOnClickListener(v -> {
            //4.2
            //Button effects for the play button
            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f));

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        });



        // Pause button
        btnPause.setOnClickListener(v -> {
            //Button effects for the pause button
            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f));

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });



        // Stop button
        btnStop.setOnClickListener(v -> {
            //Button effects for the stop button
            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f));

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(MemoryActivity.this, R.raw.song1);
            }
        });



        //Memory button
        btnAddMemory.setOnClickListener(v -> {
            pickImageFromGallery();

            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f));

        });



        //When the image is clicked on, it will go fullscreen
        gvMemoryPost.setOnItemClickListener((parent, view, position, id) -> {
            MemoryPost clickedPost = posts.get(position);

            Intent intent = new Intent(MemoryActivity.this, FullScreenImageActivity.class);
            intent.putExtra("imageUri", clickedPost.imageUri.toString());
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();  // use the class-level one

            // Ask for caption
            EditText input = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Add Memory Caption")
                    .setView(input)
                    .setPositiveButton("Save", (dialog, which) -> {
                        caption = input.getText().toString();

                        // Add to the list
                        posts.add(new MemoryPost(imageUri, caption));
                        adapter.notifyDataSetChanged();

                        // Save to DB here
                        Database dbHelper = new Database(this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.execSQL("INSERT INTO " + Database.TABLE_MEMORIES +
                                        " (" + Database.COL_IMAGE_URI + ", " +
                                        Database.COL_CAPTION + ") VALUES (?, ?)",
                                new Object[]{imageUri.toString(), caption});
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }


        //Saving the memory post to the database
        Database dbHelper = new Database(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("INSERT INTO " + Database.TABLE_MEMORIES +
                        " (" + Database.COL_IMAGE_URI + ", " +
                        Database.COL_CAPTION + ") VALUES (?, ?)",
                new Object[]{imageUri.toString(), caption});

    }
}

//Memory post
class MemoryPost {
    Uri imageUri;
    String caption;

    MemoryPost(Uri imageUri, String caption) {
        this.imageUri = imageUri;
        this.caption = caption;
    }
}

class MemoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MemoryPost> posts;

    public MemoryAdapter(Context context, ArrayList<MemoryPost> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout;
        ImageView imageView;
        TextView textView;

        if (convertView == null) {
            layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            textView = new TextView(context);
            textView.setTextSize(14);
            textView.setGravity(Gravity.CENTER);

            layout.addView(imageView);
            layout.addView(textView);
        } else {
            layout = (LinearLayout) convertView;
            imageView = (ImageView) layout.getChildAt(0);
            textView = (TextView) layout.getChildAt(1);
        }

        MemoryPost post = posts.get(position);
        imageView.setImageURI(post.imageUri);
        textView.setText(post.caption);

        return layout;
    }
}