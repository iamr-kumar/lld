package ecommerce;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GamifiedEcommerce {
    public static GamifiedEcommerce instance;
    private Map<String, User> users;

    public static GamifiedEcommerce getInstance() {
        if (instance == null) {
            instance = new GamifiedEcommerce();
        }
        return instance;
    }

    private GamifiedEcommerce() {
        this.users = new ConcurrentHashMap<>();
    }

    public void addUser(String username) {
        User user = users.get(username);
        if (user != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        user = new User(username);
        users.put(username, user);
    }

    public void purchase(String username, double amount, double redeemAmount) {
        User user = users.get(username);
        if (user == null) {
            throw new IllegalArgumentException("User " + username + " does not exist");
        }
        if (user.points < redeemAmount) {
            throw new IllegalStateException("Insufficient points. User has only " + user.points + " points");
        }

        double maxRedeemableAmount = user.levelStrategy.getMaxRedeemAmount(amount);
        double finalRedeemedAmount = Math.min(redeemAmount, maxRedeemableAmount);
        double finalAmount = amount - finalRedeemedAmount;
        user.points -= finalRedeemedAmount;
        double pointsEarned = user.levelStrategy.calculatePoints(finalAmount);
        user.points += pointsEarned;
        user.updateTotalSpent(finalAmount);
        user.updateLevel();
        System.out.println("Purchase successfull");
        System.out.println("Points redemeed:" + finalRedeemedAmount);
        System.out.println("Total payable amount: " + finalAmount);
        System.out.println("Total points earned: " + pointsEarned);
    }

    public void getUserStats(String username) {
        User user = users.get(username);
        if (user == null) {
            throw new IllegalArgumentException("User with username " + username + " does not exist");
        }
        String userLevel = user.getUserLevel();
        double points = user.getPoints();

        System.out.println("User level is:" + userLevel + " and current user point is " + points);
    }

}
