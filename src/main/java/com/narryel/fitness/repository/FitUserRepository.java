package com.narryel.fitness.repository;

import com.narryel.fitness.domain.entity.FitUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FitUserRepository extends JpaRepository<FitUser, Long> {
}
