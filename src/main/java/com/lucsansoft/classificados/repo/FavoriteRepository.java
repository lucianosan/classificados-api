package com.lucsansoft.classificados.repo;

import com.lucsansoft.classificados.model.Favorite;
import com.lucsansoft.classificados.model.Listing;
import com.lucsansoft.classificados.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
  List<Favorite> findByUser(User user);
  Optional<Favorite> findByUserAndListing(User user, Listing listing);
}
