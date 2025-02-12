package com.cars24.rateLimiter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.args.ExpiryOption;

@Service
@Slf4j
@RequiredArgsConstructor
public class FixedWindow {

    private final Jedis jedis;

    public boolean isAllowed(String clientId, int limit, int windowSize){

        String key = "rate_limit_" + clientId;

        String value = jedis.get(key);
        int numberOfRequests = (value != null) ? Integer.parseInt(value) : 0;

        if(numberOfRequests < limit){
            Transaction transaction = jedis.multi();
            transaction.incr(key);
            transaction.expire(key, windowSize, ExpiryOption.NX); // NX flag sets the expiration only if it is not set earlier
            // which means the expiration won't be set in the subsequent requests
            transaction.exec();
            return true;
        }

        return false;
    }

}
