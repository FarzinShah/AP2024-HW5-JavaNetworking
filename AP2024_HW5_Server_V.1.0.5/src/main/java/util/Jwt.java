package util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class Jwt {
    private String SECRET_KEY = "AP2024_HW5_#####";

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        int expirationTimeLimit = 8;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expirationTimeLimit)) // todo: اعتبارش 8 ساعت باشه
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Boolean validateToken(String token, String arg) {
        final String extractedUsername = extractArg(token);
        return (extractedUsername.equals(arg) && !isTokenExpired(token));
    }

    public String extractArg(String token) {
        return extractAllClaims(token).getSubject();
    } //todo: توکن میگیره اون آرگومان مطلوب رو میده

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}

class Test8 {
    public static void main(String[] args) {
        Jwt jwtUtil = new Jwt();
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        System.out.println("Generated Token: " + token);
        boolean isValid = jwtUtil.validateToken(token, username);
        System.out.println("Is token valid: " + isValid);
        String extractedUsername = jwtUtil.extractArg(token);
        System.out.println("Extracted Username: " + extractedUsername);
    }
}