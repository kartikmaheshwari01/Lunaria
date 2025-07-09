package com.example.lunarphase;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import android.graphics.drawable.Drawable;


public class MoreFragment extends Fragment {

    VideoView videoBackground;
    AppCompatButton btnRate, btnContact, btnFaq, btnPrivacy, btnCloseApp, btnLater, btnSubmit;
    SwitchCompat switchNotifications, switchLightMode;
    RatingBar ratingBar;
    Dialog dialog;
    private static final int GALLERY_REQUEST_CODE = 101;

    public MoreFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        videoBackground = view.findViewById(R.id.videoBackground);
        btnRate = view.findViewById(R.id.btnRate);
        switchNotifications = view.findViewById(R.id.switchNotifications);
        switchLightMode = view.findViewById(R.id.switchLightMode);
        btnCloseApp = view.findViewById(R.id.btnCloseApp);
        btnContact = view.findViewById(R.id.btnContact);
        btnPrivacy = view.findViewById(R.id.btnPrivacy);
        btnFaq = view.findViewById(R.id.btnFaq);
        AppCompatButton btnChangeBackground = view.findViewById(R.id.btnChangeBackground);

        btnRate.setOnClickListener(v -> showRatingDialog());
        btnPrivacy.setOnClickListener(v -> showPrivacyDialog());
        btnFaq.setOnClickListener(v -> showFaqTextDialog());
        btnChangeBackground.setOnClickListener(v -> showBackgroundDialog());
        btnCloseApp.setOnClickListener(v -> requireActivity().finishAffinity());
        btnContact.setOnClickListener(v -> {
            String email = "kumarkartik955@gmail.com";
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + email));
            try {
                startActivity(Intent.createChooser(intent, "Send Email"));
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(getContext(), "No email app found.", Toast.LENGTH_SHORT).show();
            }
        });

        // Load previously saved background
        SharedPreferences prefs = requireContext().getSharedPreferences("BackgroundPrefs", Context.MODE_PRIVATE);
        String selectedBg = prefs.getString("backgroundType", "video");
        applyBackground(selectedBg);

        SharedPreferences pref = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean notificationsEnabled = pref.getBoolean("notifications_enabled", true);

