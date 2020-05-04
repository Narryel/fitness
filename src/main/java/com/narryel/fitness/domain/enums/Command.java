package com.narryel.fitness.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Command {
    START("/start"),
    REGISTER_USER("registerUser"),
    PLAN_TRAINING("planTraining"),
    ADD_EXERCISE("addExercise"),
    GET_MENU("getMenu"),
    CHOOSE_TRAINING_TO_START("chooseTrainingToStart"),
    FINISH_TRAINING_PLANNING("finishTrainingPlanning"),
    START_TRAINING("startTraining"),
    START_EXERCISE("startExercise"),
    CHANGE_WEIGHT("changeWeight"),
    FINISH_EXERCISE("finishExercise"),
    FINISH_TRAINING("finishTraining");


    String value;
}
