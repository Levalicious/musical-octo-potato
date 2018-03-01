package core;

import java.util.ArrayList;

public class Layer extends Block{
	public static long height;
	public static ArrayList<Block> layer;
	
	// Initializing a layer with no constructors means creating a genesis layer
	Layer(){
		layer = new ArrayList<Block>(4);
		layer.add(Block.getGenesis(1));
		layer.add(Block.getGenesis(2));
		layer.add(Block.getGenesis(3));
		layer.add(Block.getGenesis(4));
	}
	
	public void setBlock(int i, Block block) {
		layer.set(i, block);
	}
	
	public Block getBlock(int i) {
		return layer.get(i);
	}
	
	public String toString() {
		String tempLayer = "";
		for(int i = 0; i < layer.size(); i++) {
			tempLayer = (tempLayer + "\n" + "\n" + layer.get(i).toString());
		}
		return tempLayer;
	}
	
	public String[] getHashes() {
		String[] prevHashes = new String[layer.size()];
		for(int i = 0; i < layer.size(); i++) {
			prevHashes[i] = layer.get(i).calculateHash();
		}
		
		return prevHashes;
	}
	
	public int size() {
		return layer.size();
	}
	
	public boolean checkValid() {
		String[] tempHash = layer.get(0).previousHashes;
		for(int i = 1; i < layer.size(); i++) {
			if(!(tempHash == layer.get(i).previousHashes)) {
				return false;
			}
		}
		
		return true;
	}
}
