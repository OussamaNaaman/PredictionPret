package com.example.loanpredictor.controller;

import com.example.loanpredictor.entity.User;
import com.example.loanpredictor.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class); // Logger already here

    public AuthController(PasswordEncoder passwordEncoder, CustomUserDetailsService customUserDetailsService,
                          AuthenticationManager authenticationManager) {
        this.passwordEncoder= passwordEncoder;
        this.customUserDetailsService= customUserDetailsService;
        this.authenticationManager = authenticationManager;
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register") // **ADDED GET MAPPING FOR /register**
    public String registerForm() {
        log.info("GET /register - displaying registration form"); // Added log for accessing registration form
        return "register"; // Return the register.html template
    }


    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, Model model) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        try{
            customUserDetailsService.saveUser(newUser);
            log.info("User registered successfully: {}", username); // ADDED SUCCESS LOG
            return "redirect:/login";
        }
        catch (Exception e){
            log.error("Error during registration for username: {}", username, e); // **MODIFIED ERROR LOG - added exception details!**
            model.addAttribute("error", "Registration failed. Please try again. " + e.getMessage()); // **MODIFIED ERROR MESSAGE - Include exception message in frontend!**
            return "login";
        }

    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if (authentication.isAuthenticated()){
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return "redirect:/";
            }
            else{
                log.warn("Authentication failed for username: {}", username);
                model.addAttribute("error", "Login failed. Please check your credentials.");
                return "login";
            }

        } catch (Exception e) {
            log.error("Exception during authentication", e);
            model.addAttribute("error", "Login failed. Please check your credentials.");
            return "login";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }
}