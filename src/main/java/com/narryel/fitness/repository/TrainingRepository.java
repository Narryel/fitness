package com.narryel.fitness.repository;

import com.narryel.fitness.domain.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingRepository extends JpaRepository<Training, Long> {
}
