package com.gastronomee.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import com.gastronomee.domain.enumeration.DayOfWeek;

/**
 * A Restaurant.
 */
@Entity
@Table(name = "restaurant")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "restaurant")
public class Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 2500)
    @Column(name = "description", length = 2500)
    private String description;

    @NotNull
    @Column(name = "jhi_open", nullable = false)
    private String open;

    @NotNull
    @Column(name = "jhi_close", nullable = false)
    private String close;

    @Column(name = "jhi_tables")
    private Integer tables;

    @Column(name = "chairs")
    private Integer chairs;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week_closed")
    private DayOfWeek dayOfWeekClosed;

    @Column(name = "opened")
    private boolean opened;

    @OneToOne(orphanRemoval=true)
    @JoinColumn(unique = true)
    private Location location;

    @ManyToOne
    private User manager;
    
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE)
    private Set<Menu> menu;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public Integer getTables() {
        return tables;
    }

    public void setTables(Integer tables) {
        this.tables = tables;
    }

    public Integer getChairs() {
        return chairs;
    }

    public void setChairs(Integer chairs) {
        this.chairs = chairs;
    }

    public DayOfWeek getDayOfWeekClosed() {
        return dayOfWeekClosed;
    }

    public void setDayOfWeekClosed(DayOfWeek dayOfWeekClosed) {
        this.dayOfWeekClosed = dayOfWeekClosed;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User user) {
        this.manager = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Restaurant restaurant = (Restaurant) o;
        if (restaurant.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, restaurant.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", open='" + open + "'" +
            ", close='" + close + "'" +
            ", tables='" + tables + "'" +
            ", chairs='" + chairs + "'" +
            ", dayOfWeekClosed='" + dayOfWeekClosed + "'" +
            ", opened='" + opened + "'" +
            '}';
    }
}
