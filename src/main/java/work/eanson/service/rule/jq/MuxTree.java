package work.eanson.service.rule.jq;

import java.util.LinkedList;
import java.util.List;

/**
 * 招法树  树形结构
 *
 * @author 不晓得
 */
@SuppressWarnings("all")
public class MuxTree<T> {
    private T data;
    private MuxTree<T> parent;
    private List<MuxTree<T>> children;


    public MuxTree(T data) {
        this.data = data;
        this.parent = null;
        this.children = new LinkedList<>();
    }

    public MuxTree(T data, MuxTree<T> parent) {
        this.data = data;
        this.parent = parent;
        this.children = new LinkedList<>();
    }

    public MuxTree<T> insert(T data, MuxTree<T> parent) {
        MuxTree<T> child = new MuxTree<>(data, parent);
        parent.children.add(child);
        return child;
    }

    public MuxTree<T> insert(T data) {
        MuxTree<T> child = new MuxTree<>(data, this);
        this.children.add(child);
        return child;
    }


    public MuxTree<T> delete(MuxTree<T> parent, int index) {
        return parent.children.remove(index);
    }

    public boolean delete(MuxTree<T> parent, MuxTree<T> child) {
        return parent.children.remove(child);
    }


    public void preOrder(MuxTree<T> temp) {
        if (temp == null) {
            return;
        }
        MuxTree p = temp;
        int parentNum = 0;
        if (p.parent != null) {
            while (p.parent != null) {
                parentNum++;
                p = p.parent;
            }
            for (int i = 0; i < parentNum; i++) {
                System.out.print(" ");
            }
            System.out.println(temp.data);
        }
        for (MuxTree<T> index : temp.children) {
            preOrder(index);
        }
    }

    public MuxTree<T> getParent() {
        return this.parent;
    }

    public List<MuxTree<T>> getChildren() {
        return this.children;
    }

    public T getData() {
        return this.data;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public void addChildren(MuxTree<T> children) {
        this.children.add(children);
    }

//    public MuxTree<T> getNode(T data) {
//        if (this.data == data)
//            return this;
//        MuxTree<T> parent = this.parent;
//    }

//    public MuxTree<T> getRoot(MuxTree<T> node) {
//        if (node.parent == null)
//            return this;
//        return getRoot(node.parent);
//    }

    public void preOrder() {
        this.preOrder(this);
    }

}
