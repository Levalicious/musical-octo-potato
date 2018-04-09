package core.types.pools;

import core.types.tree.TreeNode;

import java.util.concurrent.ConcurrentHashMap;

public interface LeafPool {
    ConcurrentHashMap<String, TreeNode> leaves = new ConcurrentHashMap<String, TreeNode>();

}
