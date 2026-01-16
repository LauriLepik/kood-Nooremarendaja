package extended;

import java.util.Random;

// Provides application branding and randomized user messages.
public class Branding {

    public static final String COMPANY = "Anywhere Holidays";
    public static final String TAGLINE = "Your journey starts here";
    public static final String URL = "nooremarendaja.kood.tech";

    private static final String[] WISHES = {
        "Have a wonderful trip! ✈️",
        "Safe travels! 🌍",
        "Enjoy your adventure! 🗺️",
        "Bon voyage! ✈️",
        "Wishing you clear skies! ☀️",
        "Have a fantastic journey! 🌟",
        "Travel safe, travel happy! 😊",
        "Adventure awaits! 🧳",
        "Fly high, dream big! ✨",
        "Happy travels! 🛫"
    };

    private static final Random random = new Random();

    public static String getRandomWish() {
        return WISHES[random.nextInt(WISHES.length)];
    }

    public static String[] getAllWishes() {
        return WISHES.clone();
    }
}
