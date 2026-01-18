package pse.nebula.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pse.nebula.user.dto.AuthenticationResult;
import pse.nebula.user.model.Role;
import pse.nebula.user.model.User;
import pse.nebula.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private BCryptPasswordEncoder realPasswordEncoder;

    @BeforeEach
    void setUp() {
        realPasswordEncoder = new BCryptPasswordEncoder();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole(Role.USER);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void getUserByEmail_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void createUser_ShouldHashPasswordAndSaveUser() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("plainPassword");
        newUser.setRole(Role.USER);

        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithValidUser_ShouldUpdateSuccessfully() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("existing");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("oldHashedPassword");
        existingUser.setRole(Role.USER);

        User updateUser = new User();
        updateUser.setId(1L);
        updateUser.setUsername("updated");
        updateUser.setEmail("updated@example.com");
        updateUser.setPassword("newPassword");
        updateUser.setRole(Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(updateUser);

        // Act
        User result = userService.updateUser(updateUser);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        User updateUser = new User();
        updateUser.setId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(updateUser));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithNullUser_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(null));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithNullPassword_ShouldKeepExistingPassword() {
        // Arrange
        String encodedPassword = "existingHashedPassword";
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setPassword(encodedPassword);

        User updateUser = new User();
        updateUser.setId(1L);
        updateUser.setPassword(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateUser(updateUser);

        // Assert
        assertEquals(encodedPassword, result.getPassword());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deleteUser_ShouldCallRepository() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnTokenAndUser() {
        // Arrange
        String encodedPassword = "hashedPassword123";
        testUser.setPassword(encodedPassword);
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", encodedPassword)).thenReturn(true);
        when(jwtUtil.generateToken("1", "test@example.com", "USER")).thenReturn("mock-jwt-token");

        // Act
        AuthenticationResult result = userService.authenticateUser("test@example.com", "password123");

        // Assert
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertNotNull(result.getUser());
        assertEquals("mock-jwt-token", result.getToken());
        assertEquals(testUser.getId(), result.getUser().getId());
        assertEquals(testUser.getEmail(), result.getUser().getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password123", encodedPassword);
        verify(jwtUtil, times(1)).generateToken("1", "test@example.com", "USER");
    }

    @Test
    void authenticateUser_WithInvalidEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.authenticateUser("wrong@example.com", "password123"));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("wrong@example.com");
    }

    @Test
    void authenticateUser_WithInvalidPassword_ShouldThrowException() {
        // Arrange
        String encodedPassword = "hashedCorrectPassword";
        testUser.setPassword(encodedPassword);
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", encodedPassword)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.authenticateUser("test@example.com", "wrongPassword"));
        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("wrongPassword", encodedPassword);
    }
}
