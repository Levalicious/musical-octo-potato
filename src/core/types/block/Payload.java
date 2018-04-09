package core.types.block;

import core.types.transaction.Transaction;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;

import static crypto.hash.Hash.getMerkleRoot;

public class Payload {
    private ArrayList<Transaction> txSet;

    protected Payload() {
        txSet = new ArrayList<Transaction>();
    }

    protected Payload(ArrayList<Transaction> txSet) {
        this.txSet = txSet;
    }

    protected void add(Transaction tx) {
        if(!txSet.contains(tx)) {
            txSet.add(tx);
        }else {
            System.out.println("Transaction already in block.");
        }
    }

    protected String countFees() {
        BigInteger total = BigInteger.ZERO;
        for(Transaction tx : txSet) {
            total.add(tx.getFee());
        }

        return total.toString();
    }

    protected boolean check() throws UnsupportedEncodingException {
        for(Transaction tx : txSet) {
            if(!tx.checkTx()) return false;
        }

        return true;
    }

    protected void process() throws UnsupportedEncodingException {
        for(Transaction tx : txSet) {
            tx.processTx();
        }
    }

    protected String getRoot() {
        return getMerkleRoot(txSet);
    }
}
