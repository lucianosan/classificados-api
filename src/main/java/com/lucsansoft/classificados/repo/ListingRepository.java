package com.lucsansoft.classificados.repo;

import com.lucsansoft.classificados.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
public interface ListingRepository extends JpaRepository<Listing, UUID> { Optional<Listing> findByPublicId(String publicId); }
