package core.types.transaction;

import crypto.hash.Hash;
import util.byteUtils.BIUtil;
import util.byteUtils.ByteUtil;

import java.io.Serializable;

import static crypto.hash.Hash.*;
import static util.Hex.fromHex;
import static util.Hex.getHex;
import static util.byteUtils.ByteUtil.concat;

public class TransactionOutput implements Serializable {
    private byte[] hash;
    private byte[] recipient;
    private byte[] value;
    private byte[] parentTxHash;

    public TransactionOutput(byte[] recipient, byte[] value, byte[] parentTxHash) {
        this.recipient = recipient;
        this.value = value;
        this.parentTxHash = parentTxHash;
        this.hash  = calcHash();
    }

    private byte[] calcHash() {
        if(parentTxHash != null) {
            return fromHex(blake256("{" + getHex(parentTxHash) + "," + getHex(value) + "," + getHex(recipient) + "}"));
        }else {
            return fromHex(blake256("{" + getHex(value) + "," + getHex(recipient) + "}"));
        }
    }

    public boolean checkOwner(byte[] pubkey) {
        return fromHex(walletHash(getHex(pubkey))).equals(recipient);
    }

    public byte[] getHash() {
        return hash;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] toBytes() {
        if(parentTxHash == null) {
            return concat(hash, recipient, value);
        }else {
            return concat(hash, recipient, value, parentTxHash);
        }
    }
}
