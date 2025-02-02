package org.recap.model.jpa;

import lombok.Data;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "matching_score_translation_t")
@Data
public class MatchingScoreTranslationEntity {
    @Id
    @Column(
            name = "ID"
    )
    private Integer id;
    @Column(
            name = "DECIMAL_MA_SCORE"
    )
    private Integer decMaScore;
    @Column(
            name = "BINARY_MA_SCORE"
    )
    private String binMaScore;
    @Column(
            name = "STRING_MA_SCORE"
    )
    private String stringMaScore;
}
