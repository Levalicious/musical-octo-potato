package core.types;

import java.util.Date;
import crypto.StringUtil;

public class Block {
    private int depth;
    private String prefix;
    private String hash;
    private String[] previousHashes;
    private String data;
    private String timeStamp;

    public Block(String prefix, String data, String[] previousHashes) {
        this.prefix = prefix;
        this.data = data;
        this.previousHashes = previousHashes;
        this.timeStamp = new Date().toString();
        this.hash = calculateHash();
    }

    public String calculateHash(){
        String prevHash = String.join(",", previousHashes);

        String hash = StringUtil.applySha256(
                timeStamp +
                        prevHash +
                        prefix +
                        data
        );
        return hash;
    }

    public String getPrefix(){
        return prefix;
    }

    public String[] getPreviousHashes(){
        return previousHashes;
    }

    public String getData(){
        return data;
    }

    public String getTimeStamp(){
        return timeStamp.toString();
    }

    public int getDepth(){
        return depth;
    }

    public void setDepth(int depth){
        this.depth = depth;
    }

    public String toString(){
        String temp = ("Timestamp: " + timeStamp + "\nPrefix: " + prefix + "\nData: " +  data + "\nPrevious Hashes: " + String.join(",", previousHashes) + "\nBlock Hash: " + hash);
        return temp;
    }
}
