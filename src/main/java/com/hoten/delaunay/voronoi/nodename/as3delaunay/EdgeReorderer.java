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

import java.util.ArrayList;

public final class EdgeReorderer {

    private ArrayList<Edge> _edges;
    private ArrayList<LR> _edgeOrientations;

    public ArrayList<Edge> get_edges() {
        return _edges;
    }

    public ArrayList<LR> get_edgeOrientations() {
        return _edgeOrientations;
    }

    public EdgeReorderer(ArrayList<Edge> origEdges, Class criterion) {
        if (criterion != Vertex.class && criterion != Site.class) {
            throw new Error("Edges: criterion must be Vertex or Site");
        }
        _edges = new ArrayList();
        _edgeOrientations = new ArrayList();
        if (origEdges.size() > 0) {
            _edges = reorderEdges(origEdges, criterion);
        }
    }

    public void dispose() {
        _edges = null;
        _edgeOrientations = null;
    }

    private ArrayList<Edge> reorderEdges(ArrayList<Edge> origEdges, Class criterion) {
        int i, j;
        int n = origEdges.size();
        Edge edge;
        // we're going to reorder the edges in order of traversal
        ArrayList<Boolean> done = new ArrayList(n);
        int nDone = 0;
        for (int k = 0; k < n; k++) {
            done.add( false);
        }
        ArrayList<Edge> newEdges = new ArrayList();

        i = 0;
        edge = origEdges.get(i);
        newEdges.add(edge);
        _edgeOrientations.add(LR.LEFT);
        ICoord firstPoint = (criterion == Vertex.class) ? edge.get_leftVertex() : edge.get_leftSite();
        ICoord lastPoint = (criterion == Vertex.class) ? edge.get_rightVertex() : edge.get_rightSite();

        if (firstPoint == Vertex.VERTEX_AT_INFINITY || lastPoint == Vertex.VERTEX_AT_INFINITY) {
            return new ArrayList();
        }

        done.set(i, true);
        ++nDone;

        while (nDone < n) {
            for (i = 1; i < n; ++i) {
                if (done.get(i)) {
                    continue;
                }
                edge = origEdges.get(i);
                ICoord leftPoint = (criterion == Vertex.class) ? edge.get_leftVertex() : edge.get_leftSite();
                ICoord rightPoint = (criterion == Vertex.class) ? edge.get_rightVertex() : edge.get_rightSite();
                if (leftPoint == Vertex.VERTEX_AT_INFINITY || rightPoint == Vertex.VERTEX_AT_INFINITY) {
                    return new ArrayList();
                }
                if (leftPoint == lastPoint) {
                    lastPoint = rightPoint;
                    _edgeOrientations.add(LR.LEFT);
                    newEdges.add(edge);
                    done.set(i, true);
                } else if (rightPoint == firstPoint) {
                    firstPoint = leftPoint;
                    _edgeOrientations.add(0, LR.LEFT);
                    newEdges.add(0, edge);
                    done.set(i, true);
                } else if (leftPoint == firstPoint) {
                    firstPoint = rightPoint;
                    _edgeOrientations.add(0, LR.RIGHT);
                    newEdges.add(0, edge);

                    done.set(i, true);
                } else if (rightPoint == lastPoint) {
                    lastPoint = leftPoint;
                    _edgeOrientations.add(LR.RIGHT);
                    newEdges.add(edge);
                    done.set(i, true);
                }
                if (done.get(i)) {
                    ++nDone;
                }
            }
        }

        return newEdges;
    }
}