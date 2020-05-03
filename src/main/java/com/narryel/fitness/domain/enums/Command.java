package com.narryel.fitness.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Command {
    START("/start"),
    REGISTER_USER_CMD("registerUser"),
    PLAN_TRAINING_CMD("planTraining"),
    ADD_EXERCISE_CMD("addExercise"),
    GET_MENU_CMD("getMenu"),
    CHOOSE_TRAINING_TO_START_CMD("chooseTrainingToStart"),
    FINISH_TRAINING_PLANNING_CMD("finishTrainingPlanning"),
    START_TRAINING("startTraining");


    String value;
}
