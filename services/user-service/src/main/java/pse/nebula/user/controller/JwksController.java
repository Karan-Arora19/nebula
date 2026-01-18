package pse.nebula.user.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * JWKS (JSON Web Key Set) Controller
 *
 * Note: Since we're using HMAC SHA256 for JWT signing, we don't expose public keys.
 * This endpoint returns an empty JWKS for compatibility with the gateway.
 */
@RestController
@RequestMapping("/api/users/.well-known")
public class JwksController {

    @GetMapping(value = "/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getJwks() {
        // For HMAC SHA256, we don't expose public keys via JWKS
        // Return empty JWKS for compatibility
        Map<String, Object> jwks = new HashMap<>();
        jwks.put("keys", new Object[]{});
        return jwks;
    }
}
