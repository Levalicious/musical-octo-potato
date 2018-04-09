package core.types.block;

import core.services.PoolManager;
import core.types.transaction.TransactionOutput;

import java.util.Date;

import static crypto.hash.Hash.blake256;
import static util.wallet.Private.*;
import static util.wallet.Public.*;
import static util.wallet.Sign.signString;
import static util.wallet.Sign.verifySig;

public class Header {
    private long height;
    private String prefix;
    private String hashRoot;
    private String txRoot;
    private String fees;
    private TransactionOutput reward;
    private String timestamp;
    private String sig;
    private String pubKey;
    private String blockHash;

    protected Header() {

    }

    protected Header(long height, String prefix, String txRoot, String fees, String hashRoot, String key) {
        this.height = height;
        this.prefix = prefix;
        this.txRoot = txRoot;
        this.fees = fees;
        this.reward = new TransactionOutput(publicKeyToAddress(privateKeyToPublicKey(stringToPrivateKey(key))), fees, null);
        this.hashRoot = hashRoot;
        this.timestamp = new Date().toString();
        this.sig = signBlock(key);
        this.pubKey = publicKeyToString(privateKeyToPublicKey(stringToPrivateKey(key)));
        this.blockHash = calculateHash();
    }

    private String signBlock(String privKey) {
        return signString(privKey, "{" +
                Long.toString(height) + "," +
                prefix + "," +
                hashRoot + "," +
                txRoot + "," +
                fees + "," +
                reward.toString() + "," +
                timestamp + "}");
    }

    protected boolean checkSig() {
        String data = ("{" +
                Long.toString(height) + "," +
                prefix + "," +
                hashRoot + "," +
                txRoot + "," +
                fees + "," +
                reward.toString() + "," +
                timestamp + "}");

        return verifySig(this.pubKey, data, this.sig);
    }

    private String calculateHash() {
        String hash = blake256("{" +
                Long.toString(height) + "," +
                prefix + "," +
                hashRoot + "," +
                txRoot + "," +
                fees + "," +
                reward.toString() + "," +
                timestamp + "," +
                sig + "," +
                pubKey + "}");

        return hash;
    }

    protected long getHeight() {
        return height;
    }

    protected String getPrefix() {
        return prefix;
    }

    protected String getHashRoot() {
        return hashRoot;
    }

    protected String getTxRoot() {
        return txRoot;
    }

    protected String getTimestamp() {
        return timestamp;
    }

    protected String getBlockHash() {
        return blockHash;
    }

    protected String getSig() {
        return sig;
    }

    protected String getPubKey() {
        return pubKey;
    }

    protected boolean checkHash() {
        return blockHash.equals(calculateHash());
    }

    public String toString() {
        String temp = ("{" +
                Long.toString(height) + "," +
                prefix + "," +
                hashRoot + "," +
                txRoot + "," +
                fees + "," +
                reward.toString() + "," +
                timestamp + "," +
                sig + "," +
                pubKey + "," +
                blockHash + "}");

        return temp;
    }

    protected boolean check() {
        if(!this.blockHash.equals(calculateHash())) {
            return false;
        }

        if(!this.checkSig()) {
            return false;
        }

        return true;
    }

    protected boolean process() {
        PoolManager.UTXOPool.put(reward.getHash(),reward);
        return true;
    }
}
