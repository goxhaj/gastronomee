package com.gastronomee.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DishOrder.
 */
@Entity
@Table(name = "dish_order")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "dishorder")
public class DishOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Max(value = 10)
    @Column(name = "rate", nullable = false)
    private Integer rate;

    @NotNull
    @Min(value = 1)
    @Column(name = "nr", nullable = false)
    private Integer nr;

    @NotNull
    @Column(name = "jhi_comment", nullable = false)
    private String comment;

    @Column(name = "created")
    private ZonedDateTime created;

    @Column(name = "updated")
    private ZonedDateTime updated;

    @ManyToOne
    private RestaurantOrder restaurantOrder;

    @ManyToOne
    private Dish dish;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRate() {
        return rate;
    }

    public DishOrder rate(Integer rate) {
        this.rate = rate;
        return this;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getNr() {
        return nr;
    }

    public DishOrder nr(Integer nr) {
        this.nr = nr;
        return this;
    }

    public void setNr(Integer nr) {
        this.nr = nr;
    }

    public String getComment() {
        return comment;
    }

    public DishOrder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public DishOrder created(ZonedDateTime created) {
        this.created = created;
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public DishOrder updated(ZonedDateTime updated) {
        this.updated = updated;
        return this;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public RestaurantOrder getRestaurantOrder() {
        return restaurantOrder;
    }

    public DishOrder restaurantOrder(RestaurantOrder restaurantOrder) {
        this.restaurantOrder = restaurantOrder;
        return this;
    }

    public void setRestaurantOrder(RestaurantOrder restaurantOrder) {
        this.restaurantOrder = restaurantOrder;
    }

    public Dish getDish() {
        return dish;
    }

    public DishOrder dish(Dish dish) {
        this.dish = dish;
        return this;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DishOrder dishOrder = (DishOrder) o;
        if (dishOrder.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), dishOrder.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DishOrder{" +
            "id=" + getId() +
            ", rate='" + getRate() + "'" +
            ", nr='" + getNr() + "'" +
            ", comment='" + getComment() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            "}";
    }
}
