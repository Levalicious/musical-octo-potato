package core.types.chain.layer;

import core.types.block.Block;
import core.types.transaction.Transaction;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import static resources.Config.MINER_WIF;

public class Finalized implements Serializable {
    private byte[] height;
    private ArrayList<Block> layer;

    public Finalized(byte[] height) {
        this.height = height;
        layer = new ArrayList<Block>();
    }

    public Finalized(byte[] height, ArrayList<Block> layer) {
        this.height = height;
        this.layer = layer;
    }

    public ArrayList<Block> getInstance() {
        return this.layer;
    }

    public void addBlock(String prefix, ArrayList<Transaction> transactions, Finalized layer) {
        Block block = new Block();

        block.setAncestors(layer.getHashes());
        block.setPayload(transactions);

        block.setHeader(height, prefix, block.getTxRoot(), block.getFees(), block.getAncestorRoot(), MINER_WIF);

        this.layer.add(block);
    }

    public void addBlock(Block block) {
        this.layer.add(block);
    }

    public ArrayList<byte[]> getHashes() {
        Collections.sort(layer);
        ArrayList<byte[]> hashList = new ArrayList<byte[]>();
        for(Block block : layer) {
            hashList.add(block.getBlockHash());
        }

        return hashList;
    }

    public boolean checkBlocks() {
        Collections.sort(layer);
        for(Block block : layer) {
            if(!block.checkBlock()) return false;
        }

        for(int i = 0; i < layer.size(); i++) {
            for(int j = 0; j < layer.size(); j++) {
                if(i != j) {
                    if(layer.get(i).getPrefix().equals(layer.get(j).getPrefix())) return false;
                }
            }
        }

        return true;
    }

    public boolean checkAncestors(byte[] realPrevRoot) {
        Collections.sort(layer);
        byte[] prevRoot = new byte[0];
        boolean hasRoot = false;
        for(Block block : layer) {
            if(!hasRoot) {
                prevRoot = block.getAncestorRoot();
                hasRoot = true;
            }else {
                if(!ByteUtils.equals(prevRoot,block.getAncestorRoot())) return false;
            }
        }

        if(!ByteUtils.equals(prevRoot,realPrevRoot)) return false;

        return true;
    }

    public void processLayer() {
        Collections.sort(layer);
        for(Block block : layer) {
            block.processBlock();
        }
    }
}
