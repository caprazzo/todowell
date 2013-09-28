package net.caprazzi.giddone;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RandomStringGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String randomString()
    {
        return new BigInteger(130, random).toString(32);
    }
}