// Disable the switch if permission is blocked (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                switchNotifications.setEnabled(false);
                Toast.makeText(requireContext(), "Enable notification permission in settings to use this feature", Toast.LENGTH_LONG).show();
                return;
            }
        }

        switchNotifications.setChecked(notificationsEnabled);

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            Toast.makeText(requireContext(),
                    isChecked ? "Notifications enabled" : "Notifications disabled",
                    Toast.LENGTH_SHORT).show();
        });



    }

    private void applyBackground(String bgType) {
        if (getView() == null) return;

        if (bgType.equals("video")) {
            videoBackground.setVisibility(View.VISIBLE);
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
        } else {
            videoBackground.setVisibility(View.GONE);
            switch (bgType) {
                case "static1":
                    getView().setBackgroundResource(R.drawable.static_bg_1);
                    break;
                case "static2":
                    getView().setBackgroundResource(R.drawable.static_bg_2);
                    break;
                case "static3":
                    getView().setBackgroundResource(R.drawable.static_bg_3);
                    break;
                case "static4":
                    getView().setBackgroundResource(R.drawable.static_bg_4);
                    break;
                case "gallery":
                    SharedPreferences prefs = requireContext().getSharedPreferences("BackgroundPrefs", Context.MODE_PRIVATE);
                    String uriString = prefs.getString("galleryImageUri", null);
                    if (uriString != null) {
                        Uri imageUri = Uri.parse(uriString);
                        Glide.with(this)
                                .load(imageUri)
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        if (getView() != null) {
                                            getView().setBackground(resource);
                                            videoBackground.setVisibility(View.GONE);
                                        }
                                    }
                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        // Optional: Handle if needed
                                    }
                                });
                    }
                    break;
            }
        }
    }

    private ViewGroup.LayoutParams getScaledLayout(VideoView videoView) {
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        int screenWidth = videoView.getWidth();
        int screenHeight = videoView.getHeight();
        float screenRatio = (float) screenWidth / screenHeight;

        // Use fallback ratio if videoView has no size yet
        int videoWidth = videoView.getMeasuredWidth() > 0 ? videoView.getMeasuredWidth() : 1920;
        int videoHeight = videoView.getMeasuredHeight() > 0 ? videoView.getMeasuredHeight() : 1080;

        float videoRatio = (float) videoWidth / videoHeight;

        if (videoRatio > screenRatio) {
            lp.width = (int) (screenHeight * videoRatio);
            lp.height = screenHeight;
        } else {
            lp.width = screenWidth;
            lp.height = (int) (screenWidth / videoRatio);
        }
        return lp;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                SharedPreferences prefs = requireContext().getSharedPreferences("BackgroundPrefs", Context.MODE_PRIVATE);
                prefs.edit().putString("galleryImageUri", selectedImage.toString()).putString("backgroundType", "gallery").apply();
                applyBackground("gallery");
            }
        }
    }

    private void showRatingDialog() {
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.rate_app_dialog);
        dialog.setCancelable(true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        ratingBar = dialog.findViewById(R.id.ratingBar);
        btnLater = dialog.findViewById(R.id.btnLater);
        btnSubmit = dialog.findViewById(R.id.btnSubmit);

        btnLater.setOnClickListener(v -> dialog.dismiss());
        btnSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            if (rating == 0f) {
                Toast.makeText(getContext(), "Please select a rating.", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("AppRatings");
            String uniqueId = UUID.randomUUID().toString();
            dbRef.child(uniqueId).setValue(rating)
                    .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Thanks for rating!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to submit rating.", Toast.LENGTH_SHORT).show());
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showFaqTextDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.faq_custom_dialog);
        dialog.setCancelable(true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView faqTextView = dialog.findViewById(R.id.faqTextView);
        AppCompatButton btnClose = dialog.findViewById(R.id.btnCloseFaqText);

        try {
            InputStream is = requireContext().getAssets().open("faq.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            faqTextView.setText(new String(buffer));
        } catch (IOException e) {
            faqTextView.setText("Unable to load FAQs.");
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showPrivacyDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_privacy);
        dialog.setCancelable(true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        WebView webView = dialog.findViewById(R.id.webViewPrivacy);
        webView.loadUrl("file:///android_asset/privacy_policy.html");

        AppCompatButton btnClose = dialog.findViewById(R.id.btnClosePrivacy);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showBackgroundDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.change_bg_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.setCancelable(true);

        SharedPreferences prefs = requireContext().getSharedPreferences("BackgroundPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String currentSelection = prefs.getString("backgroundType", "video");

        // Get all ImageViews
        ImageView[] options = {
                dialog.findViewById(R.id.bg_video),
                dialog.findViewById(R.id.bg1),
                dialog.findViewById(R.id.bg2),
                dialog.findViewById(R.id.bg3),
                dialog.findViewById(R.id.bg4),
                dialog.findViewById(R.id.bgGallery)
        };

        String[] types = {"video", "static1", "static2", "static3", "static4", "gallery"};

        // Set initial selection states
        for (int i = 0; i < options.length; i++) {
            View parent = (View) options[i].getParent();
            parent.setBackgroundResource(R.drawable.selected_border);// FrameLayout
            if (types[i].equals("video")) { // Always make video selected initially
                parent.setBackgroundResource(R.drawable.selected_border);
            } else {
                parent.setBackgroundResource(R.drawable.unselected_boarder);
            }
        }

        // Set click listeners
        for (int i = 0; i < options.length; i++) {
            final int index = i;
            ImageView option = options[i];

            option.setOnClickListener(v -> {
                // Save selected background
                editor.putString("backgroundType", types[index]).apply();

                // Special case for gallery
                if (types[index].equals("gallery")) {
                    pickImageFromGallery();
                } else {
                    applyBackground(types[index]);
                }

                // Update all borders
                for (int j = 0; j < options.length; j++) {
                    View parent = (View) options[j].getParent(); // FrameLayout
                    if (j == index) {
                        parent.setBackgroundResource(R.drawable.selected_border);
                    } else {
                        parent.setBackgroundResource(R.drawable.unselected_boarder);
                    }
                }
                dialog.dismiss();
            });
        }
        AppCompatButton btnClose = dialog.findViewById(R.id.btnCloseDialog);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoBackground != null && videoBackground.getVisibility() == View.VISIBLE && !videoBackground.isPlaying()) {
            videoBackground.start();
        }
    }
}

