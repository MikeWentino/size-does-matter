package theperfectfit.sizedoesmatter;

/**
 * Created by Weldon on 4/1/2016.
 */

import Jama.Matrix;
import Structs.FloatPoint;

public class MatrixFunctions {

    //m is x1 and n is n1'
    //put in skewed scale as m and normalized scale with top left origin as n
    public static Matrix findProjectiveMatrix(FloatPoint[] m, FloatPoint[] n){
        double[][] equations = {
                {m[0].x, m[0].y, 1.0, 0, 0, 0, -1.0*n[0].x*m[0].x, -1.0*n[0].x*m[0].y},
                {0, 0, 0, m[0].x, m[0].y, 1.0, -1.0*n[0].y*m[0].x, -1.0*n[0].y*m[0].y},
                {m[1].x, m[1].y, 1.0, 0, 0, 0, -1.0*n[1].x*m[1].x, -1.0*n[1].x*m[1].y},
                {0, 0, 0, m[1].x, m[1].y, 1.0, -1.0*n[1].y*m[1].x, -1.0*n[1].y*m[1].y},
                {m[2].x, m[2].y, 1.0, 0, 0, 0, -1.0*n[2].x*m[2].x, -1.0*n[2].x*m[2].y},
                {0, 0, 0, m[2].x, m[2].y, 1.0, -1.0*n[2].y*m[2].x, -1.0*n[2].y*m[2].y},
                {m[3].x, m[3].y, 1.0, 0, 0, 0, -1.0*n[3].x*m[3].x, -1.0*n[3].x*m[3].y},
                {0, 0, 0, m[3].x, m[3].y, 1.0, -1.0*n[3].y*m[3].x, -1.0*n[3].y*m[3].y}};
        Matrix leftSide = new Matrix(equations);
        double[][] knownValues = {
                {n[0].x},{n[0].y},
                {n[1].x},{n[1].y},
                {n[2].x},{n[2].y},
                {n[3].x},{n[3].y}};

        Matrix rightSide = new Matrix(knownValues);

        Matrix P = leftSide.solve(rightSide);
        double[][] tempTransform = {
                {P.get(0, 0),P.get(1, 0),P.get(2, 0)},
                {P.get(3, 0),P.get(4, 0),P.get(5, 0)},
                {P.get(6, 0),P.get(7, 0),1.0}};

        Matrix TransformMatrix = new Matrix(tempTransform);
        return TransformMatrix;
    }

    public static void estimate(FloatPoint[] SkewedScale, FloatPoint[] NormalizedScale) {

        Matrix TransformMatrix = findProjectiveMatrix(SkewedScale, NormalizedScale);
        TransformMatrix.print(4,3);

        double[][] p1Temp = {{SkewedScale[0].x},{SkewedScale[0].y},{1.0}};
        Matrix p1Matrix = new Matrix(p1Temp);
        System.out.println("p1(" + NormalizedScale[0].x + ", " + NormalizedScale[0].y+ "):");
        Matrix result1 = TransformMatrix.times(p1Matrix);
        result1.times(1.0/result1.get(2, 0)).print(4,3);

        double[][] p2Temp = {{SkewedScale[1].x},{SkewedScale[1].y},{1.0}};
        Matrix p2Matrix = new Matrix(p2Temp);
        System.out.println("p2(" + NormalizedScale[1].x + ", " + NormalizedScale[1].y+ "):");
        Matrix result2 = TransformMatrix.times(p2Matrix);
        result2.times(1.0/result2.get(2, 0)).print(4,3);

        double[][] p3Temp = {{SkewedScale[2].x},{SkewedScale[2].y},{1.0}};
        Matrix p3Matrix = new Matrix(p3Temp);
        System.out.println("p3(" + NormalizedScale[2].x + ", " + NormalizedScale[2].y+ "):");
        Matrix result3 = TransformMatrix.times(p3Matrix);
        result3.times(1.0/result3.get(2, 0)).print(4,3);

        double[][] p4Temp = {{SkewedScale[3].x},{SkewedScale[3].y},{1.0}};
        Matrix p4Matrix = new Matrix(p4Temp);
        System.out.println("p4(" + NormalizedScale[3].x + ", " + NormalizedScale[3].y+ "):");
        Matrix result4 = TransformMatrix.times(p4Matrix);
        result4.times(1.0/result4.get(2, 0)).print(4,3);
    }

    public static FloatPoint transformPoint(FloatPoint p, Jama.Matrix m) {
        double[][] p1Temp = {{p.x},{p.y},{1.0}};
        Jama.Matrix p1Matrix = m.times(new Jama.Matrix(p1Temp));
        return new FloatPoint(p1Matrix.get(0,0)/p1Matrix.get(2,0), p1Matrix.get(1,0)/p1Matrix.get(2,0));
    }
}
