package bookmyshow.core;

import java.util.Date;
import java.util.UUID;

public class Show {
    private final String id;
    private final Movie movie;
    private final Screen screen;
    private final Theatre theatre;
    private final Date startTime;

    public Show(Movie movie, Screen screen, Theatre theatre, Date startTime) {
        this.id = UUID.randomUUID().toString(); // Using hashCode of UUID for simplicity
        this.movie = movie;
        this.screen = screen;
        this.theatre = theatre;
        this.startTime = startTime;
    }

    public String getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Screen getScreen() {
        return screen;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    public Date getStartTime() {
        return startTime;
    }

}
