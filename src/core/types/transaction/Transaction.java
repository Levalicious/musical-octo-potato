package core.types.transaction;

import core.types.pools.UTXOPool;
import crypto.hash.Hash;
import util.byteUtils.BIUtil;
import util.byteUtils.ByteUtil;
import util.wallet.ECKey;

import javax.print.DocFlavor;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;

import static resources.Config.GENESIS_TX;
import static util.Hex.fromHex;
import static util.Hex.getHex;
import static util.byteUtils.ByteUtil.appendByte;
import static util.byteUtils.ByteUtil.concat;

public class Transaction implements UTXOPool, TxBase, Serializable {
    private byte[] sender;
    private byte[] recipient;
    private byte[] value;
    private byte[] data = null;
    private byte[] fee;
    private byte[] r;
    private byte[] s;
    private byte v;
    private byte[] txHash;

    private ArrayList<TransactionInput> inputs = new ArrayList<>();
    private ArrayList<TransactionOutput> outputs = new ArrayList<>();

    public Transaction() {

    }

    public Transaction(byte[] sender, byte[] recipient, byte[] value, ArrayList<TransactionInput> inputs, byte[] privKey) {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.data = null;
        this.fee = calcFee();
        this.inputs = inputs;
        genSig(privKey);
        this.txHash = calcHash();

    }

    public Transaction(boolean hasData, byte[] sender, byte[] recipient, byte[] value, byte[] data, ArrayList<TransactionInput> inputs, byte[] privKey) {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.data = data;
        this.fee = calcFeeWithData();
        this.inputs = inputs;
        genSigWithData(privKey);
        this.txHash = calcHashWithData();
    }

    public Transaction(byte[] sender, byte[] recipient, byte[] value, byte[] fee, ArrayList<TransactionInput> inputs, byte[] privKey) {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.fee = fee;
        this.data = null;
        this.inputs = inputs;
        genSig(privKey);
        this.txHash = calcHash();
    }

    public Transaction(byte[] sender, byte[] recipient, byte[] value, byte[] fee, byte[] data, ArrayList<TransactionInput> inputs, byte[] privKey) {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.fee = fee;
        this.data = data;
        this.inputs = inputs;
        genSigWithData(privKey);
        this.txHash = calcHashWithData();
    }

    public void genSig(byte[] privKey) {
        ECKey keypair = ECKey.fromPrivate(privKey);
        byte[] toSign = concat(sender, recipient, value, fee);
        for(TransactionInput i : inputs) {
            toSign = concat(toSign, i.toBytes());
        }
        ECKey.ECDSASignature sig = keypair.sign(crypto.hash.Hash.byteHash.blake256(toSign));
        r = sig.r.toByteArray();
        s = sig.s.toByteArray();
        v = sig.v;
    }

    public void genSigWithData(byte[] privKey) {
        ECKey keypair = ECKey.fromPrivate(privKey);
        byte[] toSign = concat(sender, recipient, value, fee, data);
        if(inputs != null) {
            for(TransactionInput i : inputs) {
                toSign = concat(toSign, i.toBytes());
            }
        }
        ECKey.ECDSASignature sig = keypair.sign(crypto.hash.Hash.byteHash.blake256(toSign));
        r = sig.r.toByteArray();
        s = sig.s.toByteArray();
        v = sig.v;
    }

    public byte[] calcFee() {
        return BIUtil.toBI(value).divide(new BigInteger("100")).toByteArray();

    }

    public byte[] calcFeeWithData() {
        return BIUtil.toBI(value).divide(new BigInteger("100")).multiply(new BigInteger(Long.toString(data.length))).toByteArray();
    }

    public byte[] calcByteFee() {
        byte[] inputSet = new byte[0];
        for(TransactionInput i : inputs) {
            inputSet = concat(inputSet, i.toBytes());
        }
        byte[] temp;
        if(data != null) {

            temp = concat(sender, recipient, value, data, inputSet);
        }else {
            temp = concat(sender, recipient, value, inputSet);
        }

        return fromHex(Integer.toHexString(temp.length));
    }

    public byte[] calcHash() {
        byte[] toHash = concat(sender, recipient, value, fee, r, s);
        toHash = appendByte(toHash, v);
        for(TransactionInput i : inputs) {
            toHash = concat(toHash, i.toBytes());
        }

        return crypto.hash.Hash.byteHash.blake256(toHash);
    }

    public byte[] calcHashWithData() {
        byte[] toHash = concat(sender, recipient, value, fee, data, r, s);
        toHash = appendByte(toHash, v);
        if(inputs != null) {
            for(TransactionInput i : inputs) {
                toHash = concat(toHash, i.toBytes());
            }
        }


        return crypto.hash.Hash.byteHash.blake256(toHash);
    }

