package com.queddeng.oauth2login.repository;

import com.queddeng.oauth2login.model.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {
    Optional<AuthProvider> findByProviderAndProviderUserId(String provider, String providerUserId);
}
