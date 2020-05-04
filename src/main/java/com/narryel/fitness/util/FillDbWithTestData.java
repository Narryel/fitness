package com.narryel.fitness.util;

import com.narryel.fitness.domain.entity.Exercise;
import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.domain.entity.Training;
import com.narryel.fitness.domain.enums.TrainingStatus;
import com.narryel.fitness.domain.enums.UserStatus;
import com.narryel.fitness.repository.ExerciseRepository;
import com.narryel.fitness.repository.FitUserRepository;
import com.narryel.fitness.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class FillDbWithTestData {

    private final FitUserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final ExerciseRepository exerciseRepository;


    @PostConstruct
    @Transactional
    void fillTestData() {

        //training ready for start
        final var user = userRepository.save(new FitUser()
                .setChatId(134614839L)
                .setNickName("Narryel")
                .setStatus(UserStatus.ACTIVE)
                .setTelegramUserId(134614839)
        );

        final var exerciseList = List.of(
                new Exercise().setName("Подъем штанги на бицепс").setStatus(TrainingStatus.READY),
                new Exercise().setName("Разгибание гантели на трицепс").setStatus(TrainingStatus.READY),
                new Exercise().setName("Пресс").setStatus(TrainingStatus.READY)
        );
        final var persistedTraining = trainingRepository.save(new Training().setUser(user).setStatus(TrainingStatus.READY).setExercises(exerciseList));

//        exerciseList.forEach(exercise -> {
//            exercise.setTraining(persistedTraining);
//            exerciseRepository.save(exercise);
//        });


    }
}
