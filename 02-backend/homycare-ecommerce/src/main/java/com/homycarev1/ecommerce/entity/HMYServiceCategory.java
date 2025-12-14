package com.homycarev1.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name="hmyservice_category")
// @Data -- known bug
@Getter
@Setter
public class HMYServiceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "hmyservice_name")
    private String hmyserviceName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    private Set<HMYService> hmyservices;

}
