package com.lucsansoft.classificados.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
@Entity @Table(name="listings", indexes={@Index(name="idx_listings_category", columnList="category"),@Index(name="idx_listings_city", columnList="city"),@Index(name="idx_listings_created_at", columnList="created_at")})
public class Listing {
  @Id @Column(nullable=false, updatable=false) private java.util.UUID id;
  @Column(name="public_id", length=10, unique=true) private String publicId;
  @ManyToOne(optional=false) @JoinColumn(name="owner_id", nullable=false) private User owner;
  @Column(nullable=false) private String title;
  @Column(nullable=false, length=4000) private String description;
  @Column(nullable=false, precision=12, scale=2) private BigDecimal price;
  @Column(nullable=false) private String category;
  @Column(nullable=false) private String city;
  @Column(nullable=false) private String state;
  @Column(name="contact_phone") private String contactPhone;
  @Column(nullable=false, name="created_at") private java.time.Instant createdAt;
  @Column(nullable=false) private int views;
  @Column(nullable=false, name="is_active") private boolean isActive;
  @OneToMany(mappedBy="listing", cascade=CascadeType.ALL, orphanRemoval=true) private java.util.List<ListingImage> images=new java.util.ArrayList<>();
  public Listing() {}
  public java.util.UUID getId(){return id;} public void setId(java.util.UUID id){this.id=id;}
  public String getPublicId(){return publicId;} public void setPublicId(String publicId){this.publicId=publicId;}
  public User getOwner(){return owner;} public void setOwner(User owner){this.owner=owner;}
  public String getTitle(){return title;} public void setTitle(String title){this.title=title;}
  public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
  public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal price){this.price=price;}
  public String getCategory(){return category;} public void setCategory(String category){this.category=category;}
  public String getCity(){return city;} public void setCity(String city){this.city=city;}
  public String getState(){return state;} public void setState(String state){this.state=state;}
  public String getContactPhone(){return contactPhone;} public void setContactPhone(String contactPhone){this.contactPhone=contactPhone;}
  public java.time.Instant getCreatedAt(){return createdAt;} public void setCreatedAt(java.time.Instant createdAt){this.createdAt=createdAt;}
  public int getViews(){return views;} public void setViews(int views){this.views=views;}
  public boolean getIsActive(){return isActive;} public void setIsActive(boolean isActive){this.isActive=isActive;}
  public java.util.List<ListingImage> getImages(){return images;} public void setImages(java.util.List<ListingImage> images){this.images=images;}
}
