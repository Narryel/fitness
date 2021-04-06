package com.narryel.fitness.util;

import com.narryel.fitness.domain.entity.Exercise;
import com.narryel.fitness.domain.entity.ExerciseSet;
import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.domain.entity.Training;
import com.narryel.fitness.domain.enums.TrainingStatus;
import com.narryel.fitness.domain.enums.UserStatus;
import com.narryel.fitness.repository.ExerciseRepository;
import com.narryel.fitness.repository.FitUserRepository;
import com.narryel.fitness.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "fill-with-test-data-enabled", havingValue = "true")
public class FillDbWithTestData {

    private final FitUserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final ExerciseRepository exerciseRepository;


    @PostConstruct
    @Transactional
    void fillTestData() {

        //training ready for start
        val user = userRepository.save(new FitUser()
                .setChatId(134614839L)
                .setNickName("Narryel")
                .setStatus(UserStatus.ACTIVE)
                .setTelegramUserId(134614839L)
        );

        val persistedTraining = trainingRepository.save(new Training()
                .setUser(user)
                .setStatus(TrainingStatus.READY)
                .setName("тестовая")
        );

        val exerciseList = List.of(
                new Exercise().setName("Подъем штанги на бицепс").setStatus(TrainingStatus.READY).setTraining(persistedTraining),
                new Exercise().setName("Разгибание гантели на трицепс").setStatus(TrainingStatus.READY).setTraining(persistedTraining),
                new Exercise().setName("Пресс").setStatus(TrainingStatus.READY).setTraining(persistedTraining)
        );

        val finishedPersistedTraining = trainingRepository.save(new Training()
                .setUser(user)
                .setStatus(TrainingStatus.FINISHED)
                .setName("тестовая Историческая")
        );

        val exerciseSet = new ExerciseSet().setRepCount(20).setSetOrder(1).setWeight(BigDecimal.TEN);
        val историческая_подъем_штанги_на_бицепс = new Exercise().setName("ИСТОРИЧЕСКАЯ Подъем штанги на бицепс")
                .setStatus(TrainingStatus.READY)
                .setTraining(finishedPersistedTraining)
                .setSets(List.of(exerciseSet));
        val finishedTrainingExersises = List.of(
                историческая_подъем_штанги_на_бицепс,
                new Exercise()
                        .setName("ИСТОРИЧЕСКАЯ Разгибание гантели на трицепс")
                        .setStatus(TrainingStatus.READY)
                        .setTraining(finishedPersistedTraining),
                new Exercise()
                        .setName("ИСТОРИЧЕСКАЯ Пресс")
                        .setStatus(TrainingStatus.READY)
                        .setTraining(finishedPersistedTraining)
        );

        finishedTrainingExersises.forEach(
                it -> it.setTraining(finishedPersistedTraining)
        );

        exerciseList.forEach(exercise ->
                exercise.setTraining(persistedTraining)
        );

        exerciseRepository.saveAll(exerciseList);
        exerciseRepository.saveAll(finishedTrainingExersises);


    }
}
