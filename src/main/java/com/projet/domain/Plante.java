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
 * A Plante.
 */
@Table("plante")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Plante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("libelle")
    private String libelle;

    @Column("racine")
    private String racine;

    @Column("image")
    private String image;

    @Transient
    @JsonIgnoreProperties(value = { "planteLibelle", "parcelleLibelle" }, allowSetters = true)
    private Set<Plantage> plantages = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "plantes" }, allowSetters = true)
    private TypePlante typePlante;

    @Transient
    @JsonIgnoreProperties(value = { "plantes" }, allowSetters = true)
    private TypePlante nom;

    @Transient
    @JsonIgnoreProperties(value = { "plantages", "ferme", "plantes", "fermeLibelle" }, allowSetters = true)
    private Set<Parcelle> parcelles = new HashSet<>();

    @Column("type_plante_id")
    private Long typePlanteId;

    @Column("nom_id")
    private Long nomId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Plante id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Plante libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getRacine() {
        return this.racine;
    }

    public Plante racine(String racine) {
        this.setRacine(racine);
        return this;
    }

    public void setRacine(String racine) {
        this.racine = racine;
    }

    public String getImage() {
        return this.image;
    }

    public Plante image(String image) {
        this.setImage(image);
        return this;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<Plantage> getPlantages() {
        return this.plantages;
    }

    public void setPlantages(Set<Plantage> plantages) {
        if (this.plantages != null) {
            this.plantages.forEach(i -> i.setPlanteLibelle(null));
        }
        if (plantages != null) {
            plantages.forEach(i -> i.setPlanteLibelle(this));
        }
        this.plantages = plantages;
    }

    public Plante plantages(Set<Plantage> plantages) {
        this.setPlantages(plantages);
        return this;
    }

    public Plante addPlantage(Plantage plantage) {
        this.plantages.add(plantage);
        plantage.setPlanteLibelle(this);
        return this;
    }

    public Plante removePlantage(Plantage plantage) {
        this.plantages.remove(plantage);
        plantage.setPlanteLibelle(null);
        return this;
    }

    public TypePlante getTypePlante() {
        return this.typePlante;
    }

    public void setTypePlante(TypePlante typePlante) {
        this.typePlante = typePlante;
        this.typePlanteId = typePlante != null ? typePlante.getId() : null;
    }

    public Plante typePlante(TypePlante typePlante) {
        this.setTypePlante(typePlante);
        return this;
    }

    public TypePlante getNom() {
        return this.nom;
    }

    public void setNom(TypePlante typePlante) {
        this.nom = typePlante;
        this.nomId = typePlante != null ? typePlante.getId() : null;
    }

    public Plante nom(TypePlante typePlante) {
        this.setNom(typePlante);
        return this;
    }

    public Set<Parcelle> getParcelles() {
        return this.parcelles;
    }

    public void setParcelles(Set<Parcelle> parcelles) {
        if (this.parcelles != null) {
            this.parcelles.forEach(i -> i.removePlantes(this));
        }
        if (parcelles != null) {
            parcelles.forEach(i -> i.addPlantes(this));
        }
        this.parcelles = parcelles;
    }

    public Plante parcelles(Set<Parcelle> parcelles) {
        this.setParcelles(parcelles);
        return this;
    }

    public Plante addParcelles(Parcelle parcelle) {
        this.parcelles.add(parcelle);
        parcelle.getPlantes().add(this);
        return this;
    }

    public Plante removeParcelles(Parcelle parcelle) {
        this.parcelles.remove(parcelle);
        parcelle.getPlantes().remove(this);
        return this;
    }

    public Long getTypePlanteId() {
        return this.typePlanteId;
    }

    public void setTypePlanteId(Long typePlante) {
        this.typePlanteId = typePlante;
    }

    public Long getNomId() {
        return this.nomId;
    }

    public void setNomId(Long typePlante) {
        this.nomId = typePlante;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Plante)) {
            return false;
        }
        return getId() != null && getId().equals(((Plante) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Plante{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", racine='" + getRacine() + "'" +
            ", image='" + getImage() + "'" +
            "}";
    }
}
