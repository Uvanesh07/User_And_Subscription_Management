package com.userms.DTO;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class LoginDTO {
    private Long loginId;

    @NotNull(message = "Username is required")
    @NotEmpty(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid username format")
    private String username;

    @NotEmpty(message = "Password is required")
    private String password;

    private boolean accountActive;

    private String accountInactiveReason;

    private LocalDateTime loginTime;

    private LocalDateTime logoutTime;

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public @NotNull(message = "Username is required") @NotEmpty(message = "Username is required") @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid username format") String getUsername() {
        return username;
    }

    public void setUsername(@NotNull(message = "Username is required") @NotEmpty(message = "Username is required") @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid username format") String username) {
        this.username = username;
    }

    public @NotEmpty(message = "Password is required") String getPassword() {
        return password;
    }

    public void setPassword(@NotEmpty(message = "Password is required") String password) {
        this.password = password;
    }

    public boolean isAccountActive() {
        return accountActive;
    }

    public void setAccountActive(boolean accountActive) {
        this.accountActive = accountActive;
    }

    public String getAccountInactiveReason() {
        return accountInactiveReason;
    }

    public void setAccountInactiveReason(String accountInactiveReason) {
        this.accountInactiveReason = accountInactiveReason;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }
}
