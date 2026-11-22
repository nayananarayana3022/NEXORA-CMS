
package com.nexora.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import com.nexora.entity.User;
import com.nexora.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void testUserRegistration() {

        User user = new User();
        user.setName("Demo User");
        user.setEmail("demo@nexora.com");
        user.setPassword("Demo@12345");

        when(userRepository.existsByEmail("demo@nexora.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Demo@12345"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.registerUser(user);

        assertEquals("Demo User", registeredUser.getName());
        assertEquals("demo@nexora.com", registeredUser.getEmail());
        assertEquals("AUTHOR", registeredUser.getRole());
        assertEquals("encodedPassword", registeredUser.getPassword());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDuplicateEmailRegistration() {

        User user = new User();
        user.setName("Demo User");
        user.setEmail("demo@nexora.com");
        user.setPassword("Demo@12345");

        when(userRepository.existsByEmail("demo@nexora.com"))
                .thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(user)
        );

        assertEquals("Email already registered!", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void testSuccessfulLogin() {

        User user = new User();
        user.setEmail("demo@nexora.com");
        user.setPassword("encodedPassword");
        user.setRole("AUTHOR");

        when(userRepository.findByEmail("demo@nexora.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("Demo@12345", "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken("demo@nexora.com", "AUTHOR"))
                .thenReturn("sample.jwt.token");

        String token = userService.loginUser(
                "demo@nexora.com", "Demo@12345");

        assertEquals("sample.jwt.token", token);
    }

    @Test
    void testInvalidPassword() {

        User user = new User();
        user.setEmail("demo@nexora.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("demo@nexora.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("WrongPassword", "encodedPassword"))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.loginUser(
                        "demo@nexora.com", "WrongPassword")
        );

        assertEquals("Invalid password!", exception.getMessage());
    }
}
