package com.example.vietjapaneselearning.repository;

import com.example.vietjapaneselearning.enums.RoleEnum;
import com.example.vietjapaneselearning.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u " +
            "WHERE (:keyword IS NULL " +
            "       OR u.email LIKE CONCAT('%', :keyword, '%') " +
            "       OR u.fullName LIKE CONCAT('%', :keyword, '%')) " +
            "AND (:role IS NULL OR u.role = :role)")
    Page<User> searchByKeywordAndRole(
            @Param("keyword") String keyword,
            @Param("role") RoleEnum role,
            Pageable pageable
    );
    @Query("SELECT count(u.id) from User u ")
    Long totalUser();
    @Query("SELECT count(u.id) from User u where u.role.name = :role")
    int countByRole(@Param("role") RoleEnum role);
}
