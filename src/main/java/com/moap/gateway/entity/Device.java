package com.moap.gateway.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "DEVICE")
@Accessors(chain = true)
public class Device implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "seller")
    private String seller;

    @Column(name = "creation_date")
    private OffsetDateTime creationDate;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "serial_number")
    private Gateway gateway;
}
