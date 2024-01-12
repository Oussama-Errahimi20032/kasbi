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
 * A Ferme.
 */
@Table("ferme")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Ferme implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("libelle")
    private String libelle;

    @Column("photo")
    private String photo;

    @Transient
    @JsonIgnoreProperties(value = { "plantages", "ferme", "plantes", "fermeLibelle" }, allowSetters = true)
    private Set<Parcelle> parcelles = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "fermes" }, allowSetters = true)
    private AppUser users;

    @Transient
    @JsonIgnoreProperties(value = { "fermes" }, allowSetters = true)
    private AppUser appUser;

    @Column("users_id")
    private Long usersId;

    @Column("app_user_id")
    private Long appUserId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ferme id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Ferme libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getPhoto() {
        return this.photo;
    }

    public Ferme photo(String photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Set<Parcelle> getParcelles() {
        return this.parcelles;
    }

    public void setParcelles(Set<Parcelle> parcelles) {
        if (this.parcelles != null) {
            this.parcelles.forEach(i -> i.setFermeLibelle(null));
        }
        if (parcelles != null) {
            parcelles.forEach(i -> i.setFermeLibelle(this));
        }
        this.parcelles = parcelles;
    }

    public Ferme parcelles(Set<Parcelle> parcelles) {
        this.setParcelles(parcelles);
        return this;
    }

    public Ferme addParcelle(Parcelle parcelle) {
        this.parcelles.add(parcelle);
        parcelle.setFermeLibelle(this);
        return this;
    }

    public Ferme removeParcelle(Parcelle parcelle) {
        this.parcelles.remove(parcelle);
        parcelle.setFermeLibelle(null);
        return this;
    }

    public AppUser getUsers() {
        return this.users;
    }

    public void setUsers(AppUser appUser) {
        this.users = appUser;
        this.usersId = appUser != null ? appUser.getId() : null;
    }

    public Ferme users(AppUser appUser) {
        this.setUsers(appUser);
        return this;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
        this.appUserId = appUser != null ? appUser.getId() : null;
    }

    public Ferme appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
    }

    public Long getUsersId() {
        return this.usersId;
    }

    public void setUsersId(Long appUser) {
        this.usersId = appUser;
    }

    public Long getAppUserId() {
        return this.appUserId;
    }

    public void setAppUserId(Long appUser) {
        this.appUserId = appUser;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ferme)) {
            return false;
        }
        return getId() != null && getId().equals(((Ferme) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ferme{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", photo='" + getPhoto() + "'" +
            "}";
    }
}
