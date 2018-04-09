package core.types.transaction;

import java.math.BigInteger;

import static crypto.hash.Hash.blake256;
import static util.wallet.Public.publicKeyToAddress;
import static util.wallet.Public.stringToPublicKey;

public class TransactionOutput {
    private String hash;
    private String recipient;
    private String value;
    private String parentTxHash;

    public TransactionOutput(String recipient, String value, String parentTxHash) {
        this.recipient = recipient;
        this.value = value;
        this.parentTxHash = parentTxHash;
        this.hash = calcHash();
    }

    private String calcHash() {
        return blake256("{" + parentTxHash + "," + value + "," + recipient + "}");
    }

    public boolean checkOwner(String pubKey) {
        return recipient.equals(publicKeyToAddress(stringToPublicKey(pubKey)));
    }

    public String getHash() {
        return hash;
    }

    public BigInteger getValue() {
        return new BigInteger(value);
    }

    public String toString() {
        if(parentTxHash == null) {
            return ("{" + hash + "," + recipient + "," + value + "}");
        }else {
            return ("{" + hash + "," + recipient + "," + value + "," + parentTxHash + "}");
        }
    }
}
