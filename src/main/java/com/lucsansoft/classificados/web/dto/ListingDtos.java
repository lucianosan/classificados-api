package com.lucsansoft.classificados.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
public class ListingDtos {
  public static class CreateRequest {
    public UUID ownerId; public String title; public String description; public BigDecimal price; public String category; public String city; public String state; public String contactPhone; public List<String> images;
  }
}
