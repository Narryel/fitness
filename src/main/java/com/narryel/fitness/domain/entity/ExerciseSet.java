package com.narryel.fitness.domain.entity;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;

@Data
@Entity
@FieldDefaults(level = PRIVATE)
public class ExerciseSet extends JpaEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "training_id", referencedColumnName = "id")
    Training training;

    @OneToOne
    Exercise exercise;

    @Column(name = "set_order")
    Integer setOrder;

    @Column(name = "rep_count")
    Integer repCount;

    Integer weight;


}
