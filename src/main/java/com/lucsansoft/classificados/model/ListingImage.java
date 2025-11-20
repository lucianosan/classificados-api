package com.lucsansoft.classificados.model;
import jakarta.persistence.*;
@Entity @Table(name="listing_images")
public class
ListingImage {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false) @JoinColumn(name="listing_id", nullable=false) private Listing listing;
  @Column(nullable=false, length=4000) private String url;
  public ListingImage() {}
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public Listing getListing(){return listing;} public void setListing(Listing listing){this.listing=listing;}
  public String getUrl(){return url;} public void setUrl(String url){this.url=url;}
}
