package com.termproject.travelersjournal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class DatePicker extends AppCompatActivity {
    public CalendarView calendarView;
    public TextView d;
    public Button enterDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        calendarView = findViewById(R.id.calendarView);
        d = findViewById(R.id.selectedDate);

        enterDateButton = findViewById(R.id.enterDateButton);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = month + 1 + "/" + dayOfMonth + "/" + year;
                d.setText(selectedDate);
            }
        });

        enterDateButton.setOnClickListener(v -> {
            String str = d.getText().toString();
            Intent intent = new Intent(DatePicker.this, JournalEntry.class);
            intent.putExtra("date", str);
            startActivity(intent);
            finish();
        });

    }
}