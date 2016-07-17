package camp.computer.clay.visualization.util;

import android.graphics.PointF;

import java.util.ArrayList;

import camp.computer.clay.visualization.images.BaseImage;
import camp.computer.clay.visualization.arch.Visualization;

public abstract class Geometry {

    public static float calculateDistance(PointF source, PointF target) {
        return calculateDistance (source.x, source.y, target.x, target.y);
    }

    public static float calculateDistance(float x, float y, float x2, float y2) {
        double distanceSquare = Math.pow (x - x2, 2) + Math.pow (y - y2, 2);
        double distance = Math.sqrt (distanceSquare);
        return (float) distance;
    }

    /**
     * Calculates the angle from centerPt to targetPt in degrees.
     * The return should range from [0,360), rotating CLOCKWISE,
     * 0 and 360 degrees represents NORTH,
     * 90 degrees represents EAST, etc...
     *
     * Assumes all points are in the same coordinate space.  If they are not,
     * you will need to call SwingUtilities.convertPointToScreen or equivalent
     * on all arguments before passing them  to this function.
     *
     * @param centerPt   Point we are rotating around.
     * @param targetPt   Point to which we want to calculate the angle, relative to the center point.
     * @return angle in degrees.  This is the angle from centerPt to targetPt.
     */
    public static float calculateRotationAngle(PointF centerPt, PointF targetPt) {

        // calculate the angle theta from the deltaY and deltaX values
        // (atan2 returns radians values from [-PI,PI])
        // 0 currently points EAST.
        // NOTE: By preserving Y and X param order to atan2,  we are expecting
        // a CLOCKWISE angle direction.
        double theta = Math.atan2(targetPt.y - centerPt.y, targetPt.x - centerPt.x);

        // rotate the theta angle clockwise by 90 degrees
        // (this makes 0 point NORTH)
        // NOTE: adding to an angle rotates it clockwise.
        // subtracting would rotate it counter-clockwise
//        theta += Math.PI / 2.0;

        // convert from radians to degrees
        // this will give you an angle from [0->270],[-180,0]
        double angle = Math.toDegrees(theta);

        // convert to positive range [0-360)
        // since we want to prevent negative angles, adjust them now.
        // we can assume that atan2 will not return a negative value
        // greater than one partial rotation
//        if (angle < 0) {
//            angle += 360;
//        }

        return (float) angle;
    }

    /**
     * Calculates coordinates of a point rotated about about another origin point by a given number
     * of degrees.
     *
     * References:
     * - http://www.gamefromscratch.com/post/2012/11/24/GameDev-math-recipes-Rotating-one-point-around-another-point.aspx
     *
     * @return
     */
    public static PointF calculateRotatedPoint(PointF originPoint, float angle, PointF point) {
        return Geometry.calculatePoint(originPoint, angle + Geometry.calculateRotationAngle(originPoint, point), (float) Geometry.calculateDistance(originPoint, point));
    }

    public static PointF calculatePoint(PointF originPoint, float rotation, float distance) {
        PointF point = new PointF();
        point.x = originPoint.x + distance * (float) Math.cos(Math.toRadians(rotation));
        point.y = originPoint.y + distance * (float) Math.sin(Math.toRadians(rotation));
        return point;
    }

    public static PointF calculateMidpoint(PointF source, PointF target) {
        PointF midpoint = new PointF();
        midpoint.x = ((source.x + target.x) / 2.0f);
        midpoint.y = ((source.y + target.y) / 2.0f);
        return midpoint;
    }

    //Compute the dot product AB . AC
    public static double calculateDotProduct(PointF linePointA, PointF linePointB, PointF pointC)
    {
        PointF AB = new PointF();
        PointF BC = new PointF();
        AB.x = linePointB.x - linePointA.x;
        AB.y = linePointB.y - linePointA.y;
        BC.x = pointC.x - linePointB.x;
        BC.y = pointC.y - linePointB.y;
        double dot = AB.x * BC.x + AB.y * BC.y;

        return dot;
    }

    //Compute the cross product AB x AC
    public static double calculateCrossProduct(PointF linePointA, PointF linePointB, PointF pointC)
    {
        PointF AB = new PointF();
        PointF AC = new PointF();
        AB.x = linePointB.x - linePointA.x;
        AB.y = linePointB.y - linePointA.y;
        AC.x = pointC.x - linePointA.x;
        AC.y = pointC.y - linePointA.y;
        double cross = AB.x * AC.y - AB.y * AC.x;

        return cross;
    }

