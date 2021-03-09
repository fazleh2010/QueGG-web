package edu.citec.sc.queggweb.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrieNode<T> {
    public static final class DuplicateInsertException extends Exception {
        public DuplicateInsertException(String duplicatePath) {
            super("Duplicate path: " + duplicatePath);
        }
    }

    @JsonIgnore
    @Getter
    private TrieNode<T> parent;

    @Getter @Setter
    private T data;

    @Getter @Setter
    private String path;

    @Getter
    private List<TrieNode<T>> children = null;

    @JsonIgnore
    public boolean isRoot() {
        return this.parent == null;
    }
    @JsonIgnore
    public boolean isLeaf() {
        if (children == null) return true;
        return children.size() == 0;
    }

    private String fullPathCache = null;

    public void setParent(TrieNode<T> parent) {
        this.parent = parent;
        this.fullPathCache = null;
    }

    public String pathPrefix() {
        val parts = new ArrayList<String>();
        TrieNode<T> cur = this.parent;
        while (cur != null) {
            parts.add(cur.path);
            cur = cur.parent;
        }
        Collections.reverse(parts);
        return String.join("", parts);
    }

    public String fullPath() {
        if (fullPathCache != null) {
            return fullPathCache;
        }
        val parts = new ArrayList<String>();
        TrieNode<T> cur = this;
        while (cur != null) {
            parts.add(cur.path);
            cur = cur.parent;
        }
        Collections.reverse(parts);
        fullPathCache = String.join("", parts);
        return fullPathCache;
    }

    public TrieNode(TrieNode<T> parent, String path, T data) {
        this.parent = parent;
        this.setPath(path);
        this.setData(data);
    }

    TrieNode() {

    }

    @Override
    public String toString() {
        return "TrieNode{" +
                "path=" + this.fullPath() +
                '}';
    }

    public TrieNode<T> find(String path) {
        return find(path, false);
    }

    public TrieNode<T> find(String path, boolean caseInsensitive) {
        if (this.isLeaf()) {
            return this;
        }

        if (caseInsensitive) {
            path = path.toLowerCase();
        }

        // final String myPath = this.getPath();
        int commonPrefixLength = getCommonPrefixLength(fullPath(), path, caseInsensitive);
        if (commonPrefixLength == 0 && !this.isRoot()) {
            return this;
        }

        /* if (commonPrefixLength == 0) {
            return this;
        } else if (commonPrefixLength == myPath.length()) { */
        // descend into child nodes
        TrieNode<T> bestChildMatch = null;
        int bestChildMatchCommonLength = 0;

        if (this.children != null) {
            for (TrieNode<T> child: this.children) {
                TrieNode<T> subtreeMatch = child.find(path, caseInsensitive);
                if (bestChildMatch == null) {
                    bestChildMatch = subtreeMatch;
                    bestChildMatchCommonLength = getCommonPrefixLength(bestChildMatch.fullPath(), path, caseInsensitive);
                    continue;
                }
                if (bestChildMatchCommonLength < getCommonPrefixLength(subtreeMatch.fullPath(), path, caseInsensitive)) {
                    // System.out.println(subtreeMatch.fullPath() + " is a better match than " + bestChildMatch.fullPath());
                    bestChildMatch = subtreeMatch;
                    bestChildMatchCommonLength = getCommonPrefixLength(bestChildMatch.fullPath(), path, caseInsensitive);
                }
            }
        }

        if (bestChildMatch != null && commonPrefixLength >= bestChildMatchCommonLength) {
            // no child beats the current node
            return this;
        }

        return bestChildMatch != null ? bestChildMatch : this;
    }

    private int getCommonPrefixLength(final String pathA, final String pathB, boolean caseInsensitive) {
        if (caseInsensitive) {
            return getCommonPrefixLength(pathA.toLowerCase(), pathB.toLowerCase());
        }
        return getCommonPrefixLength(pathA, pathB);
    }

    private int getCommonPrefixLength(final String pathA, final String pathB) {
        int commonPrefixLength = 0;
        for (int idx = 0; idx < pathA.length(); idx++) {
            if (pathB.length() <= idx) {
                break;
            }
            if (pathA.charAt(idx) != pathB.charAt(idx)) {
                break;
            }
            commonPrefixLength = idx + 1;
        }
        return commonPrefixLength;
    }

    public void insert(final String fullPath, T data) throws DuplicateInsertException {
        final String myPath = this.getPath();
        final String myFullPath = this.fullPath();
        final String myPathPrefix = this.pathPrefix();
        final String path = fullPath.substring(this.pathPrefix().length());

        int commonPrefixLength = getCommonPrefixLength(myPath, path);

        if (commonPrefixLength == 0) {
            if (this.isRoot() && !"".equals(this.path)) {
                // if root and not split yet, we have to do so now

                T tmp = this.getData();
                this.setData(null);
                this.setPath("");

                TrieNode<T> newChildA = new TrieNode<>(this, myPath, tmp);
                if (this.children != null) {
                    for (TrieNode<T> prevChild : this.children) {
                        newChildA.addChild(prevChild);
                    }
                    this.children.clear();
                }
                TrieNode<T> newChildB = new TrieNode<>(this, path, data);

                this.addChild(newChildA);
                this.addChild(newChildB);
                return;
            } else {
                // otherwise we have reached a common prefix path
                TrieNode<T> newChild = new TrieNode<>(this, path, data);
                this.addChild(newChild);
                return;
            }
        }

        final String commonPrefix = myPath.substring(0, commonPrefixLength);
        final String remainderA = myPath.substring(commonPrefixLength);
        final String remainderB = path.substring(commonPrefixLength);

        if ("".equals(remainderA)) {
            // already correctly split
            if ("".equals(remainderB)) {
                // no remainder -> would be a duplicate
                throw new DuplicateInsertException(this.fullPath());
            }
            TrieNode<T> newChildB = new TrieNode<>(this, remainderB, data);
            this.addChild(newChildB);
        } else {
            // have to split

            T tmp = this.getData();
            this.setData(null);
            this.setPath(commonPrefix);

            TrieNode<T> newChildA = new TrieNode<>(this, remainderA, tmp);
            if (this.children != null) {
                for (TrieNode<T> prevChild : this.children) {
                    newChildA.addChild(prevChild);
                }
                this.children.clear();
            }
            TrieNode<T> newChildB = new TrieNode<>(this, remainderB, data);

            this.addChild(newChildA);
            this.addChild(newChildB);
        }
    }

    private void addChild(TrieNode<T> child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        child.setParent(this);
        this.children.add(child);
    }

    public int size() {
        if (this.children == null) {
            return 1;
        }

        int total = 1;
        for (TrieNode<T> child: this.children) {
            total += child.size();
        }
        return total;
    }

}
