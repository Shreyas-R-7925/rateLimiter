package com.cars24.rateLimiter.controller;

import com.cars24.rateLimiter.service.FixedWindow;
import com.cars24.rateLimiter.service.LeakyBucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Slf4j
public class RateLimitingController {

    private final FixedWindow fixedWindow;

    private final LeakyBucket leakyBucket;

    @GetMapping("/fixedWindow")
    public ResponseEntity<?> getMessageV1(HttpServletRequest request ){

        String userIpAddress = request.getRemoteAddr();

        if(fixedWindow.isAllowed(userIpAddress, 3, 10)){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.status(429).body("Too many requests");
    }

    @GetMapping("/leakyBucket")
    public ResponseEntity<?> getMessageV2(HttpServletRequest request){

        String userIpAddress = request.getRemoteAddr();
        if(leakyBucket.isAllowed(userIpAddress, 3, 0.2)){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.status(429).body("Too many requests");
    }

}