    //Compute the distance from AB to C
    //if isSegment is true, AB is a segment, not a line.
    // References:
    // - http://stackoverflow.com/questions/4438244/how-to-calculate-shortest-2d-distance-between-a-point-and-a-line-segment-in-all
    public static double calculateLineToPointDistance(PointF linePointA, PointF linePointB, PointF pointC, boolean isSegment) {
        double distance = calculateCrossProduct(linePointA, linePointB, pointC) / Geometry.calculateDistance(linePointA, linePointB);
        if (isSegment) {
            double dot1 = calculateDotProduct(linePointA, linePointB, pointC);
            if (dot1 > 0) {
                return Geometry.calculateDistance(linePointB, pointC);
            }

            double dot2 = calculateDotProduct(linePointB, linePointA, pointC);
            if (dot2 > 0) {
                return Geometry.calculateDistance(linePointA, pointC);
            }
        }
        return Math.abs(distance);
    }

    public static PointF calculateCentroidPosition(ArrayList<PointF> points)  {
        PointF centroidPosition = new PointF(0, 0);

        for(PointF point : points) {
            centroidPosition.x += point.x;
            centroidPosition.y += point.y;
        }

        centroidPosition.x /= points.size();
        centroidPosition.y /= points.size();

        return centroidPosition;
    }

    public static Rectangle calculateBoundingBox(ArrayList<PointF> points) {
        float[] boundaryPoints = new float[4]; // left, top, right, bottom
        // TODO: center, width, height

        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;

        for (PointF point: points) {
            if (point.x < minX) {
                minX = point.x;
            }
            if (point.y < minY) {
                minY = point.y;
            }
            if (point.x > maxX) {
                maxX = point.x;
            }
            if (point.y > maxY) {
                maxY = point.y;
            }
        }

        Rectangle rectangle = new Rectangle(minX, minY, maxX, maxY);

        return rectangle;
    }

    public static PointF calculateCenterPosition(ArrayList<PointF> points) {
        float[] boundaryPoints = new float[4]; // left, top, right, bottom
        // TODO: center, width, height

        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;

        for (PointF point: points) {
            if (point.x < minX) {
                minX = point.x;
            }
            if (point.y < minY) {
                minY = point.y;
            }
            if (point.x > maxX) {
                maxX = point.x;
            }
            if (point.y > maxY) {
                maxY = point.y;
            }
        }

        float left = minX;
        float top = minY;
        float right = maxX;
        float bottom = maxY;

        boundaryPoints[0] = left;
        boundaryPoints[1] = top;
        boundaryPoints[2] = right;
        boundaryPoints[3] = bottom;

        // PointF boundingBoxPosition = new PointF(minX + ((right - left) / 2.0f), minY + ((bottom - top) / 2.0f));
        PointF boundingBoxPosition = new PointF(minX + ((right - left) / 2.0f), minY + ((bottom - top) / 2.0f));

        return boundingBoxPosition;
    }

    public static PointF calculateNearestPoint(PointF sourcePoint, ArrayList<PointF> points) {

        // Initialize point
        PointF nearestPoint = points.get(0);
        float nearestDistance = (float) Geometry.calculateDistance(sourcePoint, nearestPoint);

        // Search for the nearest point
        for (PointF point: points) {
            float distance = (float) Geometry.calculateDistance(sourcePoint, point);
            if (distance < nearestDistance) {
                nearestPoint.set(point);
            }
        }

        return nearestPoint;
    }

    /**
     * Computes the convex hull using the "quick hull" algorithm.
     *
     * <strong>References</strong>
     * - Another implementation is <em>GrahamScan</em> (http://algs4.cs.princeton.edu/99hull/GrahamScan.java.html).
     *
     * @param points
     * @return
     */
    public static ArrayList<PointF> computeConvexHull (ArrayList<PointF> points) {

        ArrayList<PointF> convexHull = new ArrayList<>();

        if (points.size() < 3) {
            return (ArrayList) points.clone();
        }

        int minPoint = -1;
        int maxPoint = -1;

        float minX = Integer.MAX_VALUE;
        float maxX = Integer.MIN_VALUE;

        for (int i = 0; i < points.size(); i++) {

            if (points.get(i).x < minX) {
                minX = points.get(i).x;
                minPoint = i;
            }

            if (points.get(i).x > maxX) {
                maxX = points.get(i).x;
                maxPoint = i;
            }
        }

        PointF A = points.get(minPoint);
        PointF B = points.get(maxPoint);

        convexHull.add(A);
        convexHull.add(B);

        points.remove(A);
        points.remove(B);

        ArrayList<PointF> leftSet = new ArrayList<>();
        ArrayList<PointF> rightSet = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            PointF p = points.get(i);
            if (pointLocation(A, B, p) == -1) {
                leftSet.add(p);
            } else if (pointLocation(A, B, p) == 1) {
                rightSet.add(p);
            }
        }

