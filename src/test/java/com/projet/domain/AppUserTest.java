package com.projet.domain;

import static com.projet.domain.AppUserTestSamples.*;
import static com.projet.domain.FermeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.projet.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AppUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppUser.class);
        AppUser appUser1 = getAppUserSample1();
        AppUser appUser2 = new AppUser();
        assertThat(appUser1).isNotEqualTo(appUser2);

        appUser2.setId(appUser1.getId());
        assertThat(appUser1).isEqualTo(appUser2);

        appUser2 = getAppUserSample2();
        assertThat(appUser1).isNotEqualTo(appUser2);
    }

    @Test
    void fermeTest() throws Exception {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Ferme fermeBack = getFermeRandomSampleGenerator();

        appUser.addFerme(fermeBack);
        assertThat(appUser.getFermes()).containsOnly(fermeBack);
        assertThat(fermeBack.getAppUser()).isEqualTo(appUser);

        appUser.removeFerme(fermeBack);
        assertThat(appUser.getFermes()).doesNotContain(fermeBack);
        assertThat(fermeBack.getAppUser()).isNull();

        appUser.fermes(new HashSet<>(Set.of(fermeBack)));
        assertThat(appUser.getFermes()).containsOnly(fermeBack);
        assertThat(fermeBack.getAppUser()).isEqualTo(appUser);

        appUser.setFermes(new HashSet<>());
        assertThat(appUser.getFermes()).doesNotContain(fermeBack);
        assertThat(fermeBack.getAppUser()).isNull();
    }
}
