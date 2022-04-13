package com.springboot.apispringboot.web.security.filter;

import com.springboot.apispringboot.domain.service.UserDetailsService;
import com.springboot.apispringboot.web.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilterRequest extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {

            String jwt = authorizationHeader.substring(7);

            String username = jwtUtil.getBodyUserName(jwt);

            if ( username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if ( jwtUtil.validateToken(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    //Verificacion de datos de conexion, horario, fecha, navegador por el cual se acceso
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    //Almacenar en cache la autenticacion para que no vuelva a untenticar al usuario;
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }

            }
        }

        filterChain.doFilter(request, response);
    }
}
