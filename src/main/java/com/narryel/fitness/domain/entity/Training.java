package com.narryel.fitness.domain.entity;

import com.narryel.fitness.domain.enums.TrainingStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
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
@NamedEntityGraphs(
        @NamedEntityGraph(
                name = "Detailed",
                attributeNodes = {
                        @NamedAttributeNode("exercises"),
                        @NamedAttributeNode("user"),
                }
        )
)
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
