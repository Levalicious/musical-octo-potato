package util.wallet;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import static crypto.hash.Hash.blake256;
import static util.Hex.fromHex;
import static util.Hex.getHex;
import static util.wallet.Private.stringToPrivateKey;
import static util.wallet.Public.stringToPublicKey;

public class Sign {
    public static String signString(String WIF, String in) {
        PrivateKey privateKey = stringToPrivateKey(WIF);
        return signData(privateKey, in);
    }

    public static String signString(PrivateKey privateKey, String in) {
        return signData(privateKey, in);
    }

    public static boolean verifySig(String pubKey, String data, String sig) {
        PublicKey publicKey = stringToPublicKey(pubKey);

        try {
            Signature verify = Signature.getInstance("ECDSA","BC");
            verify.initVerify(publicKey);
            verify.update(fromHex(blake256(data)));
            return verify.verify(fromHex(sig));
        }
        catch (Exception e) {
            System.out.println("Signature validation failed");
            throw new RuntimeException(e);
        }
    }

    private static String signData(PrivateKey privateKey, String in) {
        Signature sig;
        byte[] output;
        try {
            sig = Signature.getInstance("ECDSA","BC");
            sig.initSign(privateKey);
            byte[] strByte = fromHex(blake256(in));
            sig.update(strByte);
            output = sig.sign();
        }
        catch (Exception e) {
            System.out.println("Message signing failed.");
            throw new RuntimeException(e);
        }
        return getHex(output);
    }
}
