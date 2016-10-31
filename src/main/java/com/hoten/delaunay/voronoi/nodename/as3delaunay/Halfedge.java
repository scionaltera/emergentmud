/*
 * Java implementation by Connor Clark (www.hotengames.com). Pretty much a 1:1
 * translation of a wonderful map generating algorthim by Amit Patel of Red Blob Games,
 * which can be found here (http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/)
 * Hopefully it's of use to someone out there who needed it in Java like I did!
 * Note, the only island mode implemented is Radial. Implementing more is something for another day.
 *
 * FORTUNE'S ALGORTIHIM
 *
 * This is a java implementation of an AS3 (Flash) implementation of an algorthim
 * originally created in C++. Pretty much a 1:1 translation from as3 to java, save
 * for some necessary workarounds. Original as3 implementation by Alan Shaw (of nodename)
 * can be found here (https://github.com/nodename/as3delaunay). Original algorthim
 * by Steven Fortune (see lisence for c++ implementation below)
 *
 * The author of this software is Steven Fortune.  Copyright (c) 1994 by AT&T
 * Bell Laboratories.
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package com.hoten.delaunay.voronoi.nodename.as3delaunay;

import com.hoten.delaunay.geom.Point;
import java.util.Stack;

public final class Halfedge {

    private static Stack<Halfedge> _pool = new Stack();

    public static Halfedge create(Edge edge, LR lr) {
        if (_pool.size() > 0) {
            return _pool.pop().init(edge, lr);
        } else {
            return new Halfedge(edge, lr);
        }
    }

    public static Halfedge createDummy() {
        return create(null, null);
    }
    public Halfedge edgeListLeftNeighbor, edgeListRightNeighbor;
    public Halfedge nextInPriorityQueue;
    public Edge edge;
    public LR leftRight;
    public Vertex vertex;
    // the vertex's y-coordinate in the transformed Voronoi space V*
    public double ystar;

    public Halfedge( Edge edge, LR lr) {
        init(edge, lr);
    }

    private Halfedge init(Edge edge, LR lr) {
        this.edge = edge;
        leftRight = lr;
        nextInPriorityQueue = null;
        vertex = null;
        return this;
    }

    @Override
    public String toString() {
        return "Halfedge (leftRight: " + leftRight + "; vertex: " + vertex + ")";
    }

    public void dispose() {
        if (edgeListLeftNeighbor != null || edgeListRightNeighbor != null) {
            // still in EdgeList
            return;
        }
        if (nextInPriorityQueue != null) {
            // still in PriorityQueue
            return;
        }
        edge = null;
        leftRight = null;
        vertex = null;
        _pool.push(this);
    }

    public void reallyDispose() {
        edgeListLeftNeighbor = null;
        edgeListRightNeighbor = null;
        nextInPriorityQueue = null;
        edge = null;
        leftRight = null;
        vertex = null;
        _pool.push(this);
    }

    public boolean isLeftOf(Point p) {
        Site topSite;
        boolean rightOfSite, above, fast;
        double dxp, dyp, dxs, t1, t2, t3, yl;

        topSite = edge.get_rightSite();
        rightOfSite = p.x > topSite.get_x();
        if (rightOfSite && this.leftRight == LR.LEFT) {
            return true;
        }
        if (!rightOfSite && this.leftRight == LR.RIGHT) {
            return false;
        }

        if (edge.a == 1.0) {
            dyp = p.y - topSite.get_y();
            dxp = p.x - topSite.get_x();
            fast = false;
            if ((!rightOfSite && edge.b < 0.0) || (rightOfSite && edge.b >= 0.0)) {
                above = dyp >= edge.b * dxp;
                fast = above;
            } else {
                above = p.x + p.y * edge.b > edge.c;
                if (edge.b < 0.0) {
                    above = !above;
                }
                if (!above) {
                    fast = true;
                }
            }
            if (!fast) {
                dxs = topSite.get_x() - edge.get_leftSite().get_x();
                above = edge.b * (dxp * dxp - dyp * dyp)
                        < dxs * dyp * (1.0 + 2.0 * dxp / dxs + edge.b * edge.b);
                if (edge.b < 0.0) {
                    above = !above;
                }
            }
        } else /* edge.b == 1.0 */ {
            yl = edge.c - edge.a * p.x;
            t1 = p.y - yl;
            t2 = p.x - topSite.get_x();
            t3 = yl - topSite.get_y();
            above = t1 * t1 > t2 * t2 + t3 * t3;
        }
        return this.leftRight == LR.LEFT ? above : !above;
    }
}
