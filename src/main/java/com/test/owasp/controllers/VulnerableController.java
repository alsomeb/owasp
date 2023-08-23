package com.test.owasp.controllers;

import com.test.owasp.models.AppUser;
import com.test.owasp.models.AppUserDTO;
import com.test.owasp.models.CreateNewUserResponseDTO;
import com.test.owasp.services.VulnerableService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("vulnerable/user")
public class VulnerableController {
    private VulnerableService vulnerableService;
    @GetMapping
    public List<AppUser> getUserByUsername(@RequestParam String username) {
        return vulnerableService.findUserByUsername(username);
    }

    @PostMapping
    public ResponseEntity<CreateNewUserResponseDTO> createNewUser(@RequestBody AppUserDTO appUserDTO) {
        var result = vulnerableService.createNewUser(appUserDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
