package com.example.tripbuddy_v10.Home_Page;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tripbuddy_v10.Gallery_View.GalleryActivity;
import com.example.tripbuddy_v10.Memory_Creation.MemoryActivity;
import com.example.tripbuddy_v10.R;
import com.example.tripbuddy_v10.Registraction.MainActivity;
import com.example.tripbuddy_v10.Trip_Planning.TripPlanningActivity;

public class HomePageActivity extends AppCompatActivity {

    Button btnGallery, btnPlanTrip, btnMemories, btnLogout;
    TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initializing the buttons and textview
        btnGallery=findViewById(R.id.btnGallery);
        btnPlanTrip=findViewById(R.id.btnPlanTrip);
        btnMemories=findViewById(R.id.btnMemories);
        tvWelcome=findViewById(R.id.tvWelcome);

        //New UI element
        btnLogout = findViewById(R.id.btnLogout);

        //5.1
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        btnLogout.setOnClickListener(v -> {
            // Clear session
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Back to login screen
            startActivity(new Intent(HomePageActivity.this, MainActivity.class));
            finish();
        });

        TextView welcomeMsg = findViewById(R.id.tvWelcome);


        //2.2 Establishing "Intent" to be used to direct the users
        //Directed users to the Gallery
        btnGallery.setOnClickListener(v -> {
            startActivity(new Intent(HomePageActivity.this, GalleryActivity.class));
            //4.2 improve interactivity and aesthetics
            //fades in
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            //Button effects for the Gallery button
            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f));

        });


        //Directs users to the plan trip
        btnPlanTrip.setOnClickListener(v -> {
            startActivity(new Intent(HomePageActivity.this, TripPlanningActivity.class));
            //4.2 improve interactivity and aesthetics
            //fades in
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            //Button effects for the plan trip button
            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f));

        });

        //Directs users to the memory
        btnMemories.setOnClickListener(v -> {
            startActivity(new Intent(HomePageActivity.this, MemoryActivity.class));
            //4.2 improve interactivity and aesthetics
            //fades in
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            //Button effects for the memory button
            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f));

        });

        //2.5 Fade out animation after 2 sec
        welcomeMsg.animate()
                .alpha(0f)
                .setStartDelay(2000)
                .setDuration(1000)
                .withEndAction(() -> welcomeMsg.setVisibility(View.GONE))
                .start();



    }
}