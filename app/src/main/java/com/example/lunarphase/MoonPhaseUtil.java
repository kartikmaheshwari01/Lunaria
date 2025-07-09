package com.example.lunarphase;

import java.util.Calendar;

public class MoonPhaseUtil {

    private static final double SYNODIC_MONTH = 29.530588853;
    private static final double KNOWN_NEW_MOON_JD = 2451550.1; // Jan 6, 2000

    public static final String[] PHASE_NAMES = {
            "New Moon", "Waxing Crescent", "First Quarter", "Waxing Gibbous",
            "Full Moon", "Waning Gibbous", "Third Quarter", "Waning Crescent"
    };

    public static double getJulianDate(int year, int month, int day) {
        if (month <= 2) {
            year--;
            month += 12;
        }
        int A = year / 100;
        int B = 2 - A + A / 4;
        return Math.floor(365.25 * (year + 4716))
                + Math.floor(30.6001 * (month + 1))
                + day + B - 1524.5;
    }

    public static int getPhaseIndex(int year, int month, int day) {
        double jd = getJulianDate(year, month, day);
        double daysSinceNew = jd - KNOWN_NEW_MOON_JD;
        double newMoons = daysSinceNew / SYNODIC_MONTH;
        double phaseFraction = newMoons - Math.floor(newMoons);
        return (int) Math.floor(phaseFraction * 8) % 8;
    }

    public static double getMoonAge(int year, int month, int day) {
        double jd = getJulianDate(year, month, day);
        double daysSinceNew = jd - KNOWN_NEW_MOON_JD;
        double age = daysSinceNew % SYNODIC_MONTH;
        if (age < 0) age += SYNODIC_MONTH;
        return age;
    }

    public static String getTodayMoonPhaseName() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int index = getPhaseIndex(year, month, day);
        return PHASE_NAMES[index];
    }

    public static Calendar getNextMajorPhaseDate() {
        Calendar calendar = Calendar.getInstance();

        for (int i = 1; i <= 30; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            int phaseIndex = getPhaseIndex(year, month, day);
            if (phaseIndex == 0 || phaseIndex == 2 || phaseIndex == 4 || phaseIndex == 6) {
                return (Calendar) calendar.clone();
            }
        }
        return null;
    }

    public static String getNextMajorPhaseName() {
        Calendar next = getNextMajorPhaseDate();
        if (next != null) {
            int year = next.get(Calendar.YEAR);
            int month = next.get(Calendar.MONTH) + 1;
            int day = next.get(Calendar.DAY_OF_MONTH);
            int index = getPhaseIndex(year, month, day);
            return PHASE_NAMES[index];
        }
        return "Unknown";
    }
}
