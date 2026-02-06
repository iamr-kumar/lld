# Snake and Ladder Game

## Problem Statement

Design and implement a **Snake and Ladder board game** that supports:

1. **Board setup** — A configurable board with cells that can be regular, snake, or ladder cells
2. **Snakes** — If a player lands on a snake's head, they slide down to the snake's tail
3. **Ladders** — If a player lands at the bottom of a ladder, they climb up to the top
4. **Dice rolling** — A dice is thrown each turn; the player advances by that many positions
5. **Win condition** — The first player to reach the final cell on the board wins
6. **Turn management** — Pluggable turn strategies (round-robin, extra turn on rolling six, etc.)
7. **Multi-player** — The system supports at least 2 players

---

## Architecture Overview

### File Structure

```text
src/
├── SnakeAndLadder.java            # Demo/driver: sets up board, players, and runs the game
├── context/
│   └── GameContext.java           # Mutable game state container (players, board, dice, turn)
├── enums/
│   └── GameState.java             # Game lifecycle states: NEW, IN_PROGRESS, FINISHED
├── models/
│   ├── Board.java                 # Board with indexed cell array + validation
│   ├── Cell.java                  # Abstract cell with source/destination positions
│   ├── RegularCell.java           # No-op cell (destination == source)
│   ├── SnakeCell.java             # Snake cell (destination < source, validated)
│   ├── LadderCell.java            # Ladder cell (destination > source, validated)
│   ├── Dice.java                  # Random dice with configurable sides
│   ├── Player.java                # Player with name and mutable position
│   └── Position.java              # Immutable position value object
├── services/
│   ├── IGameService.java          # Game service contract
│   └── GameService.java           # Game orchestration: turns, moves, win detection
└── strategy/
    ├── ITurnStrategy.java         # Turn selection contract
    ├── RoundRobinStrategy.java    # Simple round-robin turn order
    └── ExtraTurnOnSixStrategy.java # Extra turn on 6, forfeit after 3 consecutive sixes
```

---

## Current Design (How It Works)

### High-Level Flow

1. `SnakeAndLadder` (driver) creates a `Board` and places `SnakeCell`s and `LadderCell`s on it.
2. A `GameContext` is created with the board — it holds all mutable game state (players, current turn, dice, game state, winner).
3. A `GameService` is created with the context and a chosen `ITurnStrategy`.
4. Players are added, then `startGame()` is called.
5. The game loop calls `playTurn()` repeatedly:
   - Current player rolls the dice.
   - Player moves forward by the dice value.
   - If the new position overshoots the board, the player stays in place.
   - Otherwise, the cell at the new position is resolved — `cell.getDestination()` handles snake/ladder transitions via polymorphism.
   - If the player reaches the final cell, they win and the game ends.
   - Otherwise, `ITurnStrategy.getNextPlayer()` determines whose turn is next.

### Key Design Decisions

- **Cell polymorphism** — `SnakeCell`, `LadderCell`, and `RegularCell` all extend `Cell`. Movement resolution is handled by calling `getDestination()` on whatever cell the player lands on — no `if/else` or `instanceof` checks in game logic.
- **Board auto-initialization** — All cells default to `RegularCell` on construction; callers only place snakes and ladders.
- **GameContext as state holder** — Pure state container with getters/setters. No business logic — all game rules live in `GameService`.
- **Constructor validation** — `SnakeCell` enforces source > destination; `LadderCell` enforces source < destination. Invalid configurations fail fast.

---

## SOLID Principles Adherence

| Principle                       | Status | Notes                                                                                                                                                           |
| ------------------------------- | ------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Single Responsibility (SRP)** | ✅     | `GameContext` holds state; `GameService` orchestrates game logic; `Cell` subclasses handle cell behavior; strategies handle turn order; `Dice` handles rolling. |
| **Open/Closed (OCP)**           | ✅     | New cell types (e.g., `PowerUpCell`) or turn strategies can be added without modifying `GameService` or `Board`.                                                |
| **Liskov Substitution (LSP)**   | ✅     | All `Cell` subclasses are interchangeable — `Board` and `GameService` work with the abstract `Cell` type. All `ITurnStrategy` implementations are swappable.    |
| **Interface Segregation (ISP)** | ✅     | `IGameService` and `ITurnStrategy` are focused, small interfaces with no unnecessary methods.                                                                   |
| **Dependency Inversion (DIP)**  | ⚠️     | `GameService` depends on `ITurnStrategy` (abstraction) ✅. However, `GameContext` directly instantiates `Dice`, and the driver wires concrete types directly.   |

---

## Design Patterns Used

| Pattern                | Usage                                                                                             |
| ---------------------- | ------------------------------------------------------------------------------------------------- |
| **Strategy**           | Turn order selection (`ITurnStrategy` → `RoundRobinStrategy`, `ExtraTurnOnSixStrategy`)           |
| **Polymorphism / OCP** | Cell behavior (`Cell` → `RegularCell`, `SnakeCell`, `LadderCell`) — no conditionals in game logic |
| **Context Object**     | `GameContext` encapsulates all shared mutable state passed between service and strategy           |

---

## Demo Scenario

`SnakeAndLadder` sets up a classic 100-cell board with:

- **10 snakes**: 16→6, 47→26, 49→11, 56→53, 62→19, 64→60, 87→24, 93→73, 95→75, 98→78
- **9 ladders**: 1→38, 4→14, 9→31, 21→42, 28→84, 36→44, 51→67, 71→91, 80→100
- **2 players**: Alice and Bob
- **Strategy**: Extra turn on rolling a 6 (forfeit after 3 consecutive sixes)

---

## How to Run

From repo root:

```bash
javac snakeandladder/src/**/*.java snakeandladder/src/SnakeAndLadder.java
java snakeandladder.src.SnakeAndLadder
```

---

## Future Improvements

### Feature Enhancements

1. **Board builder / factory** — Encapsulate board construction (snake/ladder placement) behind a builder, possibly with random generation
2. **Multiple dice** — Support rolling multiple dice per turn
3. **Undo on triple six** — Revert the last move when a player rolls three consecutive sixes (currently just moves to next player)
4. **Game history / replay** — Record each turn for playback or auditing
5. **Interactive mode** — Accept player input from console instead of auto-playing

### Engineering / Design Enhancements

1. **Dependency injection** — Inject `Dice` into `GameContext` instead of hard-coding a 6-sided dice
2. **Custom exceptions** — Replace `IllegalArgumentException` / `IllegalStateException` with domain-specific exceptions (e.g., `InvalidMoveException`, `GameNotStartedException`)
3. **Board validation** — Validate that no snake head or ladder top overshoots the board; detect snake-ladder cycles or chains
4. **Testability** — Accept `Random` (or a seed) in `Dice` for deterministic testing

### Code Quality

1. **Decouple output** — Replace `System.out.println` in `GameService` with an observer/listener or event system for turn/win notifications
2. **Immutable player position** — Return new `Player` snapshots instead of mutating position in place
3. **Unit tests** — Test cell resolution, overshoot handling, strategy behavior, and win detection
