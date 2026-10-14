package com.homycarev1.ecommerce.controller;

import com.homycarev1.ecommerce.dao.AppUserRepository;
import com.homycarev1.ecommerce.entity.AppUser;
import com.homycarev1.ecommerce.entity.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AccountController(AppUserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/account/me")
    public AccountView currentAccount(Authentication authentication) {
        AppUser user = users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return AccountView.from(user);
    }

    @PostMapping("/account/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountView signUp(@Valid @RequestBody SignUpRequest request) {
        if (users.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }

        AppUser user = new AppUser();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        return AccountView.from(users.save(user));
    }

    @GetMapping("/admin/users")
    public List<AccountView> listAccounts() {
        return users.findAll().stream().map(AccountView::from).toList();
    }

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountView createAccount(@Valid @RequestBody CreateAccountRequest request) {
        if (users.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }

        AppUser user = new AppUser();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setActive(true);
        return AccountView.from(users.save(user));
    }

    public record CreateAccountRequest(
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Email @Size(max = 190) String email,
            @NotBlank @Size(min = 10, max = 72) String password,
            @NotNull UserRole role) {
    }

    public record SignUpRequest(
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Email @Size(max = 190) String email,
            @NotBlank @Size(min = 10, max = 72) String password) {
    }

    public record AccountView(Long id, String fullName, String email, UserRole role, boolean active) {
        static AccountView from(AppUser user) {
            return new AccountView(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.isActive());
        }
    }
}
