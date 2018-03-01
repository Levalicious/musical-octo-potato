package core;

import java.util.Date;

import crypto.StringUtil;
import resources.GenesisBlocks;

public class Block implements GenesisBlocks {
	private String prefix;
	public String hash;
	public String[] previousHashes;
	private String data;
	private long timeStamp;
	private int nonce;
	
	public Block() {
	}
	public Block(String prefix, String data, String[] previousHashes) {
		this.prefix = prefix;
		this.data = data;
		this.previousHashes = previousHashes;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}
	
	public String calculateHash() {
		String prevHash = String.join(",", previousHashes);
		
		String calculatedhash = StringUtil.applySha256(
				Long.toString(timeStamp) +
				Integer.toString(nonce) +
				prevHash + 
				prefix +
				data
				);
		return calculatedhash;
	}
	
	
	public static Block getGenesis(int i) {
		switch(i) {
			case 1:	return GEN_ONE;
			case 2: return GEN_TWO;
			case 3: return GEN_THREE;
			case 4: return GEN_FOUR;
			default:System.out.println("Attempted to call nonexistent genesis block. Program will now exit.");
					System.exit(0);
					return new Block("","ERROR INVALID BLOCK",GEN_HASH);
		}
		
		
	}
	
	public String toString() {
		String tempBlock = ("Prefix: " + prefix + "\n" + "Block Hash: " + hash + "\n" + "Previous Hashes: " + String.join(",", previousHashes) + "\n" + "Data: " + data);
		return tempBlock;
	}
}
