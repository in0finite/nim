package util;

import java.util.LinkedList;

public class Tree<T> {

    public class Node {
        public LinkedList<Node> children = new LinkedList<>();
        public T userData ;
    }


    private Node root;

}
