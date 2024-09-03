package com.eCommerce.helper;

import java.util.Random;

public class OtpGenerator {

        private static final int OTP_LENGTH = 6;

        public static String generateOTP() {
            Random random = new Random();
            StringBuilder otp = new StringBuilder();
            for (int i = 0; i < OTP_LENGTH; i++) {
                otp.append(random.nextInt(10));
            }
            return otp.toString();
        }

}