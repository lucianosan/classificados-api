package com.lucsansoft.classificados.model;
import jakarta.persistence.*;
@Entity @Table(name="favorites", uniqueConstraints=@UniqueConstraint(name="uk_fav_user_listing", columnNames={"user_id","listing_id"}))
public class Favorite {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false) @JoinColumn(name="user_id", nullable=false) private User user;
  @ManyToOne(optional=false) @JoinColumn(name="listing_id", nullable=false) private Listing listing;
  public Favorite() {}
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public User getUser(){return user;} public void setUser(User user){this.user=user;}
  public Listing getListing(){return listing;} public void setListing(Listing listing){this.listing=listing;}
}
