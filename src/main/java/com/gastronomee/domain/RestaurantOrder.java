package com.gastronomee.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

import com.gastronomee.domain.enumeration.CurrencyType;
import com.gastronomee.domain.enumeration.RestaurantOrderStatus;

/**
 * A RestaurantOrder.
 */
@Entity
@Table(name = "restaurant_order")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "restaurantorder")
public class RestaurantOrder implements Serializable {

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
    @Column(name = "persons", nullable = false)
    private Integer persons;

    @NotNull
    @Column(name = "jhi_comment", nullable = false)
    private String comment;
    
    @Column(name = "order_date")
    private ZonedDateTime orderDate;

    @Column(name = "created")
    private ZonedDateTime created;

    @Column(name = "updated")
    private ZonedDateTime updated;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RestaurantOrderStatus status;
    
    @Column(name = "price")
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private CurrencyType currency;

    @ManyToOne
    private User user;

    @ManyToOne
    private Restaurant restaurant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRate() {
        return rate;
    }

    public RestaurantOrder rate(Integer rate) {
        this.rate = rate;
        return this;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getPersons() {
        return persons;
    }

    public RestaurantOrder persons(Integer persons) {
        this.persons = persons;
        return this;
    }

    public void setPersons(Integer persons) {
        this.persons = persons;
    }

    public String getComment() {
        return comment;
    }

    public RestaurantOrder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public RestaurantOrder created(ZonedDateTime created) {
        this.created = created;
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public RestaurantOrder updated(ZonedDateTime updated) {
        this.updated = updated;
        return this;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public RestaurantOrderStatus getStatus() {
        return status;
    }

    public RestaurantOrder status(RestaurantOrderStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(RestaurantOrderStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public RestaurantOrder user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public RestaurantOrder restaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        return this;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public ZonedDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(ZonedDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public CurrencyType getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyType currency) {
		this.currency = currency;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RestaurantOrder restaurantOrder = (RestaurantOrder) o;
        if (restaurantOrder.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), restaurantOrder.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RestaurantOrder{" +
            "id=" + getId() +
            ", rate='" + getRate() + "'" +
            ", persons='" + getPersons() + "'" +
            ", comment='" + getComment() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