        hullSet(A, B, rightSet, convexHull);
        hullSet(B, A, leftSet, convexHull);

        return convexHull;
    }

    public static void hullSet(PointF A, PointF B, ArrayList<PointF> set, ArrayList<PointF> hull) {
        int insertPosition = hull.indexOf(B);

        if (set.size() == 0) {
            return;
        }

        if (set.size() == 1) {
            PointF p = set.get(0);
            set.remove(p);
            hull.add(insertPosition, p);
            return;
        }

        float dist = Integer.MIN_VALUE;
        int furthestPoint = -1;

        for (int i = 0; i < set.size(); i++) {
            PointF p = set.get(i);
            float distance = distance(A, B, p);
            if (distance > dist) {
                dist = distance;
                furthestPoint = i;
            }
        }

        PointF P = set.get(furthestPoint);
        set.remove(furthestPoint);
        hull.add(insertPosition, P);

        // Determine who's to the left of AP
        ArrayList<PointF> leftSetAP = new ArrayList<PointF>();
        for (int i = 0; i < set.size(); i++)
        {
            PointF M = set.get(i);
            if (pointLocation(A, P, M) == 1)
            {
                leftSetAP.add(M);
            }
        }

        // Determine who's to the left of PB
        ArrayList<PointF> leftSetPB = new ArrayList<>();
        for (int i = 0; i < set.size(); i++) {
            PointF M = set.get(i);
            if (pointLocation(P, B, M) == 1) {
                leftSetPB.add(M);
            }
        }

        hullSet(A, P, leftSetAP, hull);
        hullSet(P, B, leftSetPB, hull);

    }

    public static float distance (PointF A, PointF B, PointF C) {
        float ABx = B.x - A.x;
        float ABy = B.y - A.y;
        float num = ABx * (A.y - C.y) - ABy * (A.x - C.x);
        if (num < 0) {
            num = -num;
        }
        return num;
    }

    public static int pointLocation(PointF A, PointF B, PointF P)
    {
        float cp1 = (B.x - A.x) * (P.y - A.y) - (B.y - A.y) * (P.x - A.x);
        if (cp1 > 0)
            return 1;
        else if (cp1 == 0)
            return 0;
        else
            return -1;
    }

    /**
     * Compute list of points that are separated by a minimal distance. Based on circle packing
     * algorithm.
     *
     * References:
     * - http://graphicdna.blogspot.com/2009/09/2d-circle-packing-algorithm-ported-to-c.html
     * - http://gamedevelopment.tutsplus.com/tutorials/when-worlds-collide-simulating-circle-circle-collisions--gamedev-769
     * - http://mathematica.stackexchange.com/questions/2594/efficient-way-to-generate-random-points-with-a-predefined-lower-bound-on-their-p
     * - http://stackoverflow.com/questions/4847269/circle-separation-distance-nearest-neighbor-problem?lq=1
     * - http://stackoverflow.com/questions/3265986/an-algorithm-to-space-out-overlapping-rectangles
     *
     * @param positions
     * @return
     */
    public static ArrayList<BaseImage> packCircles(ArrayList<BaseImage> positions, float distance, PointF packingCenter) {

        // Sort points based on distance from center
        ArrayList<BaseImage> sortedImages = sortByDistanceToPoint(positions, packingCenter);
        ArrayList<PointF> sortedPositions = Visualization.getPositions(sortedImages);

        float minSeparationSq = distance * distance;

        float iterationCounter = 1000;

        for (int i = 0; i < sortedPositions.size() - 1; i++) {
            for (int j = i + 1; j < sortedPositions.size(); j++) {

                if (i == j) {
                    continue;
                }

                // Vector/Segment connecting a pair of points
                // TODO: Vector2 AB = mCircles[j].mCenter - mCircles[i].mCenter;
                PointF vectorAB = new PointF(
                        sortedPositions.get(j).x - sortedPositions.get(i).x,
                        sortedPositions.get(j).y - sortedPositions.get(i).y
                );

                float r = (sortedImages.get(i).boardWidth / 2.0f) + (sortedImages.get(i).boardWidth / 2.0f);

                // Length squared = (dx * dx) + (dy * dy);
                double vectorABLength = Geometry.calculateDistance(sortedPositions.get(i), sortedPositions.get(j));
                double d = vectorABLength * vectorABLength - minSeparationSq;
                double minSepSq = Math.min(d, minSeparationSq);
                d -= minSepSq;

                if (d < (r * r) - 0.01)
//                if (d < (r * r) - 500)
                {
//                    Log.v("Sort", "r^2 - d = " + ((r * r) - d));
//                    Log.v("Sort", "--");
                    // Normalize (transform into unit vector)
                    // TODO: AB.Normalize();
                    float magnitude = (float) Geometry.calculateDistance(
                            sortedPositions.get(i),
                            sortedPositions.get(j)
                    );
                    // (float) Geometry.calculateDistance(packingCenter, vectorAB);
                    vectorAB.x = vectorAB.x / magnitude;
                    vectorAB.y = vectorAB.y / magnitude;

                    // TODO: AB *= (float)((r - Math.Sqrt(d)) * 0.5f);
                    vectorAB.x *= (float)((r - Math.sqrt(d)) * 0.5f);
                    vectorAB.y *= (float)((r - Math.sqrt(d)) * 0.5f);

//                    if (positions.get(j) != mDraggingCircle)
                    // TODO: positions.get(j).mCenter += AB;
                    sortedPositions.get(j).x += vectorAB.x;
                    sortedPositions.get(j).y += vectorAB.y;
//                    if (positions.get(i) != mDraggingCircle)
                    // TODO: positions.get(i).mCenter -= AB;
                    sortedPositions.get(i).x -= vectorAB.x;
                    sortedPositions.get(i).y -= vectorAB.y;
                }

            }
        }

        float damping = 0.1f / iterationCounter;
        for (int i = 0; i < sortedPositions.size(); i++)
        {
//            if (mCircles[i] != mDraggingCircle)
//            {
            // TODO: Vector2 v = mCircles[i].mCenter - this.mPackingCenter;
            PointF v = new PointF(
                    sortedPositions.get(i).x - packingCenter.x,
                    sortedPositions.get(i).y - packingCenter.y
            );

            // TODO: v *= damping;
            v.x *= damping;
            v.y *= damping;

            // TODO: mCircles[i].mCenter -= v;
            sortedPositions.get(i).x -= v.x;
            sortedPositions.get(i).y -= v.y;

            ((BaseImage) sortedImages.get(i)).setPosition(sortedPositions.get(i));
//            }
        }

        return sortedImages;

    }

    public static ArrayList<BaseImage> sortByDistanceToPoint(ArrayList<BaseImage> positions, PointF point) {

        // Initialize with unsorted list of points
        ArrayList<BaseImage> sortedList = new ArrayList(positions);

        for (int i = 0; i < sortedList.size(); i++) {
            for (int j = 1; j < (sortedList.size() - i); j++) {

                BaseImage p1 = sortedList.get(j - 1);
                BaseImage p2 = sortedList.get(j);

                if (Geometry.calculateDistance(p1.getPosition(), point) > Geometry.calculateDistance(p2.getPosition(), point)) {
                    sortedList.remove(j - 1);
                    sortedList.add(j, p1);
                }

            }
        }

//        String sortedListResult = "";
//        for (Image p: sortedList) {
//            sortedListResult += calculateDistance(p.getPosition(), point) + ", ";
//        }
//        Log.v("Sort", sortedListResult);

        return sortedList;

    }

