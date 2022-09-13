package com.demo.project2.model;

import java.util.HashSet;
import java.util.Set;
import java.time.LocalDate;

import javax.persistence.*;

import javax.validation.constraints.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private long id;

    @FutureOrPresent(message = "Invoice date should not be in the past!")
    private LocalDate date;

    @NotBlank
    @Size(max = 30)
    @Column(name = "company_from")
    private String companyFrom;

    @NotBlank
    @Size(max = 40)
    @Column(name = "street_from")
    private String streetFrom;

    @NotBlank
    @Size(max = 25)
    @Column(name = "city_from")
    private String cityFrom;

    @NotBlank
    @Size(max = 20)
    @Column(name = "state_from")
    private String stateFrom;

    @NotBlank
    @Size(max = 10)
    @Column(name = "zip_from")
    private String zipFrom;

    @NotBlank
    @Size(max = 20)
    private String phoneFrom;

    @NotBlank
    @Size(max = 120)
    @Column(name = "name_to")
    private String nameTo;

    @NotBlank
    @Size(max = 30)
    @Column(name = "company_to")
    private String companyTo;

    @NotBlank
    @Size(max = 40)
    @Column(name = "street_to")
    private String streetTo;

    @NotBlank
    @Size(max = 25)
    @Column(name = "city_to")
    private String cityTo;

    @NotBlank
    @Size(max = 20)
    @Column(name = "state_to")
    private String stateTo;

    @NotBlank
    @Size(max = 10)
    @Column(name = "zip_to")
    private String zipTo;

    @NotBlank
    @Size(max = 20)
    @Column(name = "phone_to")
    private String phoneTo;

    @Size(max = 120)
    @Email
    @Column(name = "email_to")
    private String emailTo;

    // one-to-many relation
    // @JoinColumn will define a new column to hold a Foreign Key
    //  ie (for one-to-many) the id of the source entity (invoices) is stored
    //  in a new column in the target table (invoice_items) as a Foreign Key.
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL}, // all entity state transitions (persist, remove...) are passed from parent entity to child entities.
            orphanRemoval = true // trigger a remove entity state transition when child entity is no longer referenced by its parent entity.
    )
    @JoinColumn(
            name="invoice_id", // new column added to invoice_item
            referencedColumnName="invoice_id"
    )
    // NB - object references an unsaved transient instance -> cascade... tells hibernate to save
    //  child objects (invoice_items) that are not present in the database, when saving the parent (invoice)
    private  Set<InvoiceItem> items = new HashSet<>(); // hashset to hold invoice item(s)

    @Size(max = 120)
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private IStatus status;

    @PositiveOrZero
    @Column(name = "created_by")
    private long createdBy;

    // non-null constructor
    public Invoice(String companyFrom, String streetFrom, String cityFrom, String stateFrom,
                   String zipFrom, String phoneFrom, String nameTo, String companyTo,
                   String streetTo, String cityTo, String stateTo, String zipTo, String phoneTo,
                   String emailTo, Set<InvoiceItem> items, String comments, long createdBy) {

        super();
        this.date = LocalDate.now(); // Create a date object - (year, month, day (yyyy-MM-dd))
        this.companyFrom = companyFrom;
        this.streetFrom = streetFrom;
        this.cityFrom = cityFrom;
        this.stateFrom = stateFrom;
        this.zipFrom = zipFrom;
        this.phoneFrom = phoneFrom;
        this.nameTo = nameTo;
        this.companyTo = companyTo;
        this.streetTo = streetTo;
        this.cityTo = cityTo;
        this.stateTo = stateTo;
        this.zipTo = zipTo;
        this.phoneTo = phoneTo;
        this.emailTo = emailTo;
        this.items = items;
        this.comments = comments;
        this.status = IStatus.DRAFT; // saved as a draft
        this.createdBy = createdBy;
    }

    // setters and getters, toString() and equals() automatically created by Lombok annotation
}
