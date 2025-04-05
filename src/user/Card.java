package user;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Card implements Serializable {
    private UUID id;

    private String cardNumber;

    private long balance;

    private UUID userId;

    public Card() {
    }

    public Card(UUID id, String cardNumber, long balance, UUID userId) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.balance = balance;
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card card)) return false;
        return getBalance() == card.getBalance() && Objects.equals(getId(), card.getId()) && Objects.equals(getCardNumber(), card.getCardNumber()) && Objects.equals(getUserId(), card.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCardNumber(), getBalance(), getUserId());
    }

    @Override
    public String toString() {
        return "Card{" +
               "id=" + id +
               ", cardNumber='" + cardNumber + '\'' +
               ", balance=" + balance +
               ", userId=" + userId +
               '}';
    }
}
