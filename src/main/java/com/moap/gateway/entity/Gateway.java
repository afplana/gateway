package com.moap.gateway.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@Entity
@Table(name = "GATEWAY")
@Accessors(chain = true)
public class Gateway implements Serializable {

    @Id
    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "IPv4address")
    private String iPv4Address;

    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "gateway", cascade = CascadeType.ALL)
    private Set<Device> devices;

}
