package com.narryel.fitness.repository;

import com.narryel.fitness.domain.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
