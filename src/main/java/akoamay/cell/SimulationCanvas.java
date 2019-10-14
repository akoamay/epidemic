package akoamay.cell;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class SimulationCanvas extends Canvas implements MouseListener {

    int len = 100;
    int w;
    int h;
    Node[] nodes;
    double[] ipv;
    short[] bulkd;

    public void genIpv() {
        ipv = new double[len];
        double s = 1.0;
        double v = 0.0;
        double m = 2;
        for (int i = 0; i < len; i++) {
            double _v = Math.exp(-Math.pow((i - m), 2.0) / (2.0 * Math.pow(s, 2.0)))
                    / Math.sqrt(2.0 * Math.PI * Math.pow(s, 2.0));
            v += _v;
            ipv[i] = v;
        }
    }

    public void genIpv2() {
        ipv = new double[len];
        double v = 0.0;
        double vv = 0.0;
        for (int i = 2; i < len; i++) {
            double _v = 1.0 / (Math.pow(i, 1.0));
            v += _v;
        }
        for (int i = 2; i < len; i++) {
            double _v = (1.0 / (Math.pow(i, 1.0))) / v;
            vv += _v;
            ipv[i] = vv;
        }
        System.out.println("vv=" + vv);
    }

    public int getLongPathLenth() {
        int l = 0;
        while (l == 0 || l == 1) {
            double r = Math.random();
            for (int i = 0; i < len; i++) {
                if (ipv[i] > r) {
                    // System.out.println("len=" + i);
                    l = i;
                    break;
                }
            }
        }
        return l;
        // return (int) (len * r) + 2;
    }

    public int getLongPath(int idx) {
        int length = getLongPathLenth();
        // short[] bulklen = bulkdistance(idx);
        List<Integer> idxList = new ArrayList<Integer>();
        for (int i = 0; i < len * len; i++) {
            // if (bulklen[i] == length) {
            if (bulkd[idx * len * len + i] == length) {
                if (nodes[i].bottom != idx && nodes[i].top != idx && nodes[i].left != idx && nodes[i].right != idx
                        && nodes[i].lc != idx) {
                    idxList.add(i);
                }
            }
        }

        int d = idxList.size();
        int _i = (int) (Math.random() * d);
        return idxList.get(_i);
    }

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
                nodes[idx].top = nodes[idx].left = nodes[idx].bottom = nodes[idx].right = nodes[idx].lc = -1;
                if (j > 0)
                    nodes[idx].left = idx - 1;
                if (j < len - 1)
                    nodes[idx].right = idx + 1;
                if (i > 0)
                    nodes[idx].top = idx - len;
                if (i < len - 1)
                    nodes[idx].bottom = idx + len;
            }
        }

        genIpv();
        // genIpv2();
        for (int i = 0; i < len; i++) {
            // System.out.println(ipv[i]);
        }

        bulkd = loadbulkd();

        for (int i = 0; i < len * len; i++) {
            int t = 0;
            System.out.println(i);
            if (Math.random() < (1.0 / 10.0)) {
                // while (nodes[i].lc == -1 && t < 500) {
                int _idx = (int) (Math.random() * len * len);
                // int _idx = getLongPath(i);
                if (_idx != i && nodes[_idx].lc == -1) {
                    nodes[i].lc = _idx;
                    nodes[_idx].lc = i;
                }
                t++;
            }
        }

        /**
         * for (int i = 0; i < len * len; i++) { if (nodes[i].lc == -1) { int _idx =
         * getLongPath(i); nodes[i].lc = _idx; nodes[_idx].lc = i; } if (i % len == 0)
         * System.out.println((double) i / (len * len) * 100 + "%");
         * 
         * }
         */

        // savebulkd();
        // System.out.println("dist=" + distance(0, 0, 20, 20));
        // System.out.println("spa=" + spa());

        dump();
    }

    public short[] loadbulkd() {
        String file = "./bulkdistance_" + len + ".dat";
        short[] obj = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            obj = (short[]) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * for (int i = 0; i < len * len * len * len; i++) { System.out.println(obj[i]);
         * }
         */
        return obj;
    }

    public void savebulkd() {
        short obj[] = new short[len * len * len * len];
        for (int i = 0; i < len * len; i++) {
            short[] d = bulkdistance(i);
            for (int j = 0; j < len * len; j++) {
                obj[i * len * len + j] = d[j];
            }
            if (i % len == 0) {
                System.out.println(((double) i / (len * len)) * 100 + "%");
            }
        }

        try {
            String file = "./bulkdistance_" + len + ".dat";
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dump() {
        try {
            FileWriter fw = new FileWriter("graph.cyt", false);
            fw.write("label\tsource\ttarget\r\n");
            int c = 0;
            for (int i = 0; i < len * len; i++) {
                if (nodes[i].top != -1 && nodes[i].top > i) {
                    fw.write(c + "\t" + i + "\t" + nodes[i].top + "\r\n");
                    c++;
                }
                if (nodes[i].right != -1 && nodes[i].right > i) {
                    fw.write(c + "\t" + i + "\t" + nodes[i].right + "\r\n");
                    c++;
                }
                if (nodes[i].bottom != -1 && nodes[i].bottom > i) {
                    fw.write(c + "\t" + i + "\t" + nodes[i].bottom + "\r\n");
                    c++;
                }
                if (nodes[i].left != -1 && nodes[i].left > i) {
                    fw.write(c + "\t" + i + "\t" + nodes[i].left + "\r\n");
                    c++;
                }
                if (nodes[i].lc != -1 && nodes[i].lc > i) {
                    fw.write(c + "\t" + i + "\t" + nodes[i].lc + "\r\n");
                    c++;
                }
            }
            fw.close();
        } catch (Exception e) {
        }
    }

    public short[] bulkdistance(int idx) {
        short[] dists = new short[len * len];

        boolean[] floodMap = new boolean[len * len];
        boolean[] _floodMap = new boolean[len * len];
        floodMap[idx] = true;

        short distance = 0;
        for (int k = 0; k < len * len; k++) {
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
            distance++;
            for (int i = 0; i < len * len; i++) {
                if (!floodMap[i] && _floodMap[i]) {
                    dists[i] = distance;
                }
                floodMap[i] = _floodMap[i];
            }
        }
        return dists;
    }

    public double spa() {
        int t = 0;

        double avg = 0.0;
        for (int i = 0; i < len * len - 1; i++) {
            // short[] dists = bulkdistance(i);
            for (int j = i + 1; j < len * len; j++) {
                avg += bulkd[i * len * len + j];
                t++;
                // System.out.println("\t" + ((double) j / (len * len)) * 100 + "%");
            }
            System.out.println(((double) i / (len * len - 1)) * 100 + "%");
        }
        return avg / t;
    }

    public void connect(int x1, int y1, int x2, int y2) {
        int idx1 = x1 + y1 * len;
        int idx2 = x2 + y2 * len;
        nodes[idx1].lc = idx2;
        nodes[idx2].lc = idx1;
    }

    public int distance(int idx1, int idx2) {

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

    public int distance(int x1, int y1, int x2, int y2) {
        int idx1 = y1 * len + x1;
        int idx2 = y2 * len + x2;
        return distance(idx1, idx2);
    }

    public void paint(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);

        g.setColor(Color.black);
        int idx;
        int[] p;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                idx = i * len + j;
                p = nodeCoords(idx);
                g.drawOval(p[0] - 5, p[1] - 5, 10, 10);

                drawLine(g, idx, nodes[idx].top);
                drawLine(g, idx, nodes[idx].left);
                drawLine(g, idx, nodes[idx].right);
                drawLine(g, idx, nodes[idx].bottom);
                drawLine(g, idx, nodes[idx].lc);
            }
        }
    }

    public void drawLine(Graphics g, int idx1, int idx2) {
        if (idx1 == -1 || idx2 == -1)
            return;
        int[] p1;
        int[] p2;
        p1 = nodeCoords(idx1);
        p2 = nodeCoords(idx2);
        g.drawLine(p1[0], p1[1], p2[0], p2[1]);

    }

    public int[] nodeCoords(int idx) {
        int j = idx % len;
        int i = idx / len;
        int x = (w / (len + 1)) * j + w / (len + 1) / 2;
        int y = (h / (len + 1)) * i + h / (len + 1) / 2;
        int[] res = { x, y };
        return res;
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