//    /**
//     * Compute list of points that are separated by a minimal distance. Based on circle packing
//     * algorithm.
//     * @param positions
//     * @return
//     */
//    public static ArrayList<PointF> packCircles(ArrayList<PointF> positions, float distance, PointF packingCenter) {
//
//        // Sort points based on distance from center
//        ArrayList<PointF> sortedPoints = sortByDistanceToPoint(positions, packingCenter);
//
//        float minSeparationSq = distance * distance;
//
//        for (int i = 0; i < sortedPoints.size() - 1; i++) {
//            for (int j = i + 1; j < sortedPoints.size(); j++) {
//
//                if (i == j) {
//                    continue;
//                }
//
//                PointF vectorAB = new PointF();
//                vectorAB.x = sortedPoints.get(j).x - sortedPoints.get(i).x;
//                vectorAB.y = sortedPoints.get(j).y - sortedPoints.get(i).x;
//
//                float radiusSum = distance + distance;
//
//                // Length squared = (dx * dx) + (dy * dy);
//                float d = (float) (Geometry.calculateDistance(vectorAB, packingCenter) * Geometry.calculateDistance(vectorAB, packingCenter)) - minSeparationSq;
//                float minSepSq = Math.min(d, minSeparationSq);
//                d -= minSepSq;
//
//                if (d < (radiusSum * radiusSum) - 0.01 )
//                {
//                    // Normalize (transform into unit vector)
//                    // TODO: AB.Normalize();
//                    float magnitude = (float) Geometry.calculateDistance(packingCenter, vectorAB);
//                    PointF unitVectorAB = new PointF(0, 0);
//                    unitVectorAB.x = vectorAB.x / magnitude;
//                    unitVectorAB.y = vectorAB.y / magnitude;
//
//                    // TODO: AB *= (float)((r - Math.Sqrt(d)) * 0.5f);
//                    unitVectorAB.x *= (float)((radiusSum - Math.sqrt(d)) * 0.5f);
//                    unitVectorAB.y *= (float)((radiusSum - Math.sqrt(d)) * 0.5f);
//
////                    if (positions.get(j) != mDraggingCircle)
//                        // TODO: positions.get(j).mCenter += AB;
//                    sortedPoints.get(j).x += unitVectorAB.x;
//                    sortedPoints.get(j).y += unitVectorAB.y;
////                    if (positions.get(i) != mDraggingCircle)
//                        // TODO: positions.get(i).mCenter -= AB;
//                    sortedPoints.get(i).x -= unitVectorAB.x;
//                    sortedPoints.get(i).y -= unitVectorAB.y;
//                }
//
//            }
//        }
//
//        float iterationCounter = 5;
//        float damping = 0.1f / (float)(iterationCounter);
//        for (int i = 0; i < sortedPoints.size(); i++)
//        {
////            if (mCircles[i] != mDraggingCircle)
////            {
//                // TODO: Vector2 v = mCircles[i].mCenter - this.mPackingCenter;
//                PointF v = new PointF(0, 0);
//                v.x = sortedPoints.get(i).x - packingCenter.x;
//                v.y = sortedPoints.get(i).y - packingCenter.y;
//
//                // TODO: v *= damping;
//                v.x *= damping;
//                v.y *= damping;
//
//                // TODO: mCircles[i].mCenter -= v;
//                sortedPoints.get(i).x -= v.x;
//                sortedPoints.get(i).y -= v.y;
////            }
//        }
//
//        return sortedPoints;
//
//    }
//
//    public static ArrayList<PointF> sortByDistanceToPoint(ArrayList<PointF> positions, PointF point) {
//
//        // Initialize with unsorted list of points
//        ArrayList<PointF> sortedList = new ArrayList(positions);
//
//        for (int i = 0; i < sortedList.size(); i++) {
//            for (int j = i + 1; j < sortedList.size(); j++) {
//
//                PointF p1 = positions.get(i);
//                PointF p2 = positions.get(j);
//
//                if (Geometry.calculateDistance(p1, point) > Geometry.calculateDistance(p2, point)) {
//                    positions.remove(i);
//                    positions.add(i + 1, p1);
//                }
//
//            }
//        }
//
//        String sortedListResult = "";
//        for (PointF p: sortedList) {
//            sortedListResult += calculateDistance(p, point) + ", ";
//        }
//        Log.v("Sort", sortedListResult);
//
//        return sortedList;
//
//    }

    // TODO: Detect if a point falls within a polygon defined by list of points.
    // TODO: (cont'd) - http://alienryderflex.com/polygon/
    // TODO: (cont'd) - http://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon
    // TODO: (cont'd) - http://math.stackexchange.com/questions/193606/detect-when-a-point-belongs-to-a-bounding-box-with-distances?lq=1
    // TODO: (cont'd) - http://math.stackexchange.com/questions/190111/how-to-check-if-a-point-is-inside-a-rectangle

    // TODO: Detect minimum bounding box rotating to fit list of points
    // TODO: (cont'd) - http://gis.stackexchange.com/questions/22895/how-to-find-the-minimum-area-rectangle-for-given-points

    // TODO: Classify shape given a list of points (2D point cloud). (Use this rather than instanceof?)
    // TODO: (cont'd) - http://stat.fsu.edu/~anuj/pdf/papers/Y2012/Su_CSDA_2012_Paper.pdf
    // TODO: (cont'd) - http://homepages.inf.ed.ac.uk/svijayak/publications/mcneill-IJCAI2005.pdf
    // TODO: (cont'd) - http://www.cs.umd.edu/~djacobs/pubs_files/ID-pami-8.pdf
    // TODO: (cont'd) - https://en.wikipedia.org/wiki/Shape_analysis_(digital_geometry)
}
