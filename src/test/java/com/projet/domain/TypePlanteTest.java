package com.projet.domain;

import static com.projet.domain.PlanteTestSamples.*;
import static com.projet.domain.TypePlanteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.projet.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TypePlanteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TypePlante.class);
        TypePlante typePlante1 = getTypePlanteSample1();
        TypePlante typePlante2 = new TypePlante();
        assertThat(typePlante1).isNotEqualTo(typePlante2);

        typePlante2.setId(typePlante1.getId());
        assertThat(typePlante1).isEqualTo(typePlante2);

        typePlante2 = getTypePlanteSample2();
        assertThat(typePlante1).isNotEqualTo(typePlante2);
    }

    @Test
    void planteTest() throws Exception {
        TypePlante typePlante = getTypePlanteRandomSampleGenerator();
        Plante planteBack = getPlanteRandomSampleGenerator();

        typePlante.addPlante(planteBack);
        assertThat(typePlante.getPlantes()).containsOnly(planteBack);
        assertThat(planteBack.getNom()).isEqualTo(typePlante);

        typePlante.removePlante(planteBack);
        assertThat(typePlante.getPlantes()).doesNotContain(planteBack);
        assertThat(planteBack.getNom()).isNull();

        typePlante.plantes(new HashSet<>(Set.of(planteBack)));
        assertThat(typePlante.getPlantes()).containsOnly(planteBack);
        assertThat(planteBack.getNom()).isEqualTo(typePlante);

        typePlante.setPlantes(new HashSet<>());
        assertThat(typePlante.getPlantes()).doesNotContain(planteBack);
        assertThat(planteBack.getNom()).isNull();
    }
}
