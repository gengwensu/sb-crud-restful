package com.gsucode.example.h2crud.controller;

import com.gsucode.example.h2crud.repository.UserRepository;
import com.gsucode.example.h2crud.exception.UserNotFoundException;
import com.gsucode.example.h2crud.model.Users;
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

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @CrossOrigin
    @GetMapping
    public List<Users> retrieveAllUsers() {
        return userRepository.findAll();
    }

    @CrossOrigin
    @GetMapping("/{id}")
    public Users retrieveUser(@PathVariable long id) {
        Optional<Users> user = userRepository.findById(id);

        if (!user.isPresent()) throw new UserNotFoundException("id - " + id);

        return user.get();
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userRepository.deleteById(id);
    }

    @CrossOrigin
    @PostMapping()
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        Users savedUser = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();
        log.info("location is " + location.toString());

        return ResponseEntity.created(location).body(savedUser);
    }

    @CrossOrigin
    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@RequestBody Users user, @PathVariable long id) {

        Optional<Users> userOptional = userRepository.findById(id);

        if (!userOptional.isPresent()) return ResponseEntity.notFound().build();

        user.setId(id);
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }

}
