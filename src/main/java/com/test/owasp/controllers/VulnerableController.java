package com.test.owasp.controllers;

import com.test.owasp.models.User;
import com.test.owasp.services.VurnableService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("vulnerable")
public class VulnerableController {
    private VurnableService vulnerableService;
    @GetMapping("user")
    public List<User> getUserByUsername(@RequestParam String username) {
        return vulnerableService.findUserByUsername(username);
    }
}
