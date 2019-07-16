package com.rest.example.h2crud.controller;

import com.rest.example.h2crud.exception.UserNotFoundException;
import com.rest.example.h2crud.model.Users;
import com.rest.example.h2crud.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    public static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Users> retrieveAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public Users retrieveUser(@PathVariable long id) {
        Optional<Users> user = userRepository.findById(id);

        if (!user.isPresent()) throw new UserNotFoundException("id - " + id);

        return user.get();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userRepository.deleteById(id);
    }

    @PostMapping()
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        Users savedUser = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();
        log.info("location is " + location.toString());

        return ResponseEntity.created(location).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@RequestBody Users user, @PathVariable long id) {

        Optional<Users> userOptional = userRepository.findById(id);

        if (!userOptional.isPresent()) return ResponseEntity.notFound().build();

        user.setId(id);
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }

}
