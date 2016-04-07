package theperfectfit.sizedoesmatter;

import Structs.FloatPoint;

public class MatrixFunctions {

    //m is x1 and n is n1'
    //put in skewed scale as m and normalized scale with top left origin as n
    public static Jama.Matrix findProjectiveMatrix(FloatPoint[] m, FloatPoint[] n){
        double[][] equations = {
                {m[0].x, m[0].y, 1.0, 0, 0, 0, -1.0*n[0].x*m[0].x, -1.0*n[0].x*m[0].y},
                {0, 0, 0, m[0].x, m[0].y, 1.0, -1.0*n[0].y*m[0].x, -1.0*n[0].y*m[0].y},
                {m[1].x, m[1].y, 1.0, 0, 0, 0, -1.0*n[1].x*m[1].x, -1.0*n[1].x*m[1].y},
                {0, 0, 0, m[1].x, m[1].y, 1.0, -1.0*n[1].y*m[1].x, -1.0*n[1].y*m[1].y},
                {m[2].x, m[2].y, 1.0, 0, 0, 0, -1.0*n[2].x*m[2].x, -1.0*n[2].x*m[2].y},
                {0, 0, 0, m[2].x, m[2].y, 1.0, -1.0*n[2].y*m[2].x, -1.0*n[2].y*m[2].y},
                {m[3].x, m[3].y, 1.0, 0, 0, 0, -1.0*n[3].x*m[3].x, -1.0*n[3].x*m[3].y},
                {0, 0, 0, m[3].x, m[3].y, 1.0, -1.0*n[3].y*m[3].x, -1.0*n[3].y*m[3].y}};
        Jama.Matrix leftSide = new Jama.Matrix(equations);
        double[][] knownValues = {
                {n[0].x},{n[0].y},
                {n[1].x},{n[1].y},
                {n[2].x},{n[2].y},
                {n[3].x},{n[3].y}};

        Jama.Matrix rightSide = new Jama.Matrix(knownValues);

        Jama.Matrix P = leftSide.solve(rightSide);
        double[][] tempTransform = {
                {P.get(0, 0),P.get(1, 0),P.get(2, 0)},
                {P.get(3, 0),P.get(4, 0),P.get(5, 0)},
                {P.get(6, 0),P.get(7, 0),1.0}};

        return new Jama.Matrix(tempTransform);
    }

    //transform one point
    public static FloatPoint transformPoint(FloatPoint p, Jama.Matrix m) {
        double[][] p1Temp = {{p.x},{p.y},{1.0}};
        Jama.Matrix p1Matrix = m.times(new Jama.Matrix(p1Temp));
        return new FloatPoint(p1Matrix.get(0,0)/p1Matrix.get(2,0), p1Matrix.get(1,0)/p1Matrix.get(2,0));
    }

    //transform all 4 target points
    public static FloatPoint[] transformPoints(FloatPoint ScaleSize, FloatPoint[] SkewedScale, FloatPoint[] ObjectPoints) {
        //TODO: add check when scale is not vertical (possibly boolean parameter)
        FloatPoint[] NormalizedScale = {new FloatPoint(SkewedScale[0].x,SkewedScale[0].y),
                new FloatPoint(SkewedScale[0].x+ScaleSize.y,SkewedScale[0].y),
                new FloatPoint(SkewedScale[0].x+ScaleSize.y,SkewedScale[0].y+ScaleSize.x),
                new FloatPoint(SkewedScale[0].x,SkewedScale[0].y+ScaleSize.x)};

        FloatPoint[] transformedPoints = new FloatPoint[4];
        for(int i=0; i<4; i++)
            transformedPoints[i] = transformPoint(ObjectPoints[i], findProjectiveMatrix(SkewedScale, NormalizedScale));

        return transformedPoints;
    }

    public static float distance(FloatPoint begin, FloatPoint end) {
        return (float) Math.sqrt(Math.pow(begin.x-end.x,2) + Math.pow(begin.y-end.y,2));
    }

    public static float[] calculateSides(FloatPoint[] corners) {
        //Print out estimated dimensions
        float[] returner = new float[4];
        FloatPoint beginPoint = corners[0];
        for(int i=1; i<4; i++) {
            FloatPoint endPoint = corners[i];
            returner[i-1] = MatrixFunctions.distance(beginPoint, endPoint);
            beginPoint = endPoint;
        }
        returner[3] = MatrixFunctions.distance(corners[0], corners[3]);
        return returner;
    }

}

