package com.example.lunarphase;

//Imports for checking Internet Connection
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lunarphase.EventModel;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.Toast;

//Imports for using external Kizitonwose Library
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

//Imports for using RealTime FireBase
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Imports for using res/raw/events.json
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EventsFragment extends Fragment {

    VideoView videoBackground;
    CalendarView calendarView;
    TextView tvMonthYear;
    ImageView btnPreviousMonth, btnNextMonth;
    AppCompatButton btnLunar, btnSolar, btnMeteor, btnPlanetary;
    private DatabaseReference db;
    CardView eventDetailsCard;
    TextView tvEventName, tvEventDate, tvEventTime, tvEventType, tvEventDescription, tvEventVisibility;


    private List<EventModel> allEvents = new ArrayList<>();
    private String currentFilter = "Lunar"; // Default filter
    private YearMonth currentMonth;
    private LocalDate selectedDate = null; // To keep track of the currently selected date

    private Set<LocalDate> markedDates = new HashSet<>();

    public EventsFragment() {
        // Required empty public constructor
    }

    // --- INNER CLASSES FOR CALENDARVIEW BINDERS ---

    // Day View Container (corresponds to calendar_day_layout.xml)
    private static class DayViewContainer extends ViewContainer {
        public TextView dayText;
        public View dotView; // Assuming eventDotView in your XML
        public ImageView eventIcon; // Assuming eventIcon in your XML

        public DayViewContainer(@NonNull View view) {
            super(view);
            dayText = view.findViewById(R.id.calendarDayText);
            dotView = view.findViewById(R.id.eventDotView);
            eventIcon = view.findViewById(R.id.eventIcon);
        }
    }

    // Month Header Container (for day of week labels, corresponds to month_header_layout.xml)
    private static class MonthHeaderContainer extends ViewContainer {
        public TextView tvDayOne, tvDayTwo, tvDayThree, tvDayFour, tvDayFive, tvDaySix, tvDaySeven;

        public MonthHeaderContainer(@NonNull View view) {
            super(view);
            tvDayOne = view.findViewById(R.id.tvDayOne);
            tvDayTwo = view.findViewById(R.id.tvDayTwo);
            tvDayThree = view.findViewById(R.id.tvDayThree);
            tvDayFour = view.findViewById(R.id.tvDayFour);
            tvDayFive = view.findViewById(R.id.tvDayFive);
            tvDaySix = view.findViewById(R.id.tvDaySix);
            tvDaySeven = view.findViewById(R.id.tvDaySeven);
        }
    }

    // --- FRAGMENT LIFECYCLE ---
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseDatabase.getInstance().getReference("events");

        calendarView = view.findViewById(R.id.calendarView);
        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        btnPreviousMonth = view.findViewById(R.id.btnPreviousMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        btnLunar = view.findViewById(R.id.btnLunar);
        btnSolar = view.findViewById(R.id.btnSolar);
        btnMeteor = view.findViewById(R.id.btnMeteor);
        btnPlanetary = view.findViewById(R.id.btnPlanetary);
        videoBackground = view.findViewById(R.id.videoBackground);


        eventDetailsCard = view.findViewById(R.id.eventDetailsCard);
        tvEventName = view.findViewById(R.id.tvEventName);
        tvEventDate = view.findViewById(R.id.tvEventDate);
        tvEventTime = view.findViewById(R.id.tvEventTime);
        tvEventType = view.findViewById(R.id.tvEventType);
        tvEventDescription = view.findViewById(R.id.tvEventDescription);
        tvEventVisibility = view.findViewById(R.id.tvEventVisibility);

        VideoView videoBackground = view.findViewById(R.id.videoBackground);
        BackgroundUtil.applyBackground(requireContext(), view, videoBackground);



        setupVideoBackground();

        loadEvents();

        // Setup the CalendarView
        setupCalendar();

        // Set up button listeners
        btnLunar.setOnClickListener(v -> {
            updateCalendar("Lunar");
            updateButtonSelection(btnLunar);
        });

        btnSolar.setOnClickListener(v -> {
            updateCalendar("Solar");
            updateButtonSelection(btnSolar);
        });

        btnMeteor.setOnClickListener(v -> {
            updateCalendar("Meteor");
            updateButtonSelection(btnMeteor);
        });

        btnPlanetary.setOnClickListener(v -> {
            updateCalendar("Planetary");
            updateButtonSelection(btnPlanetary);
        });

        btnPreviousMonth.setOnClickListener(v -> {
            currentMonth = currentMonth.minusMonths(1);
            calendarView.scrollToMonth(currentMonth);
        });
        btnNextMonth.setOnClickListener(v -> {
            currentMonth = currentMonth.plusMonths(1);
            calendarView.scrollToMonth(currentMonth);
        });
        // Initial update of calendar markers for the default filter
        updateCalendarMarkers();
        updateButtonSelection(btnLunar);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoBackground != null) {
            videoBackground.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoBackground != null) {
            videoBackground.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (videoBackground != null) {
            videoBackground.stopPlayback();
        }
    }

    // --- VIDEO BACKGROUND SETUP ---
    private void setupVideoBackground() {
        Uri videoUri = Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.bg_back);
        videoBackground.setVideoURI(videoUri);
        videoBackground.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f);

            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();
            float videoRatio = (float) videoWidth / videoHeight;

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            float screenRatio = (float) screenWidth / screenHeight;

            ViewGroup.LayoutParams lp = videoBackground.getLayoutParams();
            if (videoRatio > screenRatio) {
                lp.width = (int) (screenHeight * videoRatio);
                lp.height = screenHeight;
            } else {
                lp.width = screenWidth;
                lp.height = (int) (screenWidth / videoRatio);
            }
            videoBackground.setLayoutParams(lp);

            videoBackground.start(); // Start after setting size
        });
    }

    // --- CALENDAR SETUP ---

    private void setupCalendar() {
        LocalDate today = LocalDate.now();
        currentMonth = YearMonth.from(today);

        // Define the calendar range
        YearMonth startMonth = YearMonth.of(2025,1);
        YearMonth endMonth = YearMonth.of(2030,12);
        DayOfWeek firstDayOfWeek = DayOfWeek.SUNDAY; // Or DayOfWeek.MONDAY, etc.

        // Setup the CalendarView
        calendarView.setup(startMonth, endMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);

        // Set the DayBinder
        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, @NonNull CalendarDay day) {
                container.dayText.setText(String.valueOf(day.getDate().getDayOfMonth()));

                if (day.getPosition() == DayPosition.MonthDate) {
                    container.dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                    // Determine background color based on current event filter
                    int bgRes = R.drawable.selected_day_background; // default fallback
                    switch (currentFilter.toLowerCase()) {
                        case "lunar": bgRes = R.drawable.bg_lunar_day; break;
                        case "solar": bgRes = R.drawable.bg_solar_day; break;
                        case "meteor": bgRes = R.drawable.bg_meteor_day; break;
                        case "planetary": bgRes = R.drawable.bg_planetary_day; break;
                    }

                    // Apply background and color logic
                    if (selectedDate != null && selectedDate.equals(day.getDate())) {
                        container.dayText.setBackgroundResource(R.drawable.selected_day_background); // Selected is always a highlight
                        container.dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                    } else if (markedDates.contains(day.getDate())) {
                        container.dayText.setBackgroundResource(bgRes); // Based on event type
                        container.dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                    } else {
                        container.dayText.setBackgroundResource(0); // No background
                        container.dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                    }
                    // Handle click on day
                    container.getView().setOnClickListener(v -> {
                        LocalDate clicked = day.getDate();
                        if (!clicked.equals(selectedDate)) {
                            LocalDate old = selectedDate;
                            selectedDate = clicked;
                            if (old != null) calendarView.notifyDateChanged(old);
                            calendarView.notifyDateChanged(clicked);
                            displayEventDetails(selectedDate);
                        } else {
                            calendarView.notifyDateChanged(clicked);
                            selectedDate = null;
                            hideEventDetails();
                        }
                    });

                } else {
                    // Grey out days outside current month
                    container.dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_faded));
                    container.dayText.setBackgroundResource(0);
                    container.getView().setOnClickListener(null);
                }
            }
        });

        // Set the Month Header Binder (for day of week labels)
        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthHeaderContainer>() {
            @NonNull
            @Override
            public MonthHeaderContainer create(@NonNull View view) {
                return new MonthHeaderContainer(view);
            }

            @Override
            public void bind(@NonNull MonthHeaderContainer container, @NonNull CalendarMonth month) {
                TextView[] dayTextViews = {
                        container.tvDayOne, container.tvDayTwo, container.tvDayThree,
                        container.tvDayFour, container.tvDayFive, container.tvDaySix,
                        container.tvDaySeven
                };

                DayOfWeek[] daysOfWeekInOrder = getDaysOfWeekInOrder(firstDayOfWeek);
                for (int i = 0; i < dayTextViews.length; i++) {
                    dayTextViews[i].setText(daysOfWeekInOrder[i].getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
                }
            }
        });

        // Set the Month Scroll Listener to update the header TextView
        // Corrected usage of MonthScrollListener with a lambda or anonymous class
        calendarView.setMonthScrollListener(month -> {
            currentMonth = month.getYearMonth();
            updateMonthLabel();
            hideEventDetails();
            selectedDate = null;
            calendarView.notifyCalendarChanged();
            return kotlin.Unit.INSTANCE;
        });

        // Initial update of the month label
        updateMonthLabel();
    }

    // Helper to get days of week in the correct order for header
    private DayOfWeek[] getDaysOfWeekInOrder(DayOfWeek firstDayOfWeek) {
        DayOfWeek[] allDays = DayOfWeek.values(); // MONDAY, TUESDAY, ..., SUNDAY
        List<DayOfWeek> reorderedDays = new ArrayList<>();
        int startIndex = firstDayOfWeek.getValue() - 1; // DayOfWeek.MONDAY.getValue() is 1, SUNDAY is 7

        for (int i = 0; i < 7; i++) {
            reorderedDays.add(allDays[(startIndex + i) % 7]);
        }
        return reorderedDays.toArray(new DayOfWeek[0]);
    }

    // --- CALENDAR UPDATE AND FILTERING ---
    private void updateCalendar(String filter) {
        currentFilter = filter;
        selectedDate = null; // Clear selection when filter changes
        hideEventDetails();  // Hide details when filter changes
        updateCalendarMarkers(); // Recalculate markedDates and redraw calendar
    }

    private void updateCalendarMarkers() {
        markedDates.clear(); // Clear previous marked dates

        // Populate markedDates based on the currentFilter
        for (EventModel event : allEvents) {
            if (event.getType().equalsIgnoreCase(currentFilter)) {
                markedDates.add(event.getDate());
            }
        }
        calendarView.notifyCalendarChanged(); // Notify the calendar to re-bind all visible days
    }

    private void updateMonthLabel() {
        String monthYear = currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + currentMonth.getYear();
        tvMonthYear.setText(monthYear);
    }

    // --- EVENT DATA MANAGEMENT AND DISPLAY ---

    private void displayEventDetails(LocalDate date) {
        EventModel eventToShow = null;
        for (EventModel event : allEvents) {
            if (event.getDate().equals(date) && event.getType().equalsIgnoreCase(currentFilter)) {
                eventToShow = event;
                break;
            }
        }

        if (eventToShow != null) {
            eventDetailsCard.setVisibility(View.VISIBLE);
            tvEventName.setText(eventToShow.getName());
            tvEventDate.setText("Date: " + eventToShow.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            tvEventTime.setText("Time: " + eventToShow.getTime() +" PST");
            tvEventType.setText("Type: " + eventToShow.getType());
            tvEventDescription.setText("Description: " +eventToShow.getDescription());
            tvEventVisibility.setText("Region: " + eventToShow.getVisibilityRegion());
            tvEventVisibility.setVisibility(View.VISIBLE);
        } else {
            hideEventDetails();
            Toast.makeText(requireContext(), "No " + currentFilter + " event on " + date.format(DateTimeFormatter.ofPattern("MMM dd")), Toast.LENGTH_SHORT).show();
        }
    }
    private void updateButtonSelection(AppCompatButton selectedButton) {
        btnLunar.setSelected(false);
        btnSolar.setSelected(false);
        btnMeteor.setSelected(false);
        btnPlanetary.setSelected(false);

        selectedButton.setSelected(true);
    }

    private void hideEventDetails() {
        eventDetailsCard.setVisibility(View.GONE);
    }


    //Checking for Internet
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
    //If internet present then FireBase Database and if not then res/raw/events.json
    private void loadEvents() {
        if (isInternetAvailable()) {
            loadEventsFromFirebase();
        } else {
            loadEventsFromLocalJson();
        }
    }
    //Real Time FireBase Database
    private void loadEventsFromFirebase() {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allEvents.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String dateKey = ds.getKey();
                    LocalDate eventDate;
                    try {
                        eventDate = LocalDate.parse(dateKey);
                    } catch (Exception e) {
                        continue;
                    }

                    for (DataSnapshot ev : ds.getChildren()) {
                        String name = ev.child("name").getValue(String.class);
                        String type = ev.child("type").getValue(String.class);
                        String time = ev.child("time").getValue(String.class);
                        String region = ev.child("region").getValue(String.class);
                        String description = ev.child("description").getValue(String.class);

                        if (name == null || type == null || time == null || region == null || description == null)
                            continue;

                        allEvents.add(new EventModel(name, eventDate, time, type, description, region));
                    }
                }
                updateCalendarMarkers();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // res/raw/events.json
    private void loadEventsFromLocalJson() {
        allEvents.clear();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.events);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String dateKey = keys.next();
                LocalDate eventDate;
                try {
                    eventDate = LocalDate.parse(dateKey);
                } catch (Exception e) {
                    continue;
                }
                JSONArray eventArray = jsonObject.getJSONArray(dateKey);
                for (int i = 0; i < eventArray.length(); i++) {
                    JSONObject eventObj = eventArray.getJSONObject(i);
                    String name = eventObj.optString("name");
                    String type = eventObj.optString("type");
                    String time = eventObj.optString("time");
                    String region = eventObj.optString("region");
                    String description = eventObj.optString("description");

                    if (name.isEmpty() || type.isEmpty() || time.isEmpty() || region.isEmpty() || description.isEmpty())
                        continue;

                    allEvents.add(new EventModel(name, eventDate, time, type, description, region));
                }
            }
            updateCalendarMarkers();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to load offline events", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}