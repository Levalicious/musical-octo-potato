package core.types.block;

import core.types.transaction.Transaction;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

import static crypto.hash.Hash.getMerkleRoot;
import static util.Hex.fromHex;

public class Payload implements Serializable {
    private ArrayList<Transaction> txSet;

    public Payload() {
        txSet = new ArrayList<Transaction>();
    }

    public Payload(ArrayList<Transaction> txSet) {
        this.txSet = txSet;
    }

    protected void add(Transaction tx) {
        if(!txSet.contains(tx)) {
            txSet.add(tx);
        }else {
            System.out.println("Transaction already in block.");
        }
    }

    protected byte[] countFees() {
        BigInteger total = BigInteger.ZERO;

        for(Transaction tx : txSet) {
            total.add(tx.getFee());
        }

        return total.toByteArray();
    }

    protected boolean check() {
        for(Transaction tx : txSet) {
            if(tx.checkTx()) return false;
            if(tx.checkIO()) return false;
        }

        return true;
    }

    protected void process() {
        for(Transaction tx : txSet) {
            tx.processTx();
        }
    }

    protected byte[] getRoot() {
        return fromHex(getMerkleRoot(txSet));
    }
}
