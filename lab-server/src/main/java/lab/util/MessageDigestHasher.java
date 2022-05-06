package lab.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MessageDigestHasher {

    private static final Logger LOGGER = LogManager.getLogger(MessageDigestHasher.class);

    private MessageDigestHasher() {
        throw new UnsupportedOperationException();
    }

    public static byte[] getHashFromBytes(byte[] data) {
        try {
            MessageDigest hashFunction = MessageDigest.getInstance("MD2");
            return hashFunction.digest(data);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error occurred whlile trying to get hash, {}", e.getMessage());
            throw new UnsupportedOperationException(e);
        }
    }
}
