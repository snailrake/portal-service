package ru.intership.portalservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.intership.portalservice.config.context.UserContext;
import ru.intership.portalservice.dto.ResetPasswordDto;
import ru.intership.portalservice.dto.UserDto;
import ru.intership.portalservice.dto.UserShortDto;
import ru.intership.portalservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserContext userContext;

    @PostMapping("/owner")
    public String registerCompanyOwner(@RequestBody UserDto userDto) {
        return userService.registerCompanyOwner(userDto);
    }

    @PostMapping("/member")
    public String registerCompanyMember(@RequestParam String companyInn,
                                        @RequestParam String role,
                                        @RequestBody @Valid UserDto userDto) {
        return userService.registerCompanyMember(companyInn, userDto, role, userContext.getUserRoles());
    }

    @PatchMapping("/password")
    public void resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        userService.resetPassword(userContext.getUserName(), resetPasswordDto);
    }

    @PatchMapping("/password/regenerated")
    public void resetPassword() {
        userService.setAndSendPassword(userContext.getUserName());
    }

    @PutMapping
    public UserDto updateUserInfo(@RequestBody @Valid UserDto userDto) {
        return userService.updateUserInfo(userContext.getUserName(), userDto);
    }

    @PostMapping("/driver")
    public String registerDriver(@RequestParam String companyInn,
                                 @RequestBody @Valid UserDto userDto) {
        return userService.registerDriver(companyInn, userDto, userContext.getUserRoles());
    }

    @GetMapping("/company/{companyInn}")
    public List<UserShortDto> getUsersInCompany(@PathVariable String companyInn) {
        return userService.getUsersInCompany(companyInn, userContext.getUserRoles());
    }
}
