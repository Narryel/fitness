package com.narryel.fitness.domain.entity;

import com.narryel.fitness.domain.enums.MuscleGroup;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

import java.util.List;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@Entity
@FieldDefaults(level = PRIVATE)
public class ExerciseInfo extends JpaEntity {

    @Enumerated(STRING)
    MuscleGroup muscleGroup;

    String description;

    @ElementCollection
    List<String> aliases;



}
