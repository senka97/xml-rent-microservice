package com.team19.rentmicroservice.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException, IOException, ServletException, ServletException {
        System.out.println("Uslo u filter");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String permissions = httpRequest.getHeader("permissions");
        String userID = httpRequest.getHeader("userID");
        String token = httpRequest.getHeader("Authorization");
        System.out.println(permissions);
        System.out.println(userID);
        System.out.println(token);

        //U slucaju da je neko prosledio zahtev sa permisijama ali bez tokena
        //sto znaci da nije ulogovan, pa samim tim ne bi trebao da ima ni permisije
        //a putanja nije permitAll, pa se pusta dalje da ga hasAuthority odbije
        if (permissions != null && token != null) {
            Set<SimpleGrantedAuthority> authorities = new HashSet<>();

            String[] perms = permissions.split("\\|");
            for (String perm : perms) {
                System.out.println(perm);
                authorities.add(new SimpleGrantedAuthority(perm));
            }

            CustomPrincipal cp = new CustomPrincipal(permissions,userID,token);
            //prvi parametar je Object Principal, sadrzi podatke o ulogovanom
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(cp, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}
