package com.narryel.fitness.domain.entity;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class JpaEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;


    @CreatedDate
    Instant createdDate;

    Instant prePersistDate;

    // todo
//    @LastModifiedBy


    @PrePersist
    void setPrePersistDate(){
        prePersistDate= Instant.now();
    }

}
