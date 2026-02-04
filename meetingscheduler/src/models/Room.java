package meetingscheduler.src.models;

import java.util.UUID;

import meetingscheduler.src.enums.RoomType;

public class Room {
    private final UUID id;
    private final int floor;
    private final int roomNumber;
    private final RoomType roomType;
    private final int capacity;

    public Room(int floor, int roomNumber, RoomType roomType, int capacity) {
        this.id = UUID.randomUUID();
        this.floor = floor;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = capacity;
    }

    public UUID getId() {
        return this.id;
    }

    public int getFloor() {
        return this.floor;
    }

    public int getRoomNumber() {
        return this.roomNumber;
    }

    public RoomType getRoomType() {
        return this.roomType;
    }

    public int getCapacity() {
        return this.capacity;
    }

}
