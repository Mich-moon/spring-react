package com.demo.project2.model;

import javax.persistence.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Entity
@Table(name = "invoice_addresses")
public class InvoiceAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(max = 30)
    private String company;

    @NotBlank
    @Size(max = 40)
    private String street;

    @NotBlank
    @Size(max = 25)
    private String city;

    @NotBlank
    @Size(max = 20)
    private String state;

    @NotBlank
    @Size(max = 10)
    private String zip;

    // constructors
    public InvoiceAddress() {

    }

    public InvoiceAddress(String company, String street, String city, String state, String zip) {
        super();
        this.company = company;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    // setters
    public void setId(long id) {
        this.id = id;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    // getters
    public long getId() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }
}
