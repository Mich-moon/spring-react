package com.demo.project2.model;

import java.math.BigDecimal;

import javax.persistence.*;

import javax.validation.constraints.*;


@Entity
@Table(name = "invoice_items")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private long id;

    @NotBlank
    @Size(max = 50)
    private String description;

    @DecimalMin(value = "0.00", inclusive = true)
    @Digits(integer=30, fraction=2)
    @PositiveOrZero
    private BigDecimal price;

    @Min(value = 1, message = "Quantity should not be less than 1")
    @Positive
    private Integer quantity;

    @DecimalMin(value = "0.00", inclusive = true)
    @Digits(integer=30, fraction=2)
    @PositiveOrZero
    private BigDecimal amount;

    // constructors
    public InvoiceItem() {

    }

    public InvoiceItem(String description, String price, String quantity, String amount) {
        // NB - all numeric values are being passed as strings

        super();
        this.description = description;
        this.price = new BigDecimal(price);
        this.quantity = Integer.parseInt(quantity);
        this.amount = new BigDecimal(amount);
    }

    // setters
    public void setId(long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) { this.price = new BigDecimal(price); }

    public void setQuantity(String quantity) {
        this.quantity = Integer.parseInt(quantity);
    }

    public void setAmount(String amount) {
        this.amount = new BigDecimal(amount);
    }

    // getters
    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() { return price; }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getAmount() { return amount; }

}
