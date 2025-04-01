package product;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static db.MyBase.productNameById;
import static db.MyBase.productPriceById;

public class OrderProduct implements Serializable {
    private UUID id;
    private UUID userId;
    private UUID cardId;
    private UUID productId;
    private int amount;
    private long price;
    private OrderStatus status;
    private LocalDateTime createdTime;
    private LocalDateTime userGetTime;

    public OrderProduct() {
    }

    public OrderProduct(UUID id, UUID userId, UUID cardId, UUID productId, int amount, OrderStatus status, LocalDateTime createdTime, LocalDateTime userGetTime) {
        this.id = id;
        this.userId = userId;
        this.cardId = cardId;
        this.productId = productId;
        this.amount = amount;
        this.status = status;
        this.createdTime = createdTime;
        this.userGetTime = userGetTime;

        this.price = amount * productPriceById(productId);
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getCardId() {
        return cardId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUserGetTime() {
        return userGetTime;
    }

    public void setUserGetTime(LocalDateTime userGetTime) {
        this.userGetTime = userGetTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderProduct that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getCardId(), that.getCardId()) && Objects.equals(getProductId(), that.getProductId()) && getStatus() == that.getStatus() && Objects.equals(getCreatedTime(), that.getCreatedTime()) && Objects.equals(getUserGetTime(), that.getUserGetTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUserId(), getCardId(), getProductId(), getStatus(), getCreatedTime(), getUserGetTime());
    }

    @Override
    public String toString() {
        return "OrderProduct{" +
               ", Product =" + productNameById(productId) +
               ", amount =" + amount +
               ", total price =" + price +
               ", status =" + status +
               ", createdTime=" + createdTime +
               '}';
    }
}