    public boolean checkSig() {
        byte[] signedData;
        ECKey.ECDSASignature sig = new ECKey.ECDSASignature(new BigInteger(r), new BigInteger(s));
        if(data != null) {
            byte[] toSign = concat(sender, recipient, value, fee, data);
            for(TransactionInput i : inputs) {
                toSign = concat(toSign, i.toBytes());
            }
            signedData = crypto.hash.Hash.byteHash.blake256(toSign);
        }else{
            byte[] toSign = concat(sender, recipient, value, fee);
            for(TransactionInput i : inputs) {
                toSign = concat(toSign, i.toBytes());
            }
            signedData = crypto.hash.Hash.byteHash.blake256(toSign);
        }

        try{
            if(!Arrays.equals(ECKey.computeAddress(ECKey.signatureToKeyBytes(signedData, sig)), sender)) return false;
            if(data != null) {
                return ECKey.verify(signedData, sig, ECKey.signatureToKeyBytes(signedData, sig));
            }else {
                return ECKey.verify(signedData, sig, ECKey.signatureToKeyBytes(signedData, sig));
            }
        }catch(SignatureException s) {
            return false;
        }
    }

    public boolean checkInputs(byte[] value, byte[] fee) {
        BigInteger total = BigInteger.ZERO;
        for(TransactionInput i : inputs) {
            if(!UTXOPool.containsKey(i.getParentHash())) return false;
            total = total.add(new BigInteger(i.getUXTO().getValue()));
        }

        if(BIUtil.isLessThan(new BigInteger(value).add(new BigInteger(fee)), total)) return false;



        return true;
    }

    public boolean checkTx() {
        if(data != null) {
            if(Arrays.equals(calcHashWithData(), GENESIS_TX)) return true;

            if(BIUtil.isLessThan(new BigInteger(calcFeeWithData()), new BigInteger(fee))) fee = calcFeeWithData();

            if(BIUtil.isLessThan(new BigInteger(calcByteFee()), new BigInteger(fee))) fee = calcByteFee();

            if(!Arrays.equals(txHash,calcHashWithData())) return false;

            if(!checkSig()) return false;

            if(!checkInputs(value, fee)) return false;

        }else {
            if(BIUtil.isLessThan(new BigInteger(calcFee()), new BigInteger(fee))) fee = calcFee();

            if(BIUtil.isLessThan(new BigInteger(calcByteFee()), new BigInteger(fee))) fee = calcByteFee();

            if(!Arrays.equals(txHash,calcHash())) return false;

            if(!checkSig()) return false;

            if(!checkInputs(value, fee)) return false;
        }

        return true;
    }

    public boolean processTx() {
        if(!checkTx()) return false;

        if(Arrays.equals(calcHashWithData(), GENESIS_TX)) {
            outputs.add(new TransactionOutput(recipient, value, txHash));

            for(TransactionOutput o : outputs) {
                UTXOPool.put(o.getHash(), o);
            }

            return true;
        }

        for(TransactionInput i : inputs) {
            i.setUTXO(UTXOPool.get(i.getParentHash()));
        }

        BigInteger leftOver = (getInputs().subtract(new BigInteger(value).add(new BigInteger(fee))));
        outputs.add(new TransactionOutput(recipient, value, txHash));
        outputs.add(new TransactionOutput(recipient, leftOver.toByteArray(), txHash));

        if(!checkIO()) return false;

        for(TransactionOutput o : outputs) {
            UTXOPool.put(o.getHash(), o);
        }

        for(TransactionInput i : inputs) {
            if(i.getUXTO() == null) continue;
            UTXOPool.remove(i.getUXTO().getHash());
        }

        return true;
    }

    public BigInteger getInputs() {
        BigInteger total = BigInteger.ZERO;

        for(TransactionInput i : inputs) {
            if(i.getUXTO() == null) continue;
            total = total.add(new BigInteger(i.getUXTO().getValue()));
        }

        return total;
    }

    public BigInteger getOutputs() {
        BigInteger total = BigInteger.ZERO;

        for(TransactionOutput o : outputs) {
            if(o.getValue() == null) continue;
            total = total.add(new BigInteger(o.getValue()));
        }

        return total;
    }

    public boolean checkIO() {
        if(!BIUtil.isEqual(getInputs().subtract(new BigInteger(fee)), getOutputs())) return false;
        return true;
    }



    public String getSenderString() {
        return getHex(sender);
    }

    public String grabHash() {
        return getHex(txHash);
    }

    public BigInteger getFee() {
        return new BigInteger(fee);
    }
}
