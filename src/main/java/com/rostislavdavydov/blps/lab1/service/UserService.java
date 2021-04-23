package com.rostislavdavydov.blps.lab1.service;

import com.rostislavdavydov.blps.lab1.repository.RoleRepository;
import com.rostislavdavydov.blps.lab1.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rostislavdavydov.blps.lab1.model.User;

import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User fetchUserById(Long id) {
        return userRepository.findById(id).isPresent() ? userRepository.findById(id).get() : null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userRepository.findByUsername(username).isPresent())
            return userRepository.findByUsername(username).get();
        else
            throw new UsernameNotFoundException("User not found");
    }

    public User getUserByUsername(String username) {
        if (userRepository.findByUsername(username).isPresent())
            return userRepository.findByUsername(username).get();
        else
            throw new UsernameNotFoundException("User not found");
    }


    public boolean saveUser(User user, String role) {
        User userFromDB = userRepository.findByUsername(user.getUsername()).isPresent() ? userRepository.findByUsername(user.getUsername()).get() : null;

        if (userFromDB != null) {
            return false;
        }
        log.info(role);
        log.info(String.valueOf(roleRepository.findByName(role).isPresent()));
        log.info(String.valueOf(Collections.singleton(roleRepository.findByName(role).get())));

        user.setRoles(roleRepository.findByName(role).isPresent() ? Collections.singleton(roleRepository.findByName(role).get()) : null);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        log.info(String.valueOf(user.getPassword()));
        userRepository.save(user);
        return true;
    }

}