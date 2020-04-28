package com.narryel.fitness.domain.entity;

import com.narryel.fitness.domain.enums.MuscleGroup;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

import java.util.List;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

@Data
@Entity
@FieldDefaults(level = PRIVATE)
public class Exercise extends JpaEntity {

    @Enumerated(STRING)
    MuscleGroup muscleGroup;

    String description;

    @ElementCollection
    List<String> aliases;



}
