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
 * A TypePlante.
 */
@Table("type_plante")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TypePlante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("humidite_max")
    private Integer humiditeMax;

    @Column("humidite_min")
    private Integer humiditeMin;

    @Column("temperature")
    private Integer temperature;

    @Transient
    @JsonIgnoreProperties(value = { "plantages", "typePlante", "nom", "parcelles" }, allowSetters = true)
    private Set<Plante> plantes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TypePlante id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public TypePlante name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getHumiditeMax() {
        return this.humiditeMax;
    }

    public TypePlante humiditeMax(Integer humiditeMax) {
        this.setHumiditeMax(humiditeMax);
        return this;
    }

    public void setHumiditeMax(Integer humiditeMax) {
        this.humiditeMax = humiditeMax;
    }

    public Integer getHumiditeMin() {
        return this.humiditeMin;
    }

    public TypePlante humiditeMin(Integer humiditeMin) {
        this.setHumiditeMin(humiditeMin);
        return this;
    }

    public void setHumiditeMin(Integer humiditeMin) {
        this.humiditeMin = humiditeMin;
    }

    public Integer getTemperature() {
        return this.temperature;
    }

    public TypePlante temperature(Integer temperature) {
        this.setTemperature(temperature);
        return this;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Set<Plante> getPlantes() {
        return this.plantes;
    }

    public void setPlantes(Set<Plante> plantes) {
        if (this.plantes != null) {
            this.plantes.forEach(i -> i.setNom(null));
        }
        if (plantes != null) {
            plantes.forEach(i -> i.setNom(this));
        }
        this.plantes = plantes;
    }

    public TypePlante plantes(Set<Plante> plantes) {
        this.setPlantes(plantes);
        return this;
    }

    public TypePlante addPlante(Plante plante) {
        this.plantes.add(plante);
        plante.setNom(this);
        return this;
    }

    public TypePlante removePlante(Plante plante) {
        this.plantes.remove(plante);
        plante.setNom(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypePlante)) {
            return false;
        }
        return getId() != null && getId().equals(((TypePlante) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TypePlante{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", humiditeMax=" + getHumiditeMax() +
            ", humiditeMin=" + getHumiditeMin() +
            ", temperature=" + getTemperature() +
            "}";
    }
}
