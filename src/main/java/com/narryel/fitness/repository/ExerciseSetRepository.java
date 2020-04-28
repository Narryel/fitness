package com.narryel.fitness.repository;

import com.narryel.fitness.domain.entity.ExerciseSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, Long> {

}
