package pse.nebula.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtValidatorTest {

    private JwtValidator jwtValidator;
    private JwtParser jwtParserMock;

    @BeforeEach
    void setUp() {
        jwtParserMock = mock(JwtParser.class);
        // Use the new constructor to inject mock
        jwtValidator = new JwtValidator(jwtParserMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testValidateToken() {
        Claims claimsMock = mock(Claims.class);
        Jws<Claims> jwsMock = (Jws<Claims>) mock(Jws.class);

        // Token should be considered valid -> expiration in the future
        when(claimsMock.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 60_000));
        when(jwsMock.getBody()).thenReturn(claimsMock);
        when(jwtParserMock.parseClaimsJws(anyString())).thenReturn(jwsMock);

        Claims claims = jwtValidator.validateToken("testToken");

        assertNotNull(claims);
    }

    @Test
    void testValidateTokenExpired() {
        Claims claimsMock = mock(Claims.class);
        Jws<Claims> jwsMock = (Jws<Claims>) mock(Jws.class);

        // Token expired -> expiration in the past
        when(claimsMock.getExpiration()).thenReturn(new Date(System.currentTimeMillis() - 60_000));
        when(jwsMock.getBody()).thenReturn(claimsMock);
        when(jwtParserMock.parseClaimsJws(anyString())).thenReturn(jwsMock);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> jwtValidator.validateToken("expiredToken"));

        assertEquals("Token expired", exception.getMessage());
    }

    @Test
    void testGetUserId() {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("user-id");

        String userId = jwtValidator.getUserId(claims);
        assertEquals("user-id", userId);
    }

    @Test
    void testGetEmail() {
        Claims claims = mock(Claims.class);
        when(claims.get("email", String.class)).thenReturn("user@example.com");

        String email = jwtValidator.getEmail(claims);
        assertEquals("user@example.com", email);
    }

    @Test
    void testGetRoles() {
        Claims claims = mock(Claims.class);
        when(claims.get("roles")).thenReturn("ROLE_USER");

        String roles = jwtValidator.getRoles(claims);
        assertEquals("ROLE_USER", roles);
    }
}
