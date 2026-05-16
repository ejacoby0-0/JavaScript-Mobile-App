package com.example.tripbuddy_v10.Trip_Planning;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TripPlanningActivity extends AppCompatActivity {

    //Initalising the variables
    private EditText edtDestination, edtNotes, edtCustomExpenses, edtAddExpenses;
    private Button btndatePicker, btnSave;

    //Intialising the activities
    private ListView lvOutdoorActivites;
    private String[] outdoorActivities = {"sightseeing - R100", "hiking - R50", "dining - R120", "museum tours - R200"};
    private int[] activityCost = {100, 50, 120, 200};

    //Initalising total cost
    private TextView tvTotalCost, tvSubtotal, tvDiscount, tvFinalTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trip_planning);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Linking the UI elements
        edtDestination=findViewById(R.id.edtDestination);
        edtNotes=findViewById(R.id.edtNotes);
        edtCustomExpenses=findViewById(R.id.edtCustomExpenses);
        btndatePicker=findViewById(R.id.btndatePicker);
        //New UI element(outdoor activities)
        lvOutdoorActivites=findViewById(R.id.lvOutdoorActivites);
        //New UI element(additional expenses)
        edtAddExpenses=findViewById(R.id.edtAddExpenses);
        //New UI element(Total cost)
        tvTotalCost=findViewById(R.id.tvTotalCost);
        //New UI element(Save Button)
        btnSave=findViewById(R.id.btnSave);
        //New UI element(Summary)
        tvSubtotal=findViewById(R.id.tvSubtotal);
        tvDiscount=findViewById(R.id.tvDiscount);
        tvFinalTotal=findViewById(R.id.tvFinalTotal);

        //Travel date button
        btndatePicker.setOnClickListener(v -> showDatePicker());

        //3.2 list of predefined trip activities

        // Select one or more activities to add to their trip.
        // Use built-in multiple choice layout
        // Simple adapter with multiple choice items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice,outdoorActivities);
        lvOutdoorActivites.setAdapter(adapter);
        lvOutdoorActivites.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        //3.3 Update total cost as user types in edtAddExpenses
        lvOutdoorActivites.setOnItemClickListener((parent, view, position, id) -> updateTotalCost());

        // Update when typing in custom expense
        edtCustomExpenses.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateTotalCost(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Update when typing in additional expense
        edtAddExpenses.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateTotalCost(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 3.5 Save button
        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                saveTrip();
            }
        });

    }

    //3.1 User inputs
    //User selecting the date
    private void showDatePicker() {
        // Get today’s date as default
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Update button text with chosen date
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    btndatePicker.setText(date);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    // Basic validation
    private boolean validateInputs() {
        String destination = edtDestination.getText().toString().trim();
        String notes = edtNotes.getText().toString().trim();
        String expenseStr = edtCustomExpenses.getText().toString().trim();

        if (destination.isEmpty()) {
            edtDestination.setError("Destination required");
            return false;
        }

        if (notes.isEmpty()) {
            edtNotes.setError("Notes required");
            return false;
        }

        if (expenseStr.isEmpty()) {
            edtCustomExpenses.setError("Enter an expense");
            return false;
        }

        try {
            double expense = Double.parseDouble(expenseStr);
            if (expense < 0) {
                edtCustomExpenses.setError("Expense must be positive");
                return false;
            }
        } catch (NumberFormatException e) {
            edtCustomExpenses.setError("Invalid number");
            return false;
        }

        // If all good
        Toast.makeText(this, "Inputs are valid!", Toast.LENGTH_SHORT).show();
        return true;
    }


    //Method for updating total cost (part of 3.3)
    private void updateTotalCost() {
        double total = 0;

        // Add selected activities
        for (int i = 0; i < outdoorActivities.length; i++) {
            if (lvOutdoorActivites.isItemChecked(i)) {
                total += activityCost[i];
            }
        }

        // Add custom expense
        String customStr = edtCustomExpenses.getText().toString().trim();
        if (!customStr.isEmpty()) {
            try {
                double val = Double.parseDouble(customStr);
                if (val >= 0) total += val;
                else edtCustomExpenses.setError("Must be non-negative");
            } catch (NumberFormatException e) {
                edtCustomExpenses.setError("Invalid number");
            }
        }

        // Add additional expense
        String addStr = edtAddExpenses.getText().toString().trim();
        if (!addStr.isEmpty()) {
            try {
                double val = Double.parseDouble(addStr);
                if (val >= 0) total += val;
                else edtAddExpenses.setError("Must be non-negative");
            } catch (NumberFormatException e) {
                edtAddExpenses.setError("Invalid number");
            }
        }

        // Also update the summary section
        tvSubtotal.setText("Subtotal: R" + total);
        tvDiscount.setText("Discount: R0");  // You can later add real discount logic if needed
        tvFinalTotal.setText("Final Total: R" + total);

        // Show total
        tvTotalCost.setText("Total: R" + total);
    }


    //Method for saving the trip (part of 3.5) and (5.2)
    private void saveTrip() {
        Database dbHelper = new Database(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String destination = edtDestination.getText().toString().trim();
        String notes = edtNotes.getText().toString().trim();
        String date = btndatePicker.getText().toString();

        ArrayList<String> chosenActivities = new ArrayList<>();
        double total = 0;
        for (int i = 0; i < outdoorActivities.length; i++) {
            if (lvOutdoorActivites.isItemChecked(i)) {
                chosenActivities.add(outdoorActivities[i]);
                total += activityCost[i];
            }
        }

        double customExpense = 0;
        if (!edtCustomExpenses.getText().toString().trim().isEmpty()) {
            customExpense = Double.parseDouble(edtCustomExpenses.getText().toString().trim());
            total += customExpense;
        }

        double addExpense = 0;
        if (!edtAddExpenses.getText().toString().trim().isEmpty()) {
            addExpense = Double.parseDouble(edtAddExpenses.getText().toString().trim());
            total += addExpense;
        }

        tvSubtotal.setText("Subtotal: R" + total);
        tvDiscount.setText("Discount: R0");
        tvFinalTotal.setText("Final Total: R" + total);

        String summary = "Destination: " + destination +
                "\nNotes: " + notes +
                "\nDate: " + date +
                "\nActivities: " + chosenActivities +
                "\nTotal: R" + total;

        // Insert into DB
        db.execSQL("INSERT INTO " + Database.TABLE_TRIPS +
                        " (" + Database.COL_DESTINATION + ", " +
                        Database.COL_NOTES + ", " +
                        Database.COL_DATE + ", " +
                        Database.COL_ACTIVITIES + ", " +
                        Database.COL_CUSTOM_EXPENSE + ", " +
                        Database.COL_ADD_EXPENSE + ", " +
                        Database.COL_TOTAL_COST + ", " +
                        Database.COL_SUMMARY + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{destination, notes, date, chosenActivities.toString(),
                        customExpense, addExpense, total, summary});

        Toast.makeText(this, "Trip saved!", Toast.LENGTH_SHORT).show();
    }
}