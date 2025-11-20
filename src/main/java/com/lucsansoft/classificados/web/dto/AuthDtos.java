package com.lucsansoft.classificados.web.dto;
public class AuthDtos {
  public static class RegisterRequest { public String name; public String email; public String password; }
  public static class LoginRequest { public String email; public String password; }
}
