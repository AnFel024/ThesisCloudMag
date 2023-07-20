package com.antithesis.cloudmag.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/list")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ListsController {


    @GetMapping("/hello_world")
    public ResponseEntity<?> helloWorld() {
        return ResponseEntity.ok().body("Hola mundo!");
    }
}
