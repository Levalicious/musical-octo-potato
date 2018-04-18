package core.types.block;

import core.services.PoolManager;
import core.types.transaction.TransactionOutput;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import util.wallet.ECKey;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import static util.byteUtils.ByteUtil.concat;

public class Header implements Serializable {
    private byte[] height;
    private String prefix;
    private byte[] hashRoot;
    private byte[] txRoot;
    private byte[] fees;
    private TransactionOutput reward;
    private byte[] timestamp;
    private byte[] r;
    private byte[] s;
    private byte v;
    private byte[] blockHash;

    public Header() {

    }
    
    public Header(byte[] height, String prefix, byte[] txRoot, byte[] fees, byte[] hashRoot, byte[] privKey) {
        this.height = height;
        this.prefix = prefix;
        this.txRoot = txRoot;
        this.fees = fees;
        this.reward = new TransactionOutput(ECKey.computeAddress(ECKey.publicKeyFromPrivate(new BigInteger(privKey),true )), fees, null);
        this.hashRoot = hashRoot;

        try {
            this.timestamp = new Date().toString().getBytes("UTF-8");
        } catch(Exception e) {
            System.out.println("Your computer doesn't support the necessary encoding.");
            System.exit(0);
        }

        signBlock(privKey);
        this.blockHash = calcHash();
    }

    private void signBlock(byte[] privKey) {
        byte[] temp = new byte[0];
        try {
            temp = concat(temp, height, prefix.getBytes("UTF-8"), hashRoot, txRoot, fees, reward.toBytes(), timestamp);
        }catch(Exception e) {
            System.out.println("Your computer doesn't support the necessary encoding.");
            System.exit(0);
        }

        ECKey.ECDSASignature sig = ECKey.fromPrivate(privKey).sign(crypto.hash.Hash.byteHash.blake256(temp));

        this.r = sig.r.toByteArray();
        this.s = sig.s.toByteArray();
        this.v = sig.v;
    }

    protected boolean checkSig() {
        byte[] temp = new byte[0];
        try {
            temp = concat(temp, height, prefix.getBytes("UTF-8"), hashRoot, txRoot, fees, reward.toBytes(), timestamp);
        }catch(Exception e) {
            System.out.println("Your computer doesn't support the necessary encoding.");
            System.exit(0);
        }

        ECKey.ECDSASignature sig = ECKey.ECDSASignature.fromComponents(r,s,v);

        try {
            return ECKey.verify(crypto.hash.Hash.byteHash.blake256(temp), sig, ECKey.signatureToKeyBytes(crypto.hash.Hash.byteHash.blake256(temp), sig));
        }catch(Exception e) {
            return false;
        }
    }

    private byte[] calcHash() {
        byte[] temp = new byte[0];
        try {
            temp = concat(temp, height, prefix.getBytes("UTF-8"), hashRoot, txRoot, fees, reward.toBytes(), timestamp);
        }catch(Exception e) {
            System.out.println("Your computer doesn't support the necessary encoding.");
            System.exit(0);
        }

        return crypto.hash.Hash.byteHash.blake256(temp);
    }

    protected byte[] getHeight() {
        return height;
    }

    protected String getPrefix() {
        return prefix;
    }

    protected byte[] getHashRoot() {
        return hashRoot;
    }

    protected byte[] getTxRoot() {
        return txRoot;
    }

    protected byte[] getTimestamp() {
        return timestamp;
    }

    protected byte[] getBlockHash() {
        return blockHash;
    }

    protected byte[] getFees() {
        return fees;
    }

    protected byte[] getR() {
        return r;
    }

    protected byte[] getS() {
        return s;
    }

    protected byte getV() {
        return v;
    }

    protected boolean checkHash() {
        return ByteUtils.equals(blockHash, calcHash());
    }

    protected boolean check() {
        if(!checkHash()) return false;

        if(!checkSig()) return false;

        return true;
    }

    protected boolean process() {
        PoolManager.UTXOPool.put(reward.getHash(), reward);
        return true;
    }
}
