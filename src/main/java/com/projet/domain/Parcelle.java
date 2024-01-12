package com.projet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Parcelle.
 */
@Table("parcelle")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Parcelle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("libelle")
    private String libelle;

    @Column("photo")
    private String photo;

    @Transient
    @JsonIgnoreProperties(value = { "planteLibelle", "parcelleLibelle" }, allowSetters = true)
    private Set<Plantage> plantages = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "parcelles", "users", "appUser" }, allowSetters = true)
    private Ferme ferme;

    @Transient
    @JsonIgnoreProperties(value = { "plantages", "typePlante", "nom", "parcelles" }, allowSetters = true)
    private Set<Plante> plantes = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "parcelles", "users", "appUser" }, allowSetters = true)
    private Ferme fermeLibelle;

    @Column("ferme_id")
    private Long fermeId;

    @Column("ferme_libelle_id")
    private Long fermeLibelleId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Parcelle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Parcelle libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getPhoto() {
        return this.photo;
    }

    public Parcelle photo(String photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Set<Plantage> getPlantages() {
        return this.plantages;
    }

    public void setPlantages(Set<Plantage> plantages) {
        if (this.plantages != null) {
            this.plantages.forEach(i -> i.setParcelleLibelle(null));
        }
        if (plantages != null) {
            plantages.forEach(i -> i.setParcelleLibelle(this));
        }
        this.plantages = plantages;
    }

    public Parcelle plantages(Set<Plantage> plantages) {
        this.setPlantages(plantages);
        return this;
    }

    public Parcelle addPlantage(Plantage plantage) {
        this.plantages.add(plantage);
        plantage.setParcelleLibelle(this);
        return this;
    }

    public Parcelle removePlantage(Plantage plantage) {
        this.plantages.remove(plantage);
        plantage.setParcelleLibelle(null);
        return this;
    }

    public Ferme getFerme() {
        return this.ferme;
    }

    public void setFerme(Ferme ferme) {
        this.ferme = ferme;
        this.fermeId = ferme != null ? ferme.getId() : null;
    }

    public Parcelle ferme(Ferme ferme) {
        this.setFerme(ferme);
        return this;
    }

    public Set<Plante> getPlantes() {
        return this.plantes;
    }

    public void setPlantes(Set<Plante> plantes) {
        this.plantes = plantes;
    }

    public Parcelle plantes(Set<Plante> plantes) {
        this.setPlantes(plantes);
        return this;
    }

    public Parcelle addPlantes(Plante plante) {
        this.plantes.add(plante);
        return this;
    }

    public Parcelle removePlantes(Plante plante) {
        this.plantes.remove(plante);
        return this;
    }

    public Ferme getFermeLibelle() {
        return this.fermeLibelle;
    }

    public void setFermeLibelle(Ferme ferme) {
        this.fermeLibelle = ferme;
        this.fermeLibelleId = ferme != null ? ferme.getId() : null;
    }

    public Parcelle fermeLibelle(Ferme ferme) {
        this.setFermeLibelle(ferme);
        return this;
    }

    public Long getFermeId() {
        return this.fermeId;
    }

    public void setFermeId(Long ferme) {
        this.fermeId = ferme;
    }

    public Long getFermeLibelleId() {
        return this.fermeLibelleId;
    }

    public void setFermeLibelleId(Long ferme) {
        this.fermeLibelleId = ferme;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Parcelle)) {
            return false;
        }
        return getId() != null && getId().equals(((Parcelle) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Parcelle{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", photo='" + getPhoto() + "'" +
            "}";
    }
}
