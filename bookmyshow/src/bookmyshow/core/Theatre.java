package bookmyshow.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Theatre {
    private final String name;
    private final String id;
    private final List<Screen> screens;

    public Theatre(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString(); // Generate a unique ID for the theatre
        this.screens = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Screen> getScreens() {
        return screens;
    }

    public boolean addScreen(Screen screen) {
        for (Screen existingScreen : screens) {
            if (existingScreen.getId().equals(screen.getId())) {
                return false; // Screen already exists
            }
        }
        screens.add(screen);
        return true; // Screen added successfully
    }
}
