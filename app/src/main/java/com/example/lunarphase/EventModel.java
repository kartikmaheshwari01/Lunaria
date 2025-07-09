package com.example.lunarphase;

import java.time.LocalDate;

public class EventModel {
    private String name;
    private LocalDate date;
    private String time;
    private String type;
    private String description;
    private String visibilityRegion;

    public EventModel(String name, LocalDate date, String time, String type, String description, String visibilityRegion) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.type = type;
        this.description = description;
        this.visibilityRegion = visibilityRegion;
    }

    public String getName() { return name; }
    public LocalDate getDate() { return date; }
    public String getTime() { return time; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getVisibilityRegion() { return visibilityRegion; }
}
