package core.types.block;

import core.types.transaction.Transaction;
import util.byteUtils.BIUtil;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

public class Block implements Serializable, Comparable<Block> {
    private Header header;
    private Payload payload;
    private Ancestors ancestors;

    /* Constructs empty block just because */
    public Block() {
        this.header = new Header();
        this.payload = new Payload();
        this.ancestors = new Ancestors();
    }

    public Block(Header header, Payload payload, Ancestors ancestors) {
        this.header = header;
        this.payload = payload;
        this.ancestors = ancestors;
    }

    public Block(byte[] height, String prefix, byte[] wif, Payload payload, Ancestors ancestors) {
        this.header = new Header(height, prefix, payload.getRoot(), payload.countFees(), ancestors.getRoot(), wif);
        this.payload = payload;
        this.ancestors = ancestors;
    }

    public boolean checkBlock() {
        if(!header.check()) return false;

        if(!payload.check()) return false;

        return true;
    }

    public void processBlock() {
        this.payload.process();
        this.header.process();
    }

    public void setHeader(byte[] height, String prefix, byte[] txRoot, byte[] fees, byte[] hashRoot, byte[] privkey) {
        this.header = new Header(height, prefix, txRoot, fees, hashRoot, privkey);
    }

    public void setPayload(ArrayList<Transaction> txList) {
        this.payload = new Payload(txList);
    }

    public void setAncestors(ArrayList<byte[]> ancestors) {
        this.ancestors = new Ancestors(ancestors);
    }

    public byte[] getTxRoot() {
        return this.payload.getRoot();
    }

    public byte[] getAncestorRoot() {
        return this.ancestors.getRoot();
    }

    public byte[] getBlockHash() {
        return this.header.getBlockHash();
    }

    public String getPrefix() {
        return this.header.getPrefix();
    }

    public byte[] getFees() {
        return this.payload.countFees();
    }

    @Override
    public int compareTo(Block block1) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if(BIUtil.isLessThan(new BigInteger(this.getPrefix(),16), new BigInteger(block1.getPrefix(), 16))) return AFTER;
        if(BIUtil.isLessThan(new BigInteger(block1.getPrefix(), 16), new BigInteger(this.getPrefix(), 16))) return BEFORE;

        return EQUAL;
    }
}