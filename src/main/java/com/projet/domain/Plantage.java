package com.projet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Plantage.
 */
@Table("plantage")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Plantage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("date")
    private LocalDate date;

    @Column("nombre")
    private Integer nombre;

    @Transient
    @JsonIgnoreProperties(value = { "plantages", "typePlante", "nom", "parcelles" }, allowSetters = true)
    private Plante planteLibelle;

    @Transient
    @JsonIgnoreProperties(value = { "plantages", "ferme", "plantes", "fermeLibelle" }, allowSetters = true)
    private Parcelle parcelleLibelle;

    @Column("plante_libelle_id")
    private Long planteLibelleId;

    @Column("parcelle_libelle_id")
    private Long parcelleLibelleId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Plantage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Plantage date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getNombre() {
        return this.nombre;
    }

    public Plantage nombre(Integer nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(Integer nombre) {
        this.nombre = nombre;
    }

    public Plante getPlanteLibelle() {
        return this.planteLibelle;
    }

    public void setPlanteLibelle(Plante plante) {
        this.planteLibelle = plante;
        this.planteLibelleId = plante != null ? plante.getId() : null;
    }

    public Plantage planteLibelle(Plante plante) {
        this.setPlanteLibelle(plante);
        return this;
    }

    public Parcelle getParcelleLibelle() {
        return this.parcelleLibelle;
    }

    public void setParcelleLibelle(Parcelle parcelle) {
        this.parcelleLibelle = parcelle;
        this.parcelleLibelleId = parcelle != null ? parcelle.getId() : null;
    }

    public Plantage parcelleLibelle(Parcelle parcelle) {
        this.setParcelleLibelle(parcelle);
        return this;
    }

    public Long getPlanteLibelleId() {
        return this.planteLibelleId;
    }

    public void setPlanteLibelleId(Long plante) {
        this.planteLibelleId = plante;
    }

    public Long getParcelleLibelleId() {
        return this.parcelleLibelleId;
    }

    public void setParcelleLibelleId(Long parcelle) {
        this.parcelleLibelleId = parcelle;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Plantage)) {
            return false;
        }
        return getId() != null && getId().equals(((Plantage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Plantage{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", nombre=" + getNombre() +
            "}";
    }
}
