package com.moap.gateway.repository;

import com.moap.gateway.entity.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GatewayRepository extends JpaRepository<Gateway, String> {
    Optional<Gateway> findBySerialNumber(String serialnumber);
}
