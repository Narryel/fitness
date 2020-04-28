package com.narryel.fitness.domain.entity;

import com.narryel.fitness.domain.enums.TrainingStatus;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;

@Data
@Entity
@FieldDefaults(level = PRIVATE)
public class Training extends JpaEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    FitUser user;

    @OneToMany(mappedBy = "training", cascade = ALL, fetch = LAZY, orphanRemoval = true)
    List<ExerciseSet> setList;

    @Enumerated(STRING)
    TrainingStatus status;
}
