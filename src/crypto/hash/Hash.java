package crypto.hash;

import org.bouncycastle.jcajce.provider.digest.Blake2b;
import static resources.Config.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Set;

import static crypto.hash.MurmurHash.hash64;
import static util.Hex.getHex;

public class Hash {
    public static void main(String[] args) {
        System.out.println(sha256("Herro"));
    }
    public static String sha256(String in) {
        return keccak.getHash(toHex(in), Parameters.KECCAK_256);
    }

    public static String sha512(String in) {
        return keccak.getHash(toHex(in), Parameters.KECCAK_512);
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

    public static long murmur64(String in) {
        try {
            return hash64(in.getBytes("UTF-8"));
        }catch(UnsupportedEncodingException e) {
            System.out.println("Your computer does not support UTF-8. Exiting.");
            System.exit(0);
        }
        return 0;
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
}
