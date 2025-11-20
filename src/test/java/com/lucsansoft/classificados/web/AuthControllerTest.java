package com.lucsansoft.classificados.web;

import com.lucsansoft.classificados.model.User;
import com.lucsansoft.classificados.security.JwtUtil;
import com.lucsansoft.classificados.service.AuthService;
import com.lucsansoft.classificados.service.GeoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
public class AuthControllerTest {
  private MockMvc mvc;
  @BeforeEach public void setup(){
    AuthService auth = new AuthService(null){
      @Override public User login(String email,String password){ User u=new User(); u.setId(UUID.randomUUID()); u.setName("Ana"); u.setEmail(email); u.setCreatedAt(Instant.now()); u.setRole("user"); u.setPasswordHash("x"); return u; }
    };
    JwtUtil jwt = new JwtUtil("dev-secret");
    GeoService geo = new GeoService(){
      @Override public Map<String,Object> lookup(String ip){ return Map.of("ip","1.2.3.4","country","Brazil","region","SP","city","São Paulo","lat",-23.55,"lon",-46.63); }
    };
    mvc = MockMvcBuilders.standaloneSetup(new AuthController(auth, jwt, geo)).build();
  }
  @Test public void loginReturnsGeo() throws Exception{
    String body = "{\"email\":\"ana@example.com\",\"password\":\"123456\"}";
    mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body).header("X-Forwarded-For","1.2.3.4"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.geo.ip").value("1.2.3.4"))
      .andExpect(jsonPath("$.user.email").value("ana@example.com"))
      .andExpect(jsonPath("$.token").exists());
  }
}
