package util.wallet;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;

import static util.Hex.getHex;
import static resources.Config.*;


public class Private {
    private static X9ECParameters x9Params = CustomNamedCurves.getByName(curve);

    public static PrivateKey generateNewPrivateKey() {
        SecureRandom random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        }
        catch (Exception e) {
            System.out.println("The random number generator failed to initialize.");
            throw new RuntimeException(e);
        }

        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
        }
        catch (Exception e) {
            System.out.println("The key generator algorithm was not found.");
            throw new RuntimeException(e);
        }

        ECParameterSpec ecSpec = new ECParameterSpec(x9Params.getCurve(), x9Params.getG(), x9Params.getN(), x9Params.getH(), x9Params.getSeed());

        try {
            keyGen.initialize(ecSpec, random);
        }
        catch (Exception e) {
            System.out.println("The key generator failed to initialize.");
            throw new RuntimeException(e);
        }

        KeyPair keyPair = keyGen.generateKeyPair();

        return keyPair.getPrivate();
    }

    public static String privateKeyToString(PrivateKey privateKey) {
        String temp = getHex(((ECPrivateKey)privateKey).getS().toByteArray());

        if(temp.startsWith("00")) {
            temp = temp.substring(2);
        }

        return "0x" + temp;
    }

    public static PrivateKey stringToPrivateKey(String in) {
        if(in.startsWith("0x")) {
            in = in.substring(2);
        }

        BigInteger s = new BigInteger(in, 16);
        ECParameterSpec ecParameterSpec = new ECNamedCurveParameterSpec(curve, x9Params.getCurve(), x9Params.getG(), x9Params.getN(), x9Params.getH(), x9Params.getSeed());
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(s, ecParameterSpec);
        KeyFactory factory;
        try {
            factory = KeyFactory.getInstance("ECDSA","BC");
        }
        catch(Exception e) {
            System.out.println("The key generator algorithm was not found.");
            throw new RuntimeException(e);
        }
        PrivateKey privateKey;
        try {
            privateKey = factory.generatePrivate(privateKeySpec);
        }
        catch (Exception e) {
            System.out.println("The keyspec on the private key string is invalid.");
            throw new RuntimeException(e);
        }
        return privateKey;
    }
}
