package com.cars24.rateLimiter.controller;

import com.cars24.rateLimiter.service.FixedWindow;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class RateLimitingController {

    private final FixedWindow fixedWindow;
    @GetMapping("/fixedWindow")
    public ResponseEntity<?> getMessageV1(HttpServletRequest request ){

        String userIpAddress = request.getRemoteAddr();
        if(fixedWindow.isAllowed(userIpAddress, 3, 10)){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.status(429).body("Too many requests");
    }

}
