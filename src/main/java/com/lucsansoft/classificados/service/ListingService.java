package com.lucsansoft.classificados.service;

import com.lucsansoft.classificados.model.Listing;
import com.lucsansoft.classificados.model.ListingImage;
import com.lucsansoft.classificados.model.User;
import com.lucsansoft.classificados.repo.ListingImageRepository;
import com.lucsansoft.classificados.repo.ListingRepository;
import com.lucsansoft.classificados.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.Collectors;
@Service public class ListingService {
  private final ListingRepository listings; private final ListingImageRepository images; private final UserRepository users;
  public ListingService(ListingRepository listings, ListingImageRepository images, UserRepository users){this.listings=listings; this.images=images; this.users=users;}
  @Transactional public Listing create(java.util.UUID ownerId,String title,String description,BigDecimal price,String category,String city,String state,String contactPhone,java.util.List<String> imageUrls){
    User owner=users.findById(ownerId).orElseThrow(); Listing l=new Listing(); l.setId(java.util.UUID.randomUUID()); l.setPublicId(generatePublicId()); l.setOwner(owner); l.setTitle(title); l.setDescription(description);
    l.setPrice(price); l.setCategory(category); l.setCity(city); l.setState(state); l.setContactPhone(contactPhone); l.setCreatedAt(Instant.now()); l.setViews(0); l.setIsActive(true);
    Listing saved=listings.save(l); if(imageUrls!=null){ for(String url:imageUrls){ ListingImage img=new ListingImage(); img.setListing(saved); img.setUrl(url); images.save(img);} } return saved; }
  public java.util.Optional<Listing> get(java.util.UUID id){ return listings.findById(id); }
  public java.util.Optional<Listing> getByPublicId(String pid){ return listings.findByPublicId(pid); }
  public java.util.List<Listing> search(String q,String category,String city){ java.util.List<Listing> all=listings.findAll().stream().filter(Listing::getIsActive).collect(Collectors.toList());
    if(q!=null && !q.isBlank()){ String qq=q.toLowerCase(); all=all.stream().filter(l-> l.getTitle().toLowerCase().contains(qq)||l.getDescription().toLowerCase().contains(qq)).collect(Collectors.toList()); }
    if(category!=null && !category.isBlank()) all=all.stream().filter(l->category.equals(l.getCategory())).collect(Collectors.toList());
    if(city!=null && !city.isBlank()) all=all.stream().filter(l->l.getCity().equalsIgnoreCase(city)).collect(Collectors.toList());
    all.sort(java.util.Comparator.comparing(Listing::getCreatedAt).reversed()); return all; }
  @Transactional public void incrementViews(java.util.UUID id){ Listing l=listings.findById(id).orElseThrow(); l.setViews(l.getViews()+1); listings.save(l); }
  @Transactional public void delete(java.util.UUID id){ listings.deleteById(id); }
  public java.util.List<String> categories(){ return java.util.Arrays.asList("Imóveis","Autos","Eletrônicos","Móveis","Esportes","Moda","Serviços","Pets","Outros"); }

  private String generatePublicId(){
    String alphabet="abcdefghijklmnopqrstuvwxyz0123456789";
    java.util.Random rnd=new java.util.Random();
    for(int tries=0;tries<5;tries++){
      StringBuilder sb=new StringBuilder(10);
      for(int i=0;i<10;i++) sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
      String pid=sb.toString();
      if(getByPublicId(pid).isEmpty()) return pid;
    }
    return Long.toString(System.currentTimeMillis(),36).substring(0,10);
  }
}
