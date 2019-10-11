package akoamay.cell;

import java.awt.*;
import java.awt.event.*;

class SimulationCanvas extends Canvas implements MouseListener {

    int len = 2;
    int w;
    int h;
    Node[] nodes;

    public SimulationCanvas(int w, int h) {
        setBackground(Color.white);
        addMouseListener(this);
        this.w = w;
        this.h = h;

        nodes = new Node[len * len];

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                int idx = i * len + j;
                nodes[idx] = new Node(idx);
                if (j < 0)
                    nodes[idx].left = idx - 1;
                if (j < len - 1)
                    nodes[idx].right = idx + 1;
                if (i < 0)
                    nodes[idx].top = idx - len;
                if (i < len - 1)
                    nodes[idx].bottom = idx + len;
            }
        }

    }

    public int distance(int x1, int y1, int x2, int y2) {
        int idx1 = y1 * len + x1;
        int idx2 = y2 * len + y2;

        boolean[] floodMap = new boolean[len * len];
        boolean[] _floodMap = new boolean[len * len];
        floodMap[idx1] = true;

        int distance = 0;
        for (int k = 0; k < len * len; k++) {
            if (floodMap[idx2])
                return distance;
            for (int i = 0; i < len * len; i++) {
                int left = nodes[i].left;
                int right = nodes[i].right;
                int top = nodes[i].top;
                int bottom = nodes[i].bottom;
                if (floodMap[i] || (left != -1 && floodMap[left]) || (right != -1 && floodMap[right])
                        || (top != -1 && floodMap[top]) || (bottom != -1 && floodMap[bottom])) {
                    _floodMap[i] = true;
                }
            }
            for (int i = 0; i < len * len; i++) {
                floodMap[i] = _floodMap[i];
            }
            distance++;
        }

        return -1;
    }

    public void paint(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}