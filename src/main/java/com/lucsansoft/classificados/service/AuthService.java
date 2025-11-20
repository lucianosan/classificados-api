package com.lucsansoft.classificados.service;

import com.lucsansoft.classificados.model.User;
import com.lucsansoft.classificados.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
@Service public class AuthService {
  private final UserRepository users; public AuthService(UserRepository users){this.users=users;}
  public User register(String name,String email,String password){ if(users.findByEmailIgnoreCase(email).isPresent()) throw new IllegalArgumentException("Email já cadastrado");
    User u=new User(); u.setId(UUID.randomUUID()); u.setName(name); u.setEmail(email); u.setPasswordHash(hash(password)); u.setCreatedAt(Instant.now()); u.setRole("user"); return users.save(u); }
  public User login(String email,String password){ Optional<User> u=users.findByEmailIgnoreCase(email); if(u.isEmpty()) throw new IllegalArgumentException("Credenciais inválidas");
    if(!u.get().getPasswordHash().equals(hash(password))) throw new IllegalArgumentException("Credenciais inválidas"); return u.get(); }
  public User getById(UUID id){ return users.findById(id).orElseThrow(); }
  private String hash(String s){ try{ MessageDigest md=MessageDigest.getInstance("SHA-256"); byte[] out=md.digest(s.getBytes(StandardCharsets.UTF_8));
    StringBuilder sb=new StringBuilder(); for(byte b:out) sb.append(String.format("%02x",b)); return sb.toString(); }catch(Exception e){ throw new RuntimeException(e);} }
}
