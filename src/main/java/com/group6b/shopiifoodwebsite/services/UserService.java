package com.group6b.shopiifoodwebsite.services;

import com.group6b.shopiifoodwebsite.constants.Provider;
import com.group6b.shopiifoodwebsite.constants.Role;
import com.group6b.shopiifoodwebsite.entities.Restaurant;
import com.group6b.shopiifoodwebsite.entities.User;
import com.group6b.shopiifoodwebsite.repositories.RestaurantRepository;
import com.group6b.shopiifoodwebsite.repositories.RoleRepository;
import com.group6b.shopiifoodwebsite.repositories.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
    public User createRestaurantAccount(User user, Restaurant restaurant) {
        user.setPassword(new BCryptPasswordEncoder()
                .encode(user.getPassword()));
        user.getRoles().add(roleRepository.findRoleById(Role.SELLER.value));
        userRepository.save(user);

        restaurant.setUser(user);
        restaurantRepository.save(restaurant);

        return user;
    }
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
    public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder()
                .encode(user.getPassword()));

        userRepository.save(user);
        setDefaultRole(user.getUsername());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).getRoles()
                .add(roleRepository.findRoleById(Role.USER.value));
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userRepository.findById(id));
    }

    public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username);
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    public void saveOauthUser(String email, @NotNull String username) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            return; // User already exists
        }
        var user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(new BCryptPasswordEncoder().encode(username));
        user.setProvider(Provider.GOOGLE.value);
        user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
        userRepository.save(user);

    }
    public Restaurant getRestaurantByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return user.getRestaurant(); // Giả sử rằng mỗi User có thuộc tính Restaurant
    }
}