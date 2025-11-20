package com.lucsansoft.classificados.service;

import com.lucsansoft.classificados.model.User;
import com.lucsansoft.classificados.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
  @Mock private UserRepository users;
  @InjectMocks private AuthService auth;
  @Test public void registerCreatesUser(){
    when(users.findByEmailIgnoreCase("ana@example.com")).thenReturn(Optional.empty());
    when(users.save(any())).thenAnswer(inv->inv.getArgument(0));
    User u=auth.register("Ana","ana@example.com","123456");
    assertNotNull(u.getId());
    assertEquals("Ana",u.getName());
    assertEquals("ana@example.com",u.getEmail());
    assertNotNull(u.getPasswordHash());
    assertNotNull(u.getCreatedAt());
    assertEquals("user",u.getRole());
  }
  @Test public void registerDuplicateEmailThrows(){
    User existing=new User(); existing.setId(UUID.randomUUID()); existing.setEmail("ana@example.com"); existing.setName("Ana"); existing.setPasswordHash("x"); existing.setCreatedAt(Instant.now());
    when(users.findByEmailIgnoreCase("ana@example.com")).thenReturn(Optional.of(existing));
    assertThrows(IllegalArgumentException.class,()->auth.register("Ana","ana@example.com","123456"));
  }
  @Test public void loginSuccess(){
    User u=new User(); u.setId(UUID.randomUUID()); u.setName("Ana"); u.setEmail("ana@example.com"); u.setCreatedAt(Instant.now()); u.setRole("user");
    String h;
    try{
      var md=java.security.MessageDigest.getInstance("SHA-256");
      byte[] out=md.digest("123456".getBytes(java.nio.charset.StandardCharsets.UTF_8));
      StringBuilder sb=new StringBuilder(); for(byte b:out) sb.append(String.format("%02x",b)); h=sb.toString();
    }catch(Exception e){ throw new RuntimeException(e); }
    u.setPasswordHash(h);
    when(users.findByEmailIgnoreCase("ana@example.com")).thenReturn(Optional.of(u));
    assertDoesNotThrow(()->auth.login("ana@example.com","123456"));
  }
  @Test public void loginInvalidThrows(){
    when(users.findByEmailIgnoreCase("ana@example.com")).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class,()->auth.login("ana@example.com","123456"));
  }
}
