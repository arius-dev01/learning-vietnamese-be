package com.example.vietjapaneselearning.repository;

import com.example.vietjapaneselearning.enums.RoleEnum;
import com.example.vietjapaneselearning.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleEnum name);
}
