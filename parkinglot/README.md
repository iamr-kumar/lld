# Parking Lot Management System

## Overview

This document outlines the requirements and low-level design for a parking lot management system. The system handles vehicle entry, parking spot allocation, payment processing, and exit management.

## Basic Requirements

### Parking Structure

- Multiple floors in the parking lot
- Each floor contains multiple parking spots
- Each parking spot is designated for a specific vehicle type

### Vehicle Types Supported

- CAR
- BIKE
- OTHERS

## Core Functionality

### Vehicle Entry Process

1. Vehicle arrives at the parking lot
2. System checks for available parking spots for the specific vehicle type across floors
3. If available:
   - System generates a ticket with unique ID
   - System records entry timestamp
   - System assigns an available parking spot for the vehicle type
   - The assigned parking spot is marked as occupied
4. If not available:
   - System rejects the vehicle entry
   - Display "No parking available for this vehicle type"

### Vehicle Exit Process

1. Vehicle arrives at the exit with a ticket
2. System validates the ticket
3. System calculates the parking duration and fare
4. Driver selects payment method
5. Payment is processed
6. Exit is granted, and the parking spot is marked as available again

### Fare Calculation

- **Base fare**: Fixed amount for the first hour (varies by vehicle type)
- **Hourly fare**: Additional charge for each extra hour (varies by vehicle type)

```text
If parking duration ≤ 1 hour:
    Total fare = Base fare
If parking duration > 1 hour:
    Total fare = Base fare + (Hourly fare × (Number of extra hours))
```

### Payment Methods

- Cash
- Credit/Debit Card
- UPI (Unified Payment Interface)

## System Components

### Vehicle

- Vehicle ID (license plate)
- Vehicle type (CAR, BIKE, OTHERS)

### Parking Spot

- Spot ID
- Spot type (based on vehicle type)
- Status (available/occupied)
- Floor number

### Ticket

- Ticket ID
- Vehicle information
- Entry timestamp
- Assigned parking spot
- Status (active/paid)

### Payment

- Payment ID
- Ticket reference
- Payment amount
- Payment method
- Payment status
- Payment timestamp

## Design Patterns Used

### Singleton Pattern

- Used for the ParkingLot class to ensure only one instance of the parking lot exists in the system
- Provides a global point of access to the parking lot instance

### Strategy Pattern

- Applied for fee calculation mechanisms
- Different fee calculation strategies can be implemented based on various criteria (vehicle type, time-based, etc.)
- Allows for flexible and interchangeable fee calculation algorithms

### Factory Pattern

- Used for payment method creation
- Encapsulates the creation logic for different payment types
- Allows easy addition of new payment methods without changing existing code

## Future Extensions

### Multi-Level Parking Enhancement

- Dynamic floor allocation based on congestion
- Floor-specific restrictions (e.g., reserved floors for certain vehicle types)
- Priority allocation for premium customers

### Multiple Entry/Exit Points

- Support for multiple entry and exit points on each floor
- Distributed entry/exit processing
- Point-specific queuing and management

### Proximity-Based Spot Allocation

- Algorithms to assign the parking spot nearest to the entry point
- Distance calculation between entry points and available spots
- Optimization for user convenience

### Additional Extensions

- Reservation system for advance booking
- Membership/subscription models
- Integration with mobile applications
- Automated license plate recognition
- EV charging station integration
- Valet parking services

## Design Constraints

- The system should be highly available
- Response time for spot allocation should be minimal
- Real-time updates for parking spot availability
- Secure payment processing
- Data persistence for audit and analytics

## Technical Considerations

- Concurrency control for simultaneous parking operations
- Database design for efficient queries
- API design for potential external integrations
- Monitoring and alerting mechanisms
- Backup and disaster recovery procedures
