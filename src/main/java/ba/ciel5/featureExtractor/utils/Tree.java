package ba.ciel5.featureExtractor.utils;

/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (Pairhe "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class Tree {

    private Pair<String,String> head;

    private ArrayList<Tree> leafs = new ArrayList<Tree>();

    private Tree parent = null;

    private HashMap<Pair<String,String>, Tree> locate = new HashMap<Pair<String,String>, Tree>();

    public Tree() {

    }

    public Tree(Pair<String,String> head) {
        addRootNode(head);
    }

    public void addRootNode(Pair<String,String> head) {
        this.head = head;
        locate.put(head, this);
    }

    public void addLeaf(Pair<String,String> root, Pair<String,String> leaf) {
        if (locate.containsKey(root)) {
            locate.get(root).addLeaf(leaf);
        } else {
            addLeaf(root).addLeaf(leaf);
        }
    }

    public Tree addLeaf(Pair leaf) {
        Tree t = new Tree(leaf);
        leafs.add(t);
        t.parent = this;
        t.locate = this.locate;
        locate.put(leaf, t);
        return t;
    }

    public Tree setAsParent(Pair<String,String> parentRoot) {
        Tree t = new Tree(parentRoot);
        t.leafs.add(this);
        this.parent = t;
        t.locate = this.locate;
        t.locate.put(head, this);
        t.locate.put(parentRoot, t);
        return t;
    }

    public Pair<String,String> getHead() {
        return head;
    }

    public Tree getTree(Pair<String,String> element) {
        return locate.get(element);
    }

    public Tree getParent() {
        return parent;
    }

    public Collection<Pair<String,String>> getSuccessors(Pair<String,String> root) {
        Collection<Pair<String,String>> successors = new ArrayList<Pair<String,String>>();
        Tree tree = getTree(root);
        if (null != tree) {
            for (Tree leaf : tree.leafs) {
                successors.add(leaf.head);
            }
        }
        return successors;
    }

    public Collection<Tree> getSubTrees() {
        return leafs;
    }

    public static Collection<Pair<String,String>> getSuccessors(Pair<String,String> of, Collection<Tree> in) {
        for (Tree tree : in) {
            if (tree.locate.containsKey(of)) {
                return tree.getSuccessors(of);
            }
        }
        return new ArrayList<Pair<String,String>>();
    }

    private static final int indent = 2;

    public String keysToString() {
        return printKeyTree(0);
    }


    private String printKeyTree(int increment) {
        String s = "";
        String inc = "";
        for (int i = 0; i < increment; ++i) {
            inc = inc + " ";
        }
        s = inc + head.getKey();
        for (Tree child : leafs) {
            s += "\n" + child.printKeyTree(increment + indent);
        }
        return s;
    }

    public String keysWithOneLinerToString() {
        return printKeyWithOneLinerTree(0);
    }


    private String printKeyWithOneLinerTree(int increment) {
        String s = "";
        String inc = "";
        for (int i = 0; i < increment; ++i) {
            inc = inc + " ";
        }
        if ( ! head.getValue().contains("\n") )
            s = inc + head.getKey() + "=" + head.getValue();
        else
            s = inc + head.getKey();
        for (Tree child : leafs) {
            s += "\n" + child.printKeyWithOneLinerTree(increment + indent);
        }
        return s;
    }

    public String fullTreeToString() {
        return printFullTree(0);
    }

    private String printFullTree(int increment) {
        String s = "";
        String inc = "";
        for (int i = 0; i < increment; ++i) {
            inc = inc + " ";
        }
        s = inc + head;
        for (Tree child : leafs) {
            s += "\n" + child.printFullTree(increment + indent);
        }
        return s;
    }
}