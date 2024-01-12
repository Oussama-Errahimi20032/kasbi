package com.projet.domain;

import static com.projet.domain.ParcelleTestSamples.*;
import static com.projet.domain.PlantageTestSamples.*;
import static com.projet.domain.PlanteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.projet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlantageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Plantage.class);
        Plantage plantage1 = getPlantageSample1();
        Plantage plantage2 = new Plantage();
        assertThat(plantage1).isNotEqualTo(plantage2);

        plantage2.setId(plantage1.getId());
        assertThat(plantage1).isEqualTo(plantage2);

        plantage2 = getPlantageSample2();
        assertThat(plantage1).isNotEqualTo(plantage2);
    }

    @Test
    void planteLibelleTest() throws Exception {
        Plantage plantage = getPlantageRandomSampleGenerator();
        Plante planteBack = getPlanteRandomSampleGenerator();

        plantage.setPlanteLibelle(planteBack);
        assertThat(plantage.getPlanteLibelle()).isEqualTo(planteBack);

        plantage.planteLibelle(null);
        assertThat(plantage.getPlanteLibelle()).isNull();
    }

    @Test
    void parcelleLibelleTest() throws Exception {
        Plantage plantage = getPlantageRandomSampleGenerator();
        Parcelle parcelleBack = getParcelleRandomSampleGenerator();

        plantage.setParcelleLibelle(parcelleBack);
        assertThat(plantage.getParcelleLibelle()).isEqualTo(parcelleBack);

        plantage.parcelleLibelle(null);
        assertThat(plantage.getParcelleLibelle()).isNull();
    }
}
