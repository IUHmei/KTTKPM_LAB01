package com.lab.jwt_resource_server.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private RSAPrivateKey loadPrivateKey() throws Exception {
        String key = Files.readString(Paths.get("private.pem"))
                .replaceAll("-----\\w+ PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(spec);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> req) throws Exception {
            System.out.println("LOGIN API CALLED"); // 👈 thêm dòng này

        String username = req.get("username");

        String token = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
            .signWith(loadPrivateKey(), SignatureAlgorithm.RS256)
            .compact();

        return Map.of(
            "accessToken", token,
            "tokenType", "Bearer"
        );
    }
}

