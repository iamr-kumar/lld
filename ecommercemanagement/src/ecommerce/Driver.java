package ecommerce;

public class Driver {
    public static void main(String[] args) {
        GamifiedEcommerce ecommerce = GamifiedEcommerce.getInstance();

        try {
            ecommerce.addUser("user1");

            ecommerce.purchase("user1", 800, 0);
            ecommerce.getUserStats("user1");

            // ecommerce.purchase("user1", 4200, 100);
            // ecommerce.getUserStats("user 1");

            ecommerce.purchase("user1", 4200, 0);
            ecommerce.getUserStats("user1");

            ecommerce.purchase("user1", 3000, 300);
            ecommerce.getUserStats("user1");

            ecommerce.purchase("user1", 5000, 0);
            ecommerce.getUserStats("user1");

        } catch (IllegalArgumentException e) {
            System.err.println("Operation Failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("Operation Failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
        }

    }
}
