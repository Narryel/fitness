package com.narryel.fitness.domain.entity;

import com.narryel.fitness.domain.enums.TrainingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
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

@Getter
@Setter
@Entity
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@Accessors(chain = true)
public class Training extends JpaEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    FitUser user;

    @OneToMany(mappedBy = "training", cascade = ALL, fetch = LAZY, orphanRemoval = true)
    List<Exercise> exercises;

    @Enumerated(STRING)
    TrainingStatus status;

    String name;


}
