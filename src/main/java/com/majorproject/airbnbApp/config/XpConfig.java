package com.majorproject.airbnbApp.config;

import org.springframework.stereotype.Service;

@Service
public class XpConfig {

    // XP awarded per event — tweak these anytime, Spring manages the bean
    public final int XP_PER_BOOKING = 100;
    public final int XP_PER_NEW_PLACE = 200;

    public static int calculateLevel(int totalXp) {
        return (int) Math.floor(Math.sqrt(totalXp / 100.0)) + 1;
    }

    public static String getRankTitle(int level) {
        if (level >= 20) return "Legendary Explorer";
        if (level >= 15) return "Elite Traveler";
        if (level >= 10) return "Master Voyager";
        if (level >= 7)  return "Seasoned Adventurer";
        if (level >= 5)  return "Explorer";
        if (level >= 3)  return "Wanderer";
        return "Newcomer";
    }
}
