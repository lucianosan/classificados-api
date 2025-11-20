package com.lucsansoft.classificados.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;
@Service
public class GeoService {
  private final HttpClient http = HttpClient.newHttpClient();
  private final ObjectMapper mapper = new ObjectMapper();
  public Map<String,Object> lookup(String ip){
    Map<String,Object> m = new LinkedHashMap<>();
    m.put("ip", ip);
    try{
      if(ip==null || ip.isBlank()) return m;
      HttpRequest req = HttpRequest.newBuilder(URI.create("https://ipwho.is/"+ip)).GET().build();
      HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
      Map<?,?> json = mapper.readValue(res.body(), Map.class);
      Object success = json.get("success");
      if(Boolean.TRUE.equals(success)){
        m.put("country", json.get("country"));
        m.put("region", json.get("region"));
        m.put("city", json.get("city"));
        m.put("lat", json.get("latitude"));
        m.put("lon", json.get("longitude"));
      }
    }catch(Exception ignore){ }
    return m;
  }
  public static String extractIp(jakarta.servlet.http.HttpServletRequest req){
    String h = req.getHeader("X-Forwarded-For");
    if(h!=null && !h.isBlank()) return h.split(",")[0].trim();
    h = req.getHeader("X-Real-IP");
    if(h!=null && !h.isBlank()) return h.trim();
    return req.getRemoteAddr();
  }
}
