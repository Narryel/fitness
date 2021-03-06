package com.narryel.fitness.repository;

import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.domain.entity.Training;
import com.narryel.fitness.domain.enums.TrainingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Override
    @EntityGraph("Detailed")
    Optional<Training> findById(Long id);

    @Query("from Training training " +
            "where training.user = :user " +
            "and training.status = 'IN_PLANNING'")
    Optional<Training> findByUserAndStatusEqualsReady(@Param("user") FitUser user);


    List<Training> findByUserAndStatus(FitUser user, TrainingStatus status);

    @Modifying
    @Query("update Training training " +
            "set training.status = 'READY' " +
            "where training.user = :user " +
            "and training.status = 'IN_PLANNING'")
    void finishPlanningUserTraining(FitUser user);



    @Modifying
    @Query("update Training training " +
            "set training.status = :status " +
            "where training.id = :id")
    void setTrainingStatusWithTrainingId(TrainingStatus status, Long id);
 }
