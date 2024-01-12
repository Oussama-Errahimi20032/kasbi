package com.projet.domain;

import static com.projet.domain.ParcelleTestSamples.*;
import static com.projet.domain.PlantageTestSamples.*;
import static com.projet.domain.PlanteTestSamples.*;
import static com.projet.domain.TypePlanteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.projet.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PlanteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Plante.class);
        Plante plante1 = getPlanteSample1();
        Plante plante2 = new Plante();
        assertThat(plante1).isNotEqualTo(plante2);

        plante2.setId(plante1.getId());
        assertThat(plante1).isEqualTo(plante2);

        plante2 = getPlanteSample2();
        assertThat(plante1).isNotEqualTo(plante2);
    }

    @Test
    void plantageTest() throws Exception {
        Plante plante = getPlanteRandomSampleGenerator();
        Plantage plantageBack = getPlantageRandomSampleGenerator();

        plante.addPlantage(plantageBack);
        assertThat(plante.getPlantages()).containsOnly(plantageBack);
        assertThat(plantageBack.getPlanteLibelle()).isEqualTo(plante);

        plante.removePlantage(plantageBack);
        assertThat(plante.getPlantages()).doesNotContain(plantageBack);
        assertThat(plantageBack.getPlanteLibelle()).isNull();

        plante.plantages(new HashSet<>(Set.of(plantageBack)));
        assertThat(plante.getPlantages()).containsOnly(plantageBack);
        assertThat(plantageBack.getPlanteLibelle()).isEqualTo(plante);

        plante.setPlantages(new HashSet<>());
        assertThat(plante.getPlantages()).doesNotContain(plantageBack);
        assertThat(plantageBack.getPlanteLibelle()).isNull();
    }

    @Test
    void typePlanteTest() throws Exception {
        Plante plante = getPlanteRandomSampleGenerator();
        TypePlante typePlanteBack = getTypePlanteRandomSampleGenerator();

        plante.setTypePlante(typePlanteBack);
        assertThat(plante.getTypePlante()).isEqualTo(typePlanteBack);

        plante.typePlante(null);
        assertThat(plante.getTypePlante()).isNull();
    }

    @Test
    void nomTest() throws Exception {
        Plante plante = getPlanteRandomSampleGenerator();
        TypePlante typePlanteBack = getTypePlanteRandomSampleGenerator();

        plante.setNom(typePlanteBack);
        assertThat(plante.getNom()).isEqualTo(typePlanteBack);

        plante.nom(null);
        assertThat(plante.getNom()).isNull();
    }

    @Test
    void parcellesTest() throws Exception {
        Plante plante = getPlanteRandomSampleGenerator();
        Parcelle parcelleBack = getParcelleRandomSampleGenerator();

        plante.addParcelles(parcelleBack);
        assertThat(plante.getParcelles()).containsOnly(parcelleBack);
        assertThat(parcelleBack.getPlantes()).containsOnly(plante);

        plante.removeParcelles(parcelleBack);
        assertThat(plante.getParcelles()).doesNotContain(parcelleBack);
        assertThat(parcelleBack.getPlantes()).doesNotContain(plante);

        plante.parcelles(new HashSet<>(Set.of(parcelleBack)));
        assertThat(plante.getParcelles()).containsOnly(parcelleBack);
        assertThat(parcelleBack.getPlantes()).containsOnly(plante);

        plante.setParcelles(new HashSet<>());
        assertThat(plante.getParcelles()).doesNotContain(parcelleBack);
        assertThat(parcelleBack.getPlantes()).doesNotContain(plante);
    }
}
