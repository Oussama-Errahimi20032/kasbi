package com.projet.domain;

import static com.projet.domain.FermeTestSamples.*;
import static com.projet.domain.ParcelleTestSamples.*;
import static com.projet.domain.PlantageTestSamples.*;
import static com.projet.domain.PlanteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.projet.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ParcelleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Parcelle.class);
        Parcelle parcelle1 = getParcelleSample1();
        Parcelle parcelle2 = new Parcelle();
        assertThat(parcelle1).isNotEqualTo(parcelle2);

        parcelle2.setId(parcelle1.getId());
        assertThat(parcelle1).isEqualTo(parcelle2);

        parcelle2 = getParcelleSample2();
        assertThat(parcelle1).isNotEqualTo(parcelle2);
    }

    @Test
    void plantageTest() throws Exception {
        Parcelle parcelle = getParcelleRandomSampleGenerator();
        Plantage plantageBack = getPlantageRandomSampleGenerator();

        parcelle.addPlantage(plantageBack);
        assertThat(parcelle.getPlantages()).containsOnly(plantageBack);
        assertThat(plantageBack.getParcelleLibelle()).isEqualTo(parcelle);

        parcelle.removePlantage(plantageBack);
        assertThat(parcelle.getPlantages()).doesNotContain(plantageBack);
        assertThat(plantageBack.getParcelleLibelle()).isNull();

        parcelle.plantages(new HashSet<>(Set.of(plantageBack)));
        assertThat(parcelle.getPlantages()).containsOnly(plantageBack);
        assertThat(plantageBack.getParcelleLibelle()).isEqualTo(parcelle);

        parcelle.setPlantages(new HashSet<>());
        assertThat(parcelle.getPlantages()).doesNotContain(plantageBack);
        assertThat(plantageBack.getParcelleLibelle()).isNull();
    }

    @Test
    void fermeTest() throws Exception {
        Parcelle parcelle = getParcelleRandomSampleGenerator();
        Ferme fermeBack = getFermeRandomSampleGenerator();

        parcelle.setFerme(fermeBack);
        assertThat(parcelle.getFerme()).isEqualTo(fermeBack);

        parcelle.ferme(null);
        assertThat(parcelle.getFerme()).isNull();
    }

    @Test
    void plantesTest() throws Exception {
        Parcelle parcelle = getParcelleRandomSampleGenerator();
        Plante planteBack = getPlanteRandomSampleGenerator();

        parcelle.addPlantes(planteBack);
        assertThat(parcelle.getPlantes()).containsOnly(planteBack);

        parcelle.removePlantes(planteBack);
        assertThat(parcelle.getPlantes()).doesNotContain(planteBack);

        parcelle.plantes(new HashSet<>(Set.of(planteBack)));
        assertThat(parcelle.getPlantes()).containsOnly(planteBack);

        parcelle.setPlantes(new HashSet<>());
        assertThat(parcelle.getPlantes()).doesNotContain(planteBack);
    }

    @Test
    void fermeLibelleTest() throws Exception {
        Parcelle parcelle = getParcelleRandomSampleGenerator();
        Ferme fermeBack = getFermeRandomSampleGenerator();

        parcelle.setFermeLibelle(fermeBack);
        assertThat(parcelle.getFermeLibelle()).isEqualTo(fermeBack);

        parcelle.fermeLibelle(null);
        assertThat(parcelle.getFermeLibelle()).isNull();
    }
}
