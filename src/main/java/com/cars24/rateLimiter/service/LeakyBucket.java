package com.cars24.rateLimiter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeakyBucket {

    private final Jedis jedis;

    public boolean isAllowed(String clientId, int bucketCapacity, double leakRate){

        String key = "rate_limit_" + clientId;

        long currentTime = System.currentTimeMillis();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        long leakTimePerToken = (long) (1000.0/leakRate);  // if 5 reqs/sec then 200 ms for every request
        // now i have given 0.2 so to procecss one token it takes 5 secs

        List<String> timeStamps = jedis.lrange(key, 0, -1);
        for(String timeStamp : timeStamps){
            if(currentTime - Long.parseLong(timeStamp) >= leakTimePerToken){
                log.info("REMOVED at {}", dtf.format(now));
                jedis.lpop(key);
            }
            else{
                break;
            }
        }

        if(jedis.llen(key) < bucketCapacity){
            jedis.rpush(key, String.valueOf(currentTime));
            log.info("SUCCESS, key {} {}", key, dtf.format(now));
            return true;
        }

        log.info("FAILURE, key {} {}", key, dtf.format(now));
        return false;
    }
}
