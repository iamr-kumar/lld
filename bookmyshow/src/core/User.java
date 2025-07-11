package bookmyshow.src.core;

public class User {
    private final String id;
    private String name;
    private String email;

    public User(String name, String email) {
        this.id = java.util.UUID.randomUUID().toString(); // Using UUID for unique ID
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
