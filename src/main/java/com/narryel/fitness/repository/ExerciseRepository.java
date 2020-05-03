package com.narryel.fitness.repository;

import com.narryel.fitness.domain.entity.Exercise;
import com.narryel.fitness.domain.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> getAllByTraining(Training training);
}
