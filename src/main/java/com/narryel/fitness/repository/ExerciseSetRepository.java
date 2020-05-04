package com.narryel.fitness.repository;

import com.narryel.fitness.domain.entity.Exercise;
import com.narryel.fitness.domain.entity.ExerciseSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, Long> {
    List<ExerciseSet> getAllByExercise(Exercise exercise);

}
