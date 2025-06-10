package org.example.clothesclassifier.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.example.clothesclassifier.dtos.TokenDTO;
import org.example.clothesclassifier.dtos.UserDTO;
import org.example.clothesclassifier.services.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenDTO> register(@RequestBody UserDTO userDto, HttpServletResponse response) {
        TokenDTO tokenDTO = authenticationService.register(userDto);


        return ResponseEntity.ok(tokenDTO);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<TokenDTO> authenticate(@RequestParam String login, @RequestParam String password, HttpServletResponse response) {

        TokenDTO tokenDTO = authenticationService.authenticate(login, password);

        return ResponseEntity.ok(tokenDTO);
    }
}
