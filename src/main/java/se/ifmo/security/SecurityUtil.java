package se.ifmo.security;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.util.Base64;

@ApplicationScoped
public class SecurityUtil {
    public String generateString() {
        SecureRandom random = new SecureRandom();
        byte[] id = new byte[32];
        random.nextBytes(id);

        return Base64.getEncoder().encodeToString(id);
    }

    public String hashStr(String str){
        return DigestUtils.sha256Hex(str);
    }
}
