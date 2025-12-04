package com.example.vietjapaneselearning.model;

import com.example.vietjapaneselearning.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name")
    private String fullName;
    @Column
    private String email;
    @Column(nullable = false)
    private String password;
    @Column
    private String avatar;
    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "phone_number")
    private String phoneNumber;
    @Column
    private String location;
    @Column
    private String bio;
    @Column
    private String language;
    @Column(name = "birth_day")
    private LocalDate birthDay;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;
    @OneToMany(mappedBy = "userId", cascade = CascadeType.REMOVE)
    private List<PlayerGame> playerGames;
    @ManyToOne()
    @JoinColumn(name = "role_id")
    @JsonIgnore
    private Role role;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + (role.getName()).toString()));
    }

    @Override
    public String getUsername() {
        return email;
    }
}
