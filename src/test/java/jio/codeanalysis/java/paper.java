package jio.codeanalysis.java;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class paper {
    @Test
    public void test() {
        getPaperFolding(10);
    }
    public int[] getPaperFolding(int n) {
        Tree root = getChild(n);
        List<Integer> resultList = new ArrayList<>();
        treeToArray(root, resultList);
        int[] resultArray = resultList.stream().mapToInt(i->i).toArray();
        System.out.println(resultList);
        return resultArray;
    }

    private Tree getChild(int n) {
        Tree root = new Tree(0);
        for(int i=1; i<n; i++) {
            addChildgen(root);
        }
        return root;
    }
    private void addChildgen(Tree leaf) {
        if(leaf.left == null) {
            leaf.addChildgen();
        } else {
            addChildgen(leaf.left);
            addChildgen(leaf.right);
        }
    }
    private void treeToArray(Tree leaf, List<Integer> result) {
        if(leaf.left == null) {
            result.add(new Integer(leaf.value));
        } else {
            treeToArray(leaf.left, result);
            result.add(new Integer(leaf.value));
            treeToArray(leaf.right, result);
        }
    }
}

class Tree {
    int value;
    Tree left = null;
    Tree right = null;

    public Tree(int v) {
        this.value = v;
    }
    public void addChildgen() {
        left = new Tree(0);
        right = new Tree(1);
    }
}
