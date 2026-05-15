package com.web3.learning.utils;

import org.bouncycastle.jcajce.provider.digest.Keccak;

import java.nio.charset.StandardCharsets;

public class KeccakHashUtils {

    public static String keccak256(byte[] inputBytes) {
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        byte[] hashBytes = digest256.digest(inputBytes);
        return bytesToHex(hashBytes);
    }

    public static String keccak256(String input) {
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        byte[] hashBytes = digest256.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashBytes);
    }

    public static String bytesToHex(byte[] inputBytes) {
        StringBuilder sb = new StringBuilder();
        for(byte b: inputBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
