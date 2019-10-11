package akoamay.cell;

class Node {
    public int id;
    public int left;
    public int right;
    public int top;
    public int bottom;

    public Node(int id) {
        this.id = id;
        left = right = top = bottom = -1;
    }

}