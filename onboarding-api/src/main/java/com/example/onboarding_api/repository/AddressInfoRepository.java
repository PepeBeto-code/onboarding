package com.example.onboarding_api.repository;

import com.example.onboarding_api.entity.AddressInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressInfoRepository extends JpaRepository<AddressInfo, Long> {
    Optional<AddressInfo> findByUserId(Long userId);
}

