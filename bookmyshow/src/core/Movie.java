package bookmyshow.src.core;

import java.util.UUID;

public class Movie {
    private final String id;
    private String name;
    private Integer durationInMinutes;

    public Movie(String name, Integer durationInMinutes) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.durationInMinutes = durationInMinutes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }
}
