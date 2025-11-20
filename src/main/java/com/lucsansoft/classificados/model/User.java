package com.lucsansoft.classificados.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="users", indexes=@Index(name="idx_users_email", columnList="email", unique=true))
public class User {
  @Id @Column(nullable=false, updatable=false) private UUID id;
  @Column(nullable=false) private String name;
  @Column(nullable=false, unique=true) private String email;
  @Column(nullable=false, name="password_hash") private String passwordHash;
  @Column(nullable=false, name="created_at") private Instant createdAt;
  @Column(nullable=false) private String role = "user";
  public User() {}
  public UUID getId(){return id;} public void setId(UUID id){this.id=id;}
  public String getName(){return name;} public void setName(String name){this.name=name;}
  public String getEmail(){return email;} public void setEmail(String email){this.email=email;}
  public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String passwordHash){this.passwordHash=passwordHash;}
  public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant createdAt){this.createdAt=createdAt;}
  public String getRole(){return role;} public void setRole(String role){this.role=role;}
}
