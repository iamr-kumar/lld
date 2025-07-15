# Fitso App Low Level Design

Design a low-level architecture for a fitness app similar to Cult Fit, focusing on onboarding centers, defining workout slots, and providing features for users to view, book, and cancel slots, with an optional notification system for slot availability.

## Requirements:

- The system must provide functionality to onboard fitness centers, capturing details such as center name, operating hours, and available workout variations.
- Each center can offer multiple workout types such as Weights, Cardio, Yoga, Swimming, etc., with the flexibility to add new workout types in the future.
- The system must allow center admins to define workout slots within the center's operating hours.
- Each slot will accommodate only one workout at a time, and the number of seats per slot is fixed for each center.
- For the current scope, admins can create these workout slots daily, limited to same-day scheduling, with no option for updates after creation.
- End users/customers can perform the following operations:

### Optional:

- Register on the platform (authentication is out of the current scope).
- View the availability of workout slots for the day, filtered by workout type and sorted by start time in ascending order.
- View the availability of workout slots for the day, filtered by workout type and center name, sorted by available seats in ascending order.
- Book a workout slot if seats are available at that time.
- Cancel a booked workout slot.

## Functional notes:-

- Concurrency Handling: Ensure the system can handle concurrent scenarios, such as multiple users attempting to book the same workout slot simultaneously.
- Day-based Operations: For simplicity, all operations are limited to a single day. However, design the system to be extensible so that expanding operations across multiple days requires minimal changes.
- Time Representation: Use an integer to represent time since the current scope is restricted to same-day operations.
- Unique Identifiers: Assign a unique identifier to each entity (e.g., center, workout type, or user), which can be a name or a unique ID, with the assumption that each will be unique within the system.
