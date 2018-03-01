package core;

import java.util.ArrayList;

public class Blockchain extends Layer {
	private static ArrayList<Layer> blockchain;
	
	public Blockchain() {
		blockchain = new ArrayList<Layer>();
		
		blockchain.add(new Layer());
	}
	
	public static long getHeight() {
		return blockchain.size() - 1;
	}
	
	public static void addLayer(Layer layer) {
		blockchain.add(layer);
		System.out.println("Layer " + getHeight() + " has been added to the chain.");
	}
	
	public static void setBlock(int height, int depth, Block block) {
		blockchain.get(height).setBlock(depth,block);
	}
	
	public static Block getBlock(int height, int depth) {
		return blockchain.get(height).getBlock(depth);
	}
	
	public Layer getLayer(int height) {
		return blockchain.get(height);
	}
	
	public void addLayer() {
		
	}
	
	public boolean checkValid() {
		if(blockchain.get(0).previousHashes != GEN_HASH) {
			return false;
		}
		
		for(int i = 1; i < blockchain.size(); i++) {
			if(!(blockchain.get(i).checkValid())) {
				System.out.println("Layer " + i + " has inconsistent hash arrays.");
				return false;
			}
			
			if(blockchain.get(i).previousHashes != blockchain.get(i - 1).getHashes()) {
				System.out.println("The hash arrays in the blocks of layer " + i + " do not match with the block hashes of layer " + (i-1) + ".");
				return false;
			}
		}
		
		return true;
	}
}
