package com.lucsansoft.classificados.web;

import com.lucsansoft.classificados.model.Listing;
import com.lucsansoft.classificados.model.User;
import com.lucsansoft.classificados.service.FavoriteService;
import com.lucsansoft.classificados.service.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
public class ListingControllerTest {
  private MockMvc mvc;
  private ListingService listings;
  private FavoriteService favorites;
  private Listing sample;
  @BeforeEach public void setup(){
    User owner=new User(); owner.setId(UUID.randomUUID()); owner.setName("Ana"); owner.setEmail("ana@example.com"); owner.setPasswordHash("x"); owner.setCreatedAt(Instant.now()); owner.setRole("user");
    sample=new Listing(); sample.setId(UUID.randomUUID()); sample.setPublicId("abcdefghij"); sample.setOwner(owner); sample.setTitle("Bicicleta"); sample.setDescription("Aro 29"); sample.setPrice(new BigDecimal("1200.00")); sample.setCategory("Esportes"); sample.setCity("São Paulo"); sample.setState("SP"); sample.setContactPhone("11999999999"); sample.setCreatedAt(Instant.now()); sample.setViews(0); sample.setIsActive(true); sample.setImages(List.of());
    listings=new ListingService(null,null,null){
      @Override public java.util.List<Listing> search(String q,String category,String city){ return List.of(sample); }
      @Override public java.util.Optional<Listing> getByPublicId(String pid){ return Optional.of(sample); }
    };
    favorites=new FavoriteService(null,null,null){
      @Override public java.util.List<com.lucsansoft.classificados.model.Favorite> listByUser(UUID userId){ return java.util.List.of(); }
    };
    mvc= MockMvcBuilders.standaloneSetup(new ListingController(listings, favorites)).build();
  }
  @Test public void getListingsReturnsItems() throws Exception{
    mvc.perform(get("/api/listings").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.items[0].id").value("abcdefghij"))
      .andExpect(jsonPath("$.items[0].uid").value(sample.getId().toString()))
      .andExpect(jsonPath("$.total").value(1));
  }
  @Test public void getByShortIdUsesPublicId() throws Exception{
    mvc.perform(get("/api/listings/abcdefghij").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value("abcdefghij"))
      .andExpect(jsonPath("$.uid").value(sample.getId().toString()));
  }
}
