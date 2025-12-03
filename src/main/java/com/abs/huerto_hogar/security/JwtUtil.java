package com.abs.huerto_hogar.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// Creación y validación de tokens JWT 
@Component
public class JwtUtil {

    // clave usada para firmar y validar los tokens JWT
    private final String SECRET_KEY = "mi_clave_secreta_muy_larga_para_huerto_hogar_2024_abs";

    // tiempo de expiración del token en milisegundos
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 horas

    // Genera la clave usada para firmar los tokens
    private SecretKey getSigningKey() {
        // Convierte la cadena SECRET_KEY a un arreglo de bytes
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        // Usa la clase Keys de jjwt para generar la clave HMAC SHA
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Genera un token JWT para un usuario
    public String generateToken(UserDetails userDetails) {

        // Crea un Map para guardar los "claims" "datos extras que irán dentro del
        // token"
        Map<String, Object> claims = new HashMap<>();

        // Obtiene la lista de rol del usuario desde sus authorities
        List<String> authorities = userDetails.getAuthorities().stream()
                // Cada GrantedAuthority se transforma a su String )
                .map(GrantedAuthority::getAuthority)
                // Se recoge el resultado en una lista de String
                .collect(Collectors.toList());

        // Agrega la lista de roles al Map de claims bajo la llave "authorities"
        claims.put("authorities", authorities);

        // Construye el token JWT
        return Jwts.builder()
                // Asigna los claims personalizados
                .setClaims(claims)
                // Define el "subject" del token con el username del usuario
                .setSubject(userDetails.getUsername())
                // Fecha y hora en que se emite el token "Ahora mismo"
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Fecha y hora en que expirará el token (Ahora mismo + EXPIRATION_TIME)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // Firma el token con la clave secreta y el algoritmo HS256
                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                // Compacta todo en un String en formato JWT (header.payload.signature)
                .compact();
    }

    // Extrae el "username" (subject) del token
    public String extractUsername(String token) {
        // Usa extractClaim para obtener el subject del token
        return extractClaim(token, Claims::getSubject);
    }

    // Indica al compilador que ignore el warning del cast al convertir el claim a
    // List<String>
    @SuppressWarnings("unchecked")
    public List<String> extractAuthorities(String token) {
        // Obtiene todos los claims del token
        Claims claims = extractAllClaims(token);
        // Obtiene el claim "authorities" y lo convierte a List<String>
        return (List<String>) claims.get("authorities");
    }

    // Valida un token comparando el username y verificando que no esté expirado
    public boolean validateToken(String token, UserDetails userDetails) {
        // Extrae el username que viene dentro del token
        final String username = extractUsername(token);
        // El token es válido si:
        // 1) El username del token coincide con el del UserDetails
        // 2) El token NO está expirado
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Pregunta si el token está expirado
    private boolean isTokenExpired(String token) {
        // El token se considera expirado si su fecha de expiración es anterior a
        // "ahora"
        return extractExpiration(token).before(new Date());
    }

    // Extrae la fecha de expiración (exp) del token
    private Date extractExpiration(String token) {
        // Usa extractClaim para obtener el claim de expiración
        return extractClaim(token, Claims::getExpiration);
    }

    // Método genérico para extraer cualquier claim del token
    // T es un tipo genérico (por ejemplo String, Date, List, etc.)
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // Obtiene todos los claims del token
        final Claims claims = extractAllClaims(token);
        // Aplica la función claimsResolver sobre los claims para devolver el dato
        // deseado
        return claimsResolver.apply(claims);
    }

    // Obtiene todos los claims (payload) del token
    private Claims extractAllClaims(String token) {
        // Crea un parser de JWT configurado con la misma clave secreta
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                // Parsea y valida el token (firma, formato, etc.) y devuelve el JWS
                .parseClaimsJws(token)
                // Obtiene solo el "body", que son los claims (datos dentro del JWT)
                .getBody();
    }
}
