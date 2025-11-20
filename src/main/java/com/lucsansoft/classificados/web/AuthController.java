package com.lucsansoft.classificados.web;

import com.lucsansoft.classificados.model.User;
import com.lucsansoft.classificados.security.JwtUtil;
import com.lucsansoft.classificados.service.AuthService;
import com.lucsansoft.classificados.service.GeoService;
import com.lucsansoft.classificados.web.dto.AuthDtos.LoginRequest;
import com.lucsansoft.classificados.web.dto.AuthDtos.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@CrossOrigin(origins="*") @RestController @RequestMapping("/api/auth")
public class AuthController {
  private final AuthService auth; private final JwtUtil jwt; private final GeoService geo;
  public AuthController(AuthService auth, JwtUtil jwt, GeoService geo){this.auth=auth; this.jwt=jwt; this.geo=geo;}
  @PostMapping("/register") public ResponseEntity<Map<String,Object>> register(@RequestBody RegisterRequest req){ User u=auth.register(req.name, req.email, req.password); String t=jwt.sign(u.getId().toString(), u.getRole()); return ResponseEntity.ok(Map.of("token", t, "user", Map.of("id", u.getId().toString(), "name", u.getName(), "email", u.getEmail(), "role", u.getRole()))); }
  @PostMapping("/login") public ResponseEntity<Map<String,Object>> login(@RequestBody LoginRequest req, jakarta.servlet.http.HttpServletRequest request){ User u=auth.login(req.email, req.password); String t=jwt.sign(u.getId().toString(), u.getRole()); String ip=GeoService.extractIp(request); java.util.Map<String,Object> g=geo.lookup(ip); return ResponseEntity.ok(Map.of("token", t, "user", Map.of("id", u.getId().toString(), "name", u.getName(), "email", u.getEmail(), "role", u.getRole()), "geo", g)); }
  @GetMapping("/me") public ResponseEntity<Map<String,Object>> me(Authentication a){ if(a==null) return ResponseEntity.status(401).build(); User u=auth.getById(java.util.UUID.fromString(a.getName())); return ResponseEntity.ok(Map.of("id", u.getId().toString(), "name", u.getName(), "email", u.getEmail(), "role", u.getRole())); }
}
