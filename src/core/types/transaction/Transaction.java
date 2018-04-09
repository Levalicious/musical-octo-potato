package core.types.transaction;

import core.types.pools.UTXOPool;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.ArrayList;

import static crypto.hash.Hash.blake256;
import static resources.Config.GENESIS_TX;
import static resources.Config.MIN_BYTE_FEE;
import static util.wallet.Private.*;
import static util.wallet.Public.*;
import static util.wallet.Sign.*;

public class Transaction implements UTXOPool, TxBase, Serializable {
    private String sender;
    private String recipient;
    private String value;
    private String data;
    private String fee;
    private String signature;
    private String senderKey;
    private String txHash;

    private ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    private ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    public Transaction(String sender, String recipient, String value, ArrayList<TransactionInput> inputs, String wif) throws UnsupportedEncodingException {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.fee = calcFee();
        this.inputs = inputs;
        genSig(stringToPrivateKey(wif));
        this.txHash = calcHash();
    }

    public Transaction(String sender, String recipient, String value, String data, ArrayList<TransactionInput> inputs, String wif) throws UnsupportedEncodingException {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.fee = calcFee();
        this.data = data;
        this.inputs = inputs;
        genSig(stringToPrivateKey(wif));
        this.txHash = calcHash();
    }

    public Transaction(String sender, String recipient, String value, String fee, String data, ArrayList<TransactionInput> inputs, String wif) {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.fee = fee;
        this.data = data;
        this.inputs = inputs;
        this.fee = fee;
        genSig(stringToPrivateKey(wif));
        this.txHash = calcHash();
    }

    private Transaction(String recipient) {
        this.sender = "0x0000000000000000000000000000000000000000";
        this.recipient = recipient;
        this.value = "100000000000000000000000000000000000";
        this.inputs = null;
        this.txHash = calcHash();
    }

    public static Transaction genesis(String recipient) {
        return new Transaction(recipient);
    }

    public String calcHash() {
        return blake256("{" + this.sender + "," + this.recipient + "," + this.value + "," + this.inputs.toString() + "," + this.signature + "," + this.senderKey + "}");
    }

    public String grabHash() {
        return this.txHash;
    }

    public String calcFee() throws UnsupportedEncodingException  {
        BigInteger val = new BigInteger(value);
        val = val.divide(new BigInteger("100"));

        byte[] tx;
        if(this.data != null) {
            tx = ("{" + this.sender + "," + this.recipient + "," + this.value + "," + this.data + "," + this.inputs.toString() + "}").getBytes("UTF-8");
        }else {
            tx = ("{" + this.sender + "," + this.recipient + "," + this.value + "," + this.inputs.toString() + "}").getBytes("UTF-8");
        }

        BigInteger bytecount = new BigInteger(Integer.toString(tx.length));

        /* Transaction hasn't been signed, therefore signature, hash, and senderkey have to be added in as such */
        bytecount = bytecount.add(new BigInteger("168"));

        /* If the sender attaches arbitrary data, make the transaction cost (size ^ 2) * bytefee */
        if(this.data != null) {
            if(val.compareTo(bytecount.multiply(MIN_BYTE_FEE)) < 0) {
                return bytecount.multiply(MIN_BYTE_FEE).toString();
            }else {
                return val.toString();
            }
        }else {
            if(val.compareTo(bytecount.multiply(bytecount).multiply(MIN_BYTE_FEE)) < 0) {
                return bytecount.multiply(bytecount).multiply(MIN_BYTE_FEE).toString();
            }else {
                return val.toString();
            }
        }
    }

    public void genSig(PrivateKey privateKey) {
        String data;
        if(this.data != null) {
            data = "{" + sender + "," + recipient + "," + value + "," + this.data + "," + fee + "," +  this.inputs.toString() + "}";
        }else {
            data = "{" + sender + "," + recipient + "," + value + "," + fee + "," +  this.inputs.toString() + "}";
        }

        this.signature = signString(privateKey, blake256(data));
        this.senderKey = publicKeyToString(util.wallet.Public.privateKeyToPublicKey(privateKey));
    }

