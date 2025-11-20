package com.lucsansoft.classificados.service;

import com.lucsansoft.classificados.model.Listing;
import com.lucsansoft.classificados.model.ListingImage;
import com.lucsansoft.classificados.model.User;
import com.lucsansoft.classificados.repo.ListingImageRepository;
import com.lucsansoft.classificados.repo.ListingRepository;
import com.lucsansoft.classificados.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ListingServiceTest {
  @Mock private ListingRepository listings;
  @Mock private ListingImageRepository images;
  @Mock private UserRepository users;
  @InjectMocks private ListingService service;
  @Test public void createGeneratesPublicIdAndSavesImages(){
    User owner=new User(); owner.setId(UUID.randomUUID()); owner.setName("Ana"); owner.setEmail("ana@example.com"); owner.setPasswordHash("x"); owner.setCreatedAt(Instant.now()); owner.setRole("user");
    when(users.findById(owner.getId())).thenReturn(Optional.of(owner));
    when(listings.findByPublicId(anyString())).thenReturn(Optional.empty());
    when(listings.save(any())).thenAnswer(inv->inv.getArgument(0));
    Listing l=service.create(owner.getId(),"Bicicleta","Aro 29",new BigDecimal("1200.00"),"Esportes","São Paulo","SP","11999999999", List.of("/assets/bike.jpg","/assets/bike2.jpg"));
    assertNotNull(l.getId());
    assertNotNull(l.getPublicId());
    assertEquals(10,l.getPublicId().length());
    assertEquals("Bicicleta",l.getTitle());
    verify(images, times(2)).save(any(ListingImage.class));
  }
  @Test public void multipleCreatesProduceDifferentPublicIds(){
    User owner=new User(); owner.setId(UUID.randomUUID()); owner.setName("Ana"); owner.setEmail("ana@example.com"); owner.setPasswordHash("x"); owner.setCreatedAt(Instant.now()); owner.setRole("user");
    when(users.findById(owner.getId())).thenReturn(Optional.of(owner));
    when(listings.findByPublicId(anyString())).thenReturn(Optional.empty());
    when(listings.save(any())).thenAnswer(inv->inv.getArgument(0));
    Listing a=service.create(owner.getId(),"Item A","Desc",new BigDecimal("10"),"Outros","São Paulo","SP",null, List.of());
    Listing b=service.create(owner.getId(),"Item B","Desc",new BigDecimal("20"),"Outros","São Paulo","SP",null, List.of());
    assertNotEquals(a.getPublicId(), b.getPublicId());
  }
}
