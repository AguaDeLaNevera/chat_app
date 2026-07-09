package com.example.chat.application;

import com.example.chat.domain.User;
import com.example.chat.domain.UserRepository;
import com.example.chat.infrastructure.exceptions.UserAlreadyExistsException;
import com.example.chat.infrastructure.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserUseCase userUseCase;

    @Test
    void createUserSavesUser() {
        when(userRepository.findByUsername("riki"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234"))
                .thenReturn("encodedPassword");

        User savedUser = new User(1, "riki", "encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userUseCase.createUser("riki", "1234");

        assertEquals(1, result.id());
        assertEquals("riki", result.username());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserThrowsUserAlreadyExsistsException() {
        User existingUser = new User(1, "riki", "encodedPassword");

        when(userRepository.findByUsername("riki"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userUseCase.createUser("riki", "1234")
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void findUserByIdReturnsUser() {
        User user = new User(1, "riki", "encodedPassword");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userUseCase.findUserById(1);

        assertEquals(1, result.id());
        assertEquals("riki", result.username());
        verify(userRepository).findById(1);
    }

    @Test
    void findUserByIdThrowsUserNotFoundException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userUseCase.findUserById(1)
        );
        verify(userRepository).findById(1);
    }

    @Test
    void findAllReturnsListOfUsers() {
        List<User> users = List.of(
                new User(1, "riki", "pass1"),
                new User(2, "ana", "pass2")
        );
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userUseCase.findAll();

        assertEquals(2, result.size());
        assertEquals("riki", result.get(0).username());
        assertEquals("ana", result.get(1).username());
        verify(userRepository).findAll();
    }

    @Test
    void findByUsernameReturnsUser() {
        User user = new User(1, "riki", "encodedPassword");
        when(userRepository.findByUsername("riki")).thenReturn(Optional.of(user));

        User result = userUseCase.findByUsername("riki");

        assertEquals(1, result.id());
        assertEquals("riki", result.username());
        verify(userRepository).findByUsername("riki");
    }

    @Test
    void findByUsernameThrowsUserNotFoundException() {
        when(userRepository.findByUsername("riki")).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userUseCase.findByUsername("riki")
        );
        verify(userRepository).findByUsername("riki");
    }

    @Test
    void getUsernameWithIdReturnsUsername() {
        User user = new User(1, "riki", "encodedPassword");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        String result = userUseCase.getUsernameWithId(1);

        assertEquals("riki", result);
        verify(userRepository).findById(1);
    }

    @Test
    void getUsernameWithIdThrowsUserNotFoundException(){
        when(userRepository.findById(1))
                .thenReturn(Optional.empty());
        assertThrows(
                UserNotFoundException.class,
                () -> userUseCase.getUsernameWithId(1)
        );
        verify(userRepository).findById(1);
    }

}
