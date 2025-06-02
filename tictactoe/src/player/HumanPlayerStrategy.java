package tictactoe.src.player;

import java.util.Scanner;

import tictactoe.src.Board;
import tictactoe.src.Position;

public class HumanPlayerStrategy implements PlayerStrategy {
    private final String name;
    private Scanner scanner;

    public HumanPlayerStrategy(String name) {
        this.name = name;
    }

    @Override
    public Position makeMove(Board board) {
        while (true) {
            System.out.println(name + ", enter your move (row [0-2] and column [0-2]): ");
            scanner = new Scanner(System.in);
            try {
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                Position position = new Position(row, col);
                if (board.isValidMove(position)) {
                    return position;
                } else {
                    System.out.println("Invalid move. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter two integers for row and column.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    public String getName() {
        return name;
    }

}