    private boolean checkSig() {
        String data;
        if(this.data != null) {
            data = "{" + sender + "," + recipient + "," + value + "," + this.data + "," + fee + "," + this.inputs.toString() + "}";
        }else {
            data = "{" + sender + "," + recipient + "," + value + "," + fee + "," + this.inputs.toString() + "}";
        }

        return verifySig(senderKey, blake256(data), signature);
    }

    public boolean checkTx() throws UnsupportedEncodingException {
        if(calcHash().equals(GENESIS_TX)) {
            return true;
        }

        if(this.fee == null) {
            return false;
        }

        if((new BigInteger(this.fee)).compareTo((new BigInteger(calcFee()))) < 0) {
            return false;
        }

        if(this.getInputs().compareTo(new BigInteger(this.value).add(new BigInteger(this.fee))) < 0) {
            return false;
        }

        if(!checkInputs()) {
            return false;
        }

        if(checkSig()) {
            if(txHash.equals(calcHash())) {
                return this.sender.equalsIgnoreCase(publicKeyToAddress(stringToPublicKey(senderKey)));
            }
        }

        return false;
    }

    public boolean processTx() throws UnsupportedEncodingException {
        if(!checkTx()) {
            return false;
        }

        if(calcHash().equals(GENESIS_TX)) {
            outputs.add(new TransactionOutput(this.recipient, this.value, this.txHash));

            for(TransactionOutput o : outputs) {
                UTXOPool.put(o.getHash(), o);
            }

            return true;
        }

        for(TransactionInput i : inputs) {
            i.setUTXO(UTXOPool.get(i.getParentHash()));
        }

        if(getInputs().intValue() < 1) {
            System.out.println("Transaction inputs too small.");
            return false;
        }

        String leftOver = ((new BigInteger(this.value)).subtract(getInputs())).subtract(new BigInteger(this.fee)).toString();
        txHash = calcHash();
        outputs.add(new TransactionOutput(this.recipient, this.value, this.txHash));
        outputs.add(new TransactionOutput(this.recipient, leftOver, this.txHash));

        if(!getInputs().toString().equals(getOutputs().toString())) {
            outputs = null;
            return false;
        }

        for(TransactionOutput o : outputs) {
            UTXOPool.put(o.getHash(), o);
        }

        for(TransactionInput i : inputs) {
            if(i.getUTXO() == null) continue;
            UTXOPool.remove(i.getUTXO().getHash());
        }

        return true;
    }

    public BigInteger getInputs() {
        BigInteger total = BigInteger.ZERO;

        for(TransactionInput i : inputs) {
            if(i.getUTXO() == null) continue;
            total = total.add(i.getUTXO().getValue());
        }

        return total;
    }

    public boolean checkInputs() {
        for(TransactionInput i : inputs) {
            if(!UTXOPool.containsKey(i.getParentHash())) return false;
        }

        return true;
    }

    public BigInteger getOutputs() {
        BigInteger total = BigInteger.ZERO;

        for(TransactionOutput o : outputs) {
            if(o.getValue() == null) continue;
            total = total.add(o.getValue());
        }

        return total;
    }

    public String getData() {
        if(data != null) {
            return this.data;
        }else {
            return null;
        }
    }

    public BigInteger getFee() {
        return new BigInteger(this.fee);
    }

    @Override
    public String getSender() {
        return this.sender;
    }

    @Override
    public String toString() {
        if(data != null) {
            return ("{" + this.sender + "," + this.recipient + "," + this.value + "," + this.fee + "," + this.inputs.toString() + "," + this.data + "," + this.signature + "," + this.senderKey + "," + this.txHash + "}");
        }else {
            return ("{" + this.sender + "," + this.recipient + "," + this.value + "," + this.fee + "," + this.inputs.toString() + "," + this.signature + "," + this.senderKey + "," + this.txHash + "}");
        }
    }
}
