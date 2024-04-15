package com.dberardi.gradesubmission.service;

import com.dberardi.gradesubmission.exception.EntityNotFoundException;
import com.dberardi.gradesubmission.model.User;
import com.dberardi.gradesubmission.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void getUser() {
        when(userRepository.findById(0L)).thenReturn(
                Optional.of(new User("User", "pass123"))
        );

        User result = userService.getUser(0L);

        assertEquals("User", result.getUsername());
        assertEquals("pass123", result.getPassword());
    }

    @Test
    public void getUserByUsername() {
        when(userRepository.findByUsername("User")).thenReturn(
                Optional.of(new User("User", "pass123"))
        );

        User result = userService.getUser("User");

        assertEquals("User", result.getUsername());
        assertEquals("pass123", result.getPassword());
    }

    @Test
    public void saveUser() {
        User user = new User("User", "pass123");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        when(bCryptPasswordEncoder.encode(anyString()))
                .thenReturn("encryptedPassword");

        User result = userService.saveUser(user);

        verify(userRepository, times(1)).save(any(User.class));
        assertEquals("User", result.getUsername());
    }

    @Test
    public void getUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(999999L));
    }
}
