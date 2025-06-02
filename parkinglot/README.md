# Parking Lot Management System

## Overview

This document outlines the requirements and low-level design for a parking lot management system. The system handles vehicle entry, parking spot allocation, payment processing, and exit management.

## Basic Requirements

### Entry and Exit Points

- One entry gate
- One exit gate

### Vehicle Types Supported

- Two-wheeler (motorcycles, scooters)
- Three-wheeler (auto-rickshaws)
- Four-wheeler (cars, SUVs)
- Heavy vehicles (trucks, buses)

## Core Functionality

### Entry Gate Process

1. Vehicle arrives at the entry gate
2. System checks for available parking spots for the specific vehicle type
3. If available:
   - System generates a ticket with unique ID
   - System records entry timestamp
   - System assigns an available parking spot for the vehicle type
   - The assigned parking spot is marked as occupied
4. If not available:
   - System rejects the vehicle entry
   - Display "No parking available for this vehicle type"

### Exit Gate Process

1. Vehicle arrives at the exit gate with ticket
2. System validates the ticket
3. System calculates the parking duration and fare
4. Driver selects payment method
5. Payment is processed
6. Exit gate opens, and the parking spot is marked as available again

### Fare Calculation

- **Base fare**: Fixed amount for the first hour (varies by vehicle type)
- **Hourly fare**: Additional charge for each extra hour (varies by vehicle type)

```
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
- Vehicle type (two-wheeler, three-wheeler, four-wheeler, heavy)

### Parking Spot

- Spot ID
- Spot type (based on vehicle type)
- Status (available/occupied)
- Location coordinates

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

## Future Extensions

### Multi-Level Parking

- Support for multiple floors/levels
- Level-specific capacity management
- Digital indicators for available spots per level

### Multiple Entry/Exit Gates

- Support for multiple entry and exit points
- Distributed entry/exit processing
- Gate-specific queuing and management

### Proximity-Based Spot Allocation

- Algorithms to assign the parking spot nearest to the entry gate
- Distance calculation between gates and available spots
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

- Concurrency control for simultaneous entry/exit
- Database design for efficient queries
- API design for potential external integrations
- Monitoring and alerting mechanisms
- Backup and disaster recovery procedures
