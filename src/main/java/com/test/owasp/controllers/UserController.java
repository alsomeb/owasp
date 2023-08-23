package com.test.owasp.controllers;

import com.test.owasp.models.AppUser;
import com.test.owasp.models.AppUserDTO;
import com.test.owasp.models.CreateNewUserResponseDTO;
import com.test.owasp.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("user")
public class UserController {

    private UserService userService;

    @GetMapping("/get")
    public List<AppUser> getUserByUsername(@RequestParam String username) {
        return userService.findUserByUsername(username);
    }

    @PostMapping("/create")
    public ResponseEntity<CreateNewUserResponseDTO> createNewUser(@RequestBody AppUserDTO appUserDTO) {
        var result = userService.createNewUser(appUserDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

}
