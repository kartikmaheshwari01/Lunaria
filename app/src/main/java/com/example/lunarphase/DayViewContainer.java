package com.example.lunarphase;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kizitonwose.calendar.view.ViewContainer;

public class DayViewContainer extends ViewContainer {
    public final TextView dayText;
    public final View dotView;
    public final ImageView eventIcon;

    public DayViewContainer(View view) {
        super(view);
        dayText = view.findViewById(R.id.calendarDayText);
        dotView = view.findViewById(R.id.eventDotView);
        eventIcon = view.findViewById(R.id.eventIcon);
    }
}

