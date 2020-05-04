package com.narryel.fitness.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@Entity
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@Accessors(chain = true)
public class Exercise extends JpaEntity {

    @ManyToOne
    @JoinColumn(name = "training_id", referencedColumnName = "id")
    Training training;

    String name;

    @OneToMany(mappedBy = "exercise", cascade = ALL, fetch = LAZY, orphanRemoval = true)
    List<ExerciseSet> sets;

    BigDecimal weight;

}
