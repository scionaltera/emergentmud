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

final public class Vertex extends Object implements ICoord {

    final public static Vertex VERTEX_AT_INFINITY = new Vertex(Double.NaN, Double.NaN);
    final private static Stack<Vertex> _pool = new Stack();

    private static Vertex create(double x, double y) {

        if (Double.isNaN(x) || Double.isNaN(y)) {
            return VERTEX_AT_INFINITY;
        }
        if (_pool.size() > 0) {

            return _pool.pop().init(x, y);
        } else {
            return new Vertex(x, y);
        }
    }
    private static int _nvertices = 0;
    private Point _coord;

    @Override
    public Point get_coord() {
        return _coord;
    }
    private int _vertexIndex;

    public int get_vertexIndex() {
        return _vertexIndex;
    }

    public Vertex(double x, double y) {
        init(x, y);
    }

    private Vertex init(double x, double y) {
        _coord = new Point(x, y);
        return this;
    }

    public void dispose() {
        _coord = null;
        _pool.push(this);
    }

    public void setIndex() {
        _vertexIndex = _nvertices++;
    }

    @Override
    public String toString() {
        return "Vertex (" + _vertexIndex + ")";
    }

    /**
     * This is the only way to make a Vertex
     *
     * @param halfedge0
     * @param halfedge1
     * @return
     *
     */
    public static Vertex intersect(Halfedge halfedge0, Halfedge halfedge1) {
        Edge edge0, edge1, edge;
        Halfedge halfedge;
        double determinant, intersectionX, intersectionY;
        boolean rightOfSite;

        edge0 = halfedge0.edge;
        edge1 = halfedge1.edge;
        if (edge0 == null || edge1 == null) {
            return null;
        }
        if (edge0.get_rightSite() == edge1.get_rightSite()) {
            return null;
        }

        determinant = edge0.a * edge1.b - edge0.b * edge1.a;
        if (-1.0e-10 < determinant && determinant < 1.0e-10) {
            // the edges are parallel
            return null;
        }

        intersectionX = (edge0.c * edge1.b - edge1.c * edge0.b) / determinant;
        intersectionY = (edge1.c * edge0.a - edge0.c * edge1.a) / determinant;

        if (Voronoi.compareByYThenX(edge0.get_rightSite(), edge1.get_rightSite()) < 0) {
            halfedge = halfedge0;
            edge = edge0;
        } else {
            halfedge = halfedge1;
            edge = edge1;
        }
        rightOfSite = intersectionX >= edge.get_rightSite().get_x();
        if ((rightOfSite && halfedge.leftRight == LR.LEFT)
                || (!rightOfSite && halfedge.leftRight == LR.RIGHT)) {
            return null;
        }

        return Vertex.create(intersectionX, intersectionY);
    }

    public double get_x() {
        return _coord.x;
    }

    public double get_y() {
        return _coord.y;
    }
}
