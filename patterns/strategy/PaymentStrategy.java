package patterns.strategy;

/**
 * PaymentStrategy interface defines the contract for payment strategies.
 * Implementing classes will provide specific payment methods.
 */
public interface PaymentStrategy {
  /**
   * Pay method to process the payment.
   * 
   * @param amount
   */
  public void pay(int amount);

}