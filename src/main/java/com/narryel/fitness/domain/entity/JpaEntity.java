package com.narryel.fitness.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@MappedSuperclass
public abstract class JpaEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    protected Long id;


    protected Instant prePersistDate;

    @PrePersist
    void setPrePersistDate(){
        prePersistDate= Instant.now();
    }

}
