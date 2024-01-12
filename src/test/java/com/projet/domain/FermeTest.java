package com.projet.domain;

import static com.projet.domain.AppUserTestSamples.*;
import static com.projet.domain.FermeTestSamples.*;
import static com.projet.domain.ParcelleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.projet.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FermeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ferme.class);
        Ferme ferme1 = getFermeSample1();
        Ferme ferme2 = new Ferme();
        assertThat(ferme1).isNotEqualTo(ferme2);

        ferme2.setId(ferme1.getId());
        assertThat(ferme1).isEqualTo(ferme2);

        ferme2 = getFermeSample2();
        assertThat(ferme1).isNotEqualTo(ferme2);
    }

    @Test
    void parcelleTest() throws Exception {
        Ferme ferme = getFermeRandomSampleGenerator();
        Parcelle parcelleBack = getParcelleRandomSampleGenerator();

        ferme.addParcelle(parcelleBack);
        assertThat(ferme.getParcelles()).containsOnly(parcelleBack);
        assertThat(parcelleBack.getFermeLibelle()).isEqualTo(ferme);

        ferme.removeParcelle(parcelleBack);
        assertThat(ferme.getParcelles()).doesNotContain(parcelleBack);
        assertThat(parcelleBack.getFermeLibelle()).isNull();

        ferme.parcelles(new HashSet<>(Set.of(parcelleBack)));
        assertThat(ferme.getParcelles()).containsOnly(parcelleBack);
        assertThat(parcelleBack.getFermeLibelle()).isEqualTo(ferme);

        ferme.setParcelles(new HashSet<>());
        assertThat(ferme.getParcelles()).doesNotContain(parcelleBack);
        assertThat(parcelleBack.getFermeLibelle()).isNull();
    }

    @Test
    void usersTest() throws Exception {
        Ferme ferme = getFermeRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        ferme.setUsers(appUserBack);
        assertThat(ferme.getUsers()).isEqualTo(appUserBack);

        ferme.users(null);
        assertThat(ferme.getUsers()).isNull();
    }

    @Test
    void appUserTest() throws Exception {
        Ferme ferme = getFermeRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        ferme.setAppUser(appUserBack);
        assertThat(ferme.getAppUser()).isEqualTo(appUserBack);

        ferme.appUser(null);
        assertThat(ferme.getAppUser()).isNull();
    }
}
