package com.queddeng.oauth2login.model;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_providers")
public class AuthProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider; // GOOGLE or GITHUB
    private String providerUserId;
    private String providerEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Getters
    public Long getId() {
        return id;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public String getProviderEmail() {
        return providerEmail;
    }

    public User getUser() {
        return user;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public void setProviderEmail(String providerEmail) {
        this.providerEmail = providerEmail;
    }

    public void setUser(User user) {
        this.user = user;
    }
}