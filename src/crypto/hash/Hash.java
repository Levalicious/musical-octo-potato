package crypto.hash;

import crypto.cryptohash.Keccak256;
import crypto.cryptohash.Keccak512;
import org.bouncycastle.jcajce.provider.digest.Blake2b;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;

import static util.Hex.getHex;

public class Hash {
    static MessageDigest md;

    public static class byteHash {
        public static byte[] sha256(byte[] in) {
            md = new Keccak256();
            byte[] out = md.digest(in);
            return out;
        }

        public static byte[] sha512(byte[] in) {
            md = new Keccak512();
            byte[] out = md.digest(in);
            return out;
        }

        public static byte[] blake256(byte[] in) {
            md = new Blake2b.Blake2b256();
            byte[] out = md.digest(in);
            return out;
        }

        public static byte[] blake512(byte[] in) {
            md = new Blake2b.Blake2b256();
            byte[] out = md.digest(in);
            return out;
        }

    }
    public static String sha256(String in) {
        md = new Keccak256();
        byte[] out = new byte[32];
        try{
            out= md.digest(in.getBytes("UTF-8"));
        }catch(UnsupportedEncodingException e) {
            System.out.println("Your computer does not support UTF-8. Exiting.");
            System.exit(0);
        }
        return getHex(out);
    }

    public static String sha512(String in) {
        md = new Keccak512();
        byte[] out = new byte[32];
        try{
            out= md.digest(in.getBytes("UTF-8"));
        }catch(UnsupportedEncodingException e) {
            System.out.println("Your computer does not support UTF-8. Exiting.");
            System.exit(0);
        }
        return getHex(out);
    }

    public static String blake256(String in) {
        MessageDigest md = new Blake2b.Blake2b256();
        byte[] out = new byte[32];
        try {
            out = md.digest(in.getBytes("UTF-8"));
        }catch(UnsupportedEncodingException e) {
            System.out.println("Your computer does not support UTF-8. Exiting.");
            System.exit(0);
        }
        return getHex(out);
    }

    public static String blake512(String in) {
        MessageDigest md = new Blake2b.Blake2b512();
        byte[] out = new byte[64];
        try {
            out = md.digest(in.getBytes("UTF-8"));
        }catch(UnsupportedEncodingException e) {
            System.out.println("Your computer does not support UTF-8. Exiting.");
            System.exit(0);
        }
        return getHex(out);
    }

    public static String walletHash(String in) {
        return blake256(in);
    }

    public static String getMerkleRoot(ArrayList temp) {
        int count = temp.size();
        ArrayList<String> prevLayer = new ArrayList<>();

        for(Object x : temp) {
            prevLayer.add(blake256(x.toString()));
        }

        ArrayList<String> treeLayer = prevLayer;

        while(count > 1) {
            treeLayer = new ArrayList<String>();

            for(int i = 1; i < prevLayer.size(); i++) {
                treeLayer.add(blake256((prevLayer.get(i - 1) + prevLayer.get(i))));
            }

            count = treeLayer.size();
            prevLayer = treeLayer;
        }

        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

    private static String toHex(String in) {
        try {
            return getHex(in.getBytes("UTF-8"));
        }catch(UnsupportedEncodingException e) {
            System.out.println("Your computer does not support UTF-8. Exiting.");
            System.exit(0);
        }
        return null;
    }

    public static byte[] toBytes(String in) {
        byte[] out = new byte[1];
        try{
            out = in.getBytes("UTF-8");
        }catch(UnsupportedEncodingException e) {
            System.out.println("Your computer does not support the required encoding format.");
            System.exit(0);
        }

        return out;
    }
}
