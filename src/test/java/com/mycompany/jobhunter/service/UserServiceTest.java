package com.mycompany.jobhunter.service;

import com.mycompany.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.mycompany.jobhunter.domain.dto.response.user.ResUpdateUserDTO;
import com.mycompany.jobhunter.domain.dto.response.user.ResUserDTO;
import com.mycompany.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.mycompany.jobhunter.domain.entity.User;
import com.mycompany.jobhunter.domain.entity.enumeration.GenderEnum;
import com.mycompany.jobhunter.repository.UserRepository;
import com.mycompany.jobhunter.service.implement.UserServiceImpl;
import com.mycompany.jobhunter.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("John Doe");
        user.setAge(30);
        user.setGender(GenderEnum.MALE);
        user.setAddress("123 Main St");
        user.setCreatedAt(Instant.now());
    }

    @Test
    void isEmailExisted_ShouldReturnTrue_WhenEmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertTrue(userService.isEmailExisted("test@example.com"));
        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }

    @Test
    void createUser_ShouldReturnResCreateUserDTO() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        ResCreateUserDTO result = userService.createUser(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void handleGetUserByEmail_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        User foundUser = userService.handleGetUserByEmail("test@example.com");

        assertNotNull(foundUser);
        assertEquals("John Doe", foundUser.getName());
    }

    @Test
    void updateRefreshToken_ShouldUpdateToken() {
        // Arrange: Create a user with initial values
        User user = new User();
        user.setEmail("test@example.com");
        user.setRefreshToken("oldToken");

        // Mock: Find user by email and return the user object
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Call the method under test
        userService.updateRefreshToken("newToken", "test@example.com");

        // Assert: Verify the user's refresh token is updated
        assertEquals("newToken", user.getRefreshToken());

        // Verify: Ensure save was called once with the updated user
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void fetchUserById_ShouldReturnCorrectUser_WhenUserExists() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("User Two");

        // Mock repository to return user2 when the specific ID is provided
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(user2));

        // Call the service method with the desired ID
        User foundUser = userService.fetchUserById(2L);

        // Verify that the correct user is returned
        assertNotNull(foundUser);
        assertEquals(2L, foundUser.getId());
        assertEquals("User Two", foundUser.getName());

        // Ensure the repository was called with the correct ID
        verify(userRepository).findById(eq(2L));
    }


    @Test
    void handleDeleteUserById_ShouldDeleteUser() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.handleDeleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void fetchAllUsers_ShouldReturnPaginatedResult() {
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        ResultPaginationDTO result = userService.fetchAllUsers(null, Pageable.unpaged());

        assertNotNull(result);
    }

    @Test
    void handleUpdateUser_ShouldUpdateAndReturnUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");

        User result = userService.handleUpdateUser(updatedUser);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
    }

    @Test
    void getOrCreateUser_ShouldCreateNewUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        Map<String, Object> userInfo = Map.of("name", "John Doe", "gender", "male");

        User result = userService.getOrCreateUser("test@example.com", userInfo);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void getOrCreateUser_ShouldReturnExistingUser_WhenUserExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        User result = userService.getOrCreateUser("test@example.com", Map.of());

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void getOrCreateUser_ShouldHandleRaceCondition() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        User result = userService.getOrCreateUser("test@example.com", Map.of("name", "John Doe", "gender", "male"));

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void getOrCreateUser_ShouldThrowRuntimeException_OnUnexpectedError() {
        when(userRepository.findByEmail(anyString())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () ->
                userService.getOrCreateUser("test@example.com", Map.of("name", "John Doe")));
    }
}