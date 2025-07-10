## APIs

POST /user/signup
POST /user/signin

GEt /cities
GET /movies/all/:city

GET /show/:moveId
GET /seats/:showId

POST /booking/create
GET /booking/status/:bookingId

POST /theatre/create
POST /screen/create
POST /movie/create
POST /show/create

PUT /show/modify/:showId

## DB Design

User

- id
- name
- email
- password

Movie

- name
- releasedate
- language
- duration

Theatre

- name
- city
- address

Screen

- id
- number
- theatre_id

Seat

- id
- number
- row
- screen_id

Show

- id
- movie_id
- screen_id
- start_time
- duration

SeatAvailibilty

- id
- show_id
- seat_id
- status
- locked_at
- locked_by (user_id)

Booking

- id
- show_id
- user_id
- status
- payment_id

booked_sets

- id
- booking_id
- seat_id
- show_id

payemnt

- id
- status
- show_id
- booking_id
- amount
