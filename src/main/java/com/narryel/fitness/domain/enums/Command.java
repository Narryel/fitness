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
    START_TRAINING_CMD("startTraining");


    String value;
}
