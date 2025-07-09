package com.example.lunarphase;

import android.util.Log;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;


import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FactsFragment extends Fragment {

    private static final String TAG = "FactsFragment";
    VideoView videoBackground;
    EditText etusername , etUserFact;
    TextView tvFactDescription;
    AppCompatButton btnAddFact, btnSubmitFact;
    CardView factCard;
    Dialog add_fact_dialog;
    ImageView thumbUp, thumbDown;
    LottieAnimationView swipeUpAnimation;
    private boolean isThumbsUpSelected = false;
    private boolean isThumbsDownSelected = false;


    private DatabaseReference db;
    private List<String> factList;
    private int currentFactIndex = 0;
    private GestureDetector gestureDetector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;


    public FactsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_facts, container, false);

    }


    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseDatabase.getInstance().getReference();

        add_fact_dialog = new Dialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_fact_dialog, null);
        add_fact_dialog.setContentView(dialogView);
        add_fact_dialog.setCancelable(true);
        Window window = add_fact_dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }


        videoBackground = view.findViewById(R.id.videoBackground);
        tvFactDescription = view.findViewById(R.id.tvFactDescription);
        btnAddFact =  view.findViewById(R.id.btnAddFact);
        etusername = dialogView.findViewById(R.id.etusername);
        etUserFact = dialogView.findViewById(R.id.etUserFact);
        btnSubmitFact = dialogView.findViewById(R.id.btnSubmitFact);
        factCard = view.findViewById(R.id.factCard);
        thumbUp = view.findViewById(R.id.thumbUp);
        thumbDown = view.findViewById(R.id.thumbDown);
        swipeUpAnimation = view.findViewById(R.id.swipeUpAnimation);

        VideoView videoBackground = view.findViewById(R.id.videoBackground);
        BackgroundUtil.applyBackground(requireContext(), view, videoBackground);



        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (swipeUpAnimation != null) {
                    swipeUpAnimation.setVisibility(View.VISIBLE);
                    swipeUpAnimation.setRepeatCount(2);
                    swipeUpAnimation.playAnimation();
                } else {
                    Log.e(TAG, "swipeUpAnimation is null!");
                }
        }, 2500);


        btnAddFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_fact_dialog.show();
            }
        });

        btnSubmitFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = etusername.getText().toString().trim();
                String userFact = etUserFact.getText().toString().trim();

                if (userName.isEmpty()) {
                    etusername.setError("Please enter your name");
                    etusername.requestFocus();
                    return;
                }

                if (userFact.isEmpty()) {
                    etUserFact.setError("Please write your fact");
                    etUserFact.requestFocus();
                    return;
                }

                // Create model and push to database under user's name
                Fact fact = new Fact(userName, userFact);
                db.child("facts").child("pending_facts").child(userName).push().setValue(fact);


                // Optional: Clear fields and dismiss
                etusername.setText("");
                etUserFact.setText("");
                add_fact_dialog.dismiss();
            }
        });

        thumbUp.setOnClickListener(v -> {
            if (!isThumbsUpSelected) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                v.animate()
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .setDuration(150)
                        .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(250).start())
                        .start();
                thumbUp.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                thumbDown.clearColorFilter();
                isThumbsUpSelected = true;
                isThumbsDownSelected = false;
            } else {
                thumbUp.clearColorFilter();
                isThumbsUpSelected = false;
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });

        thumbDown.setOnClickListener(v -> {
            if (!isThumbsDownSelected) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                v.animate()
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .setDuration(150)
                        .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(250).start())
                        .start();
                thumbDown.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                thumbUp.clearColorFilter();
                isThumbsDownSelected = true;
                isThumbsUpSelected = false;
            } else {
                thumbDown.clearColorFilter();
                isThumbsDownSelected = false;
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });


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

        factList = new ArrayList<>();
        factList.addAll(Arrays.asList(
                getString(R.string.fact_1),
                getString(R.string.fact_2),
                getString(R.string.fact_3),
                getString(R.string.fact_4),
                getString(R.string.fact_5),
                getString(R.string.fact_6),
                getString(R.string.fact_7),
                getString(R.string.fact_8),
                getString(R.string.fact_9),
                getString(R.string.fact_10),
                getString(R.string.fact_11),
                getString(R.string.fact_12),
                getString(R.string.fact_13),
                getString(R.string.fact_14),
                getString(R.string.fact_15)
));

// Load approved facts from Firebase
        DatabaseReference approvedRef = db.child("facts").child("approved_facts");
        approvedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot factSnap : snapshot.getChildren()) {
                    String factText = factSnap.getValue(String.class);
                    if (factText != null) {
                        factList.add(factText);
                    }
                }
                // Show the first fact after all loading is done
                tvFactDescription.setText(factList.get(currentFactIndex));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        tvFactDescription = view.findViewById(R.id.tvFactDescription);
        tvFactDescription.setText(factList.get(currentFactIndex));

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY < 0) {
                        showNextFact();
                        // Swipe up
                    } else {
                        showPreviousFact();
                        // Swipe down
                    }
                    return true;
                }
                return false;
            }
        });

        factCard.setOnTouchListener((v, event) -> {
            if (gestureDetector.onTouchEvent(event)) {
                v.performClick();
                return true;
            }
            return false;
        });

    }
    private void showNextFact() {
        if (currentFactIndex < factList.size() - 1) {
            factCard.animate()
                    .translationY(-factCard.getHeight())  // slide up
                    .alpha(0f)
                    .setDuration(250)
                    .withEndAction(() -> {
                        currentFactIndex++;
                        tvFactDescription.setText(factList.get(currentFactIndex));
                        factCard.setTranslationY(factCard.getHeight()); // reset position below
                        factCard.animate()
                                .translationY(0)
                                .alpha(1f)
                                .setDuration(250)
                                .start();

                        isThumbsUpSelected = false;
                        isThumbsDownSelected = false;
                        thumbUp.clearColorFilter();
                        thumbDown.clearColorFilter();
                    })
                    .start();
        }
    }

    private void showPreviousFact() {
        if (currentFactIndex > 0) {
            factCard.animate()
                    .translationY(factCard.getHeight())  // slide down
                    .alpha(0f)
                    .setDuration(250)
                    .withEndAction(() -> {
                        currentFactIndex--;
                        tvFactDescription.setText(factList.get(currentFactIndex));
                        factCard.setTranslationY(-factCard.getHeight()); // reset position above
                        factCard.animate()
                                .translationY(0)
                                .alpha(1f)
                                .setDuration(250)
                                .start();


                        isThumbsUpSelected = false;
                        isThumbsDownSelected = false;
                        thumbUp.clearColorFilter();
                        thumbDown.clearColorFilter();
                    })
                    .start();
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