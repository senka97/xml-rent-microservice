package com.team19.rentmicroservice.controller;

import com.team19.rentmicroservice.security.CustomPrincipal;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestController {

    @GetMapping("/helloAdmin")
    @PreAuthorize("hasAuthority('helloAdmin')")
    public String helloAdmin()  {
        //Dobavljanje podataka o ulogovanom korisniku iz SecurityContextHolder-a
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        System.out.println("Kontroler:");
        System.out.println(cp.getPermissions());
        System.out.println(cp.getUserID());
        System.out.println(cp.getToken());
        return "Hello Admin";
    }
}
