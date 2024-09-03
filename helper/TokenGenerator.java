package com.eCommerce.helper;

import java.util.UUID;

public class TokenGenerator {

    public String tokengenerator() {

        String token = UUID.randomUUID().toString();
        return token;

    }

}
