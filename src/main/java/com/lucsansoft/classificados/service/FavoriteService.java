package com.lucsansoft.classificados.service;

import com.lucsansoft.classificados.model.Favorite;
import com.lucsansoft.classificados.model.Listing;
import com.lucsansoft.classificados.model.User;
import com.lucsansoft.classificados.repo.FavoriteRepository;
import com.lucsansoft.classificados.repo.ListingRepository;
import com.lucsansoft.classificados.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Service public class FavoriteService {
  private final FavoriteRepository favorites; private final UserRepository users; private final ListingRepository listings;
  public FavoriteService(FavoriteRepository favorites, UserRepository users, ListingRepository listings){this.favorites=favorites; this.users=users; this.listings=listings;}
  public List<Favorite> listByUser(UUID userId){ User u=users.findById(userId).orElseThrow(); return favorites.findByUser(u); }
  @Transactional public void add(UUID userId, UUID listingId){ User u=users.findById(userId).orElseThrow(); Listing l=listings.findById(listingId).orElseThrow();
    favorites.findByUserAndListing(u,l).orElseGet(()->{ Favorite f=new Favorite(); f.setUser(u); f.setListing(l); return favorites.save(f); }); }
  @Transactional public void remove(UUID userId, UUID listingId){ User u=users.findById(userId).orElseThrow(); Listing l=listings.findById(listingId).orElseThrow(); favorites.findByUserAndListing(u,l).ifPresent(favorites::delete); }
}
