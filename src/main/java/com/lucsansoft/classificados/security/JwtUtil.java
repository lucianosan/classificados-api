package com.lucsansoft.classificados.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
public class JwtUtil {
  private final SecretKey key;
  public JwtUtil(String secret){ try{ byte[] k = MessageDigest.getInstance("SHA-256").digest(secret.getBytes()); this.key = Keys.hmacShaKeyFor(k);}catch(Exception e){ throw new RuntimeException(e);} }
  public String sign(String sub, String role){
    Instant now = Instant.now();
    return Jwts.builder().setSubject(sub).addClaims(Map.of("role", role)).setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plusSeconds(7*24*3600))).signWith(key, SignatureAlgorithm.HS256).compact();
  }
  public io.jsonwebtoken.Claims verify(String token){ return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody(); }
}
