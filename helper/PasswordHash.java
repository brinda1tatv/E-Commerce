package com.eCommerce.helper;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHash {

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String plainPassword, String salt) {
        return BCrypt.hashpw(plainPassword + salt, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String salt, String hashedPassword) {
        return BCrypt.checkpw(plainPassword + salt, hashedPassword);
    }

}
