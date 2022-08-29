package com.demo.project2.model;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private URole name;

    // constructors
    public Role() {
    }

    public Role(URole name) {
        this.name = name;
    }

    // setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(URole name) {
        this.name = name;
    }

    // getters
    public Integer getId() {
        return id;
    }

    public URole getName() {
        return name;
    }


}