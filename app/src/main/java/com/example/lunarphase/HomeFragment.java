package com.example.lunarphase;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    final Calendar myCalender = Calendar.getInstance();
    EditText etSelectDate;
    TextView tvLocation, tvSelectedDate, tvMoonPhaseDescription, tvMoonAge, tvMoonrise, tvMoonset;
    ImageView ivMoonPhase;
    VideoView videoBackground;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSelectDate = view.findViewById(R.id.etSelectDate);
        tvLocation  =  view.findViewById(R.id.tvLocation);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvMoonPhaseDescription = view.findViewById(R.id.tvMoonPhaseDescription);
        ivMoonPhase = view.findViewById(R.id.ivMoonPhase);
        tvMoonAge = view.findViewById(R.id.tvMoonAge);
        tvMoonrise = view.findViewById(R.id.tvMoonrise);
        tvMoonset = view.findViewById(R.id.tvMoonset);
        videoBackground = view.findViewById(R.id.videoBackground);

        VideoView videoBackground = view.findViewById(R.id.videoBackground);
        BackgroundUtil.applyBackground(requireContext(), view, videoBackground);



        SimpleDateFormat selectedFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        tvSelectedDate.setText(selectedFormat.format(myCalender.getTime()));
        updateMoonImage(myCalender);

        etSelectDate.setOnClickListener(v -> new DatePickerDialog(requireContext(), (datePicker, year, month, dayOfMonth) -> {
            myCalender.set(Calendar.YEAR, year);
            myCalender.set(Calendar.MONTH, month);
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myformat = "dd-MMM-yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(myformat, Locale.US);
            etSelectDate.setText(dateFormat.format(myCalender.getTime()));

            tvSelectedDate.setText("Date: " + selectedFormat.format(myCalender.getTime()));


            updateMoonImage(myCalender);
        }, myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH), myCalender.get(Calendar.DAY_OF_MONTH)).show());

        Uri videoUri = Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.bg_back);
        videoBackground.setVideoURI(videoUri);
        videoBackground.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f);

            int screenWidth = videoBackground.getWidth();
            int screenHeight = videoBackground.getHeight();

            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();

            float screenRatio = (float) screenWidth / screenHeight;
            float videoRatio = (float) videoWidth / videoHeight;

            ViewGroup.LayoutParams lp = videoBackground.getLayoutParams();
            if (videoRatio > screenRatio) {
                lp.width = (int) (screenHeight * videoRatio);
                lp.height = screenHeight;
            } else {
                lp.width = screenWidth;
                lp.height = (int) (screenWidth / videoRatio);
            }
            videoBackground.setLayoutParams(lp);
        });

        videoBackground.setOnCompletionListener(mp -> videoBackground.start());
        videoBackground.start();
    }

    private void animateMoonImageChange(final int newResId) {
        Animation fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_moon);
        Animation fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_moon);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                ivMoonPhase.setImageResource(newResId);
                ivMoonPhase.startAnimation(fadeIn);
            }

            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
        });

        ivMoonPhase.startAnimation(fadeOut);
        new Handler().postDelayed(() -> {
            ivMoonPhase.setImageResource(newResId);
            ivMoonPhase.startAnimation(fadeIn);
        }, 250);
    }

     static final String[] PHASE_NAMES = {
            "New Moon", "Waxing Crescent", "First Quarter", "Waxing Gibbous", "Full Moon",
            "Waning Gibbous", "Third Quarter", "Waning Crescent"
    };

    private void updateMoonImage(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int phaseIndex = MoonPhaseUtil.getPhaseIndex(year, month, day);
        tvMoonPhaseDescription.setText("Phase: " + PHASE_NAMES[phaseIndex]);
        animateMoonImageChange(getMoonImage(phaseIndex));

        double moonAge = MoonPhaseUtil.getMoonAge(year, month, day);
        String ageText = String.format(Locale.US, "Moon Age: %.1f days", moonAge);
        tvMoonAge.setText(ageText);

        Calendar today = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {

            MainActivity activity = (MainActivity) requireActivity();
            double lat = activity.getLatitude();
            double lng = activity.getLongitude();
            fetchMoonTimes(lat, lng);

        } else {
            tvMoonrise.setText("Moonrise: --");
            tvMoonset.setText("Moonset: --");
        }
    }


    private int getMoonImage(int index) {
        int[] images = {
                R.drawable.phase_new,
                R.drawable.phase_waxing_crescent,
                R.drawable.phase_first_quarter,
                R.drawable.phase_waxing_gibbous,
                R.drawable.phase_full,
                R.drawable.phase_waning_gibbous,
                R.drawable.phase_third_quarter,
                R.drawable.phase_waning_crescent
        };
        return images[index];
    }
    private void fetchMoonTimes(double lat, double lng) {
        String apiKey = "6df6974f63f64c92a96923fd4cb2adf9";
        String url = "https://api.ipgeolocation.io/astronomy?apiKey=" + apiKey
                + "&lat=" + lat + "&long=" + lng;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String moonrise = response.getString("moonrise");
                        String moonset = response.getString("moonset");


                        tvMoonrise.setText("Moonrise: " + formatTime12Hr(moonrise));
                        tvMoonset.setText("Moonset: " + formatTime12Hr(moonset));
                    } catch (Exception e) {
                        e.printStackTrace();
                        tvMoonrise.setText("Moonrise: Error");
                        tvMoonset.setText("Moonset: Error");
                    }
                },
                error -> {
                    tvMoonrise.setText("Moonrise: Failed");
                    tvMoonset.setText("Moonset: Failed");
                });
        queue.add(request);
    }
    private String formatTime12Hr(String time24) {
        try {
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.US);
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.US);
            Date date = sdf24.parse(time24);
            return sdf12.format(date);
        } catch (Exception e) {
            return time24;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (videoBackground != null && !videoBackground.isPlaying()) {
            videoBackground.start();
        }
    }

}
