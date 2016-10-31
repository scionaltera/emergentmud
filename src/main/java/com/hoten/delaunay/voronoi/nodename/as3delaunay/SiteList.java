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
import com.hoten.delaunay.geom.Rectangle;
import java.util.ArrayList;

public final class SiteList implements IDisposable {

    private ArrayList<Site> _sites;
    private int _currentIndex;
    private boolean _sorted;

    public SiteList() {
        _sites = new ArrayList();
        _sorted = false;
    }

    @Override
    public void dispose() {
        if (_sites != null) {
            for (Site site : _sites) {
                site.dispose();
            }
            _sites.clear();
            _sites = null;
        }
    }

    public int push(Site site) {
        _sorted = false;
        _sites.add(site);
        return _sites.size();
    }

    public int get_length() {
        return _sites.size();
    }

    public Site next() {
        if (_sorted == false) {
            throw new Error("SiteList::next():  sites have not been sorted");
        }
        if (_currentIndex < _sites.size()) {
            return _sites.get(_currentIndex++);
        } else {
            return null;
        }
    }

    public Rectangle getSitesBounds() {
        if (_sorted == false) {
            Site.sortSites(_sites);
            _currentIndex = 0;
            _sorted = true;
        }
        double xmin, xmax, ymin, ymax;
        if (_sites.isEmpty()) {
            return new Rectangle(0, 0, 0, 0);
        }
        xmin = Double.MAX_VALUE;
        xmax = Double.MIN_VALUE;
        for (Site site : _sites) {
            if (site.get_x() < xmin) {
                xmin = site.get_x();
            }
            if (site.get_x() > xmax) {
                xmax = site.get_x();
            }
        }
        // here's where we assume that the sites have been sorted on y:
        ymin = _sites.get(0).get_y();
        ymax = _sites.get(_sites.size() - 1).get_y();

        return new Rectangle(xmin, ymin, xmax - xmin, ymax - ymin);
    }

    /*public ArrayList<Color> siteColors(referenceImage:BitmapData = null)
     {
     var colors:Vector.<uint> = new Vector.<uint>();
     for each (var site:Site in _sites)
     {
     colors.push(referenceImage ? referenceImage.getPixel(site.x, site.y) : site.color);
     }
     return colors;
     }*/
    public ArrayList<Point> siteCoords() {
        ArrayList<Point> coords = new ArrayList();
        for (Site site : _sites) {
            coords.add(site.get_coord());
        }
        return coords;
    }

    /**
     *
     * @return the largest circle centered at each site that fits in its region;
     * if the region is infinite, return a circle of radius 0.
     *
     */
    public ArrayList<Circle> circles() {
        ArrayList<Circle> circles = new ArrayList();
        for (Site site : _sites) {
            double radius = 0;
            Edge nearestEdge = site.nearestEdge();

            //!nearestEdge.isPartOfConvexHull() && (radius = nearestEdge.sitesDistance() * 0.5);
            if (!nearestEdge.isPartOfConvexHull()) {
                radius = nearestEdge.sitesDistance() * 0.5;
            }
            circles.add(new Circle(site.get_x(), site.get_y(), radius));
        }
        return circles;
    }

    public ArrayList<ArrayList<Point>> regions(Rectangle plotBounds) {
        ArrayList<ArrayList<Point>> regions = new ArrayList();
        for (Site site : _sites) {
            regions.add(site.region(plotBounds));
        }
        return regions;
    }
    /**
     *
     * @param proximityMap a BitmapData whose regions are filled with the site
     * index values; see PlanePointsCanvas::fillRegions()
     * @param x
     * @param y
     * @return coordinates of nearest Site to (x, y)
     *
     */
    /*public Point nearestSitePoint(proximityMap:BitmapData, double x, double y)
     {
     var index:uint = proximityMap.getPixel(x, y);
     if (index > _sites.length - 1)
     {
     return null;
     }
     return _sites[index].coord;
     }*/
}