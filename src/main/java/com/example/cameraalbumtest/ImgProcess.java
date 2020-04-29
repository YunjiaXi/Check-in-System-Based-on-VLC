package com.example.cameraalbumtest;
//
//import org.apache.commons.math3.fitting.PolynomialCurveFitter;
//import org.apache.commons.math3.fitting.WeightedObservedPoints;
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.Rect;
//import org.opencv.core.Scalar;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static java.lang.Math.pow;
//import static java.lang.Math.sqrt;
//
//public class ImgProcess {
//    private int bitWidth;
//    private String res = "";
//    /*
//     * @param data Y of YUV
//     * @return bits 01
//     */
//
//
//    public ImgProcess(byte[] data, int height, int width, int scanFreq, int LEDFreq){
//        Process(data, height, width, scanFreq, LEDFreq);
//    }
//
//    public String Process(byte[] data, int height, int width, int scanFreq, int LEDFreq){
//
//        this.bitWidth = scanFreq * width / LEDFreq;
//        ArrayList<Byte> valid = FindVaildLine(data, width, height);
//        int length = valid.size() / width;
//        byte[] Equalized = HistEqualize(valid, width, length);
//
//        Mat img = new Mat(length, width, CvType.CV_8UC1);
//        img.put(0, 0, Equalized);
//        Mat HPF = HPFfilter(img);
////
////        return Judge(HPF);
//        return " ";
//    }
//
//    private ArrayList<Byte> FindVaildLine(byte[] data, int width, int height){
//        int i, j;
//        ArrayList<Byte> valid = new ArrayList<>();
//        for (i = 0; i < height ; i++)
//        {
//            int sum = 0;
//            for (j = 0; j < width; j++)
//            {
//                if (data[j + i * width] == (byte)0xFF)
//                    break;
//                sum += data[j + i * width] & 0xFF;
//            }
//            if (j == width)
//            {
//                if (sum / (float)width >= 1.0)
//                {
//                    for (j = 0; j < width; j++)
//                    {
//                        valid.add(data[j + i * width]);
//                    }
//                }
//            }
//        }
//        return valid;
//    }
//
//    private byte[] HistEqualize(ArrayList<Byte> valid, int width, int length){
//        int i, j;
//        int[] histogram = new int[256];
//        for (i = 0 ; i < length; i++)
//            for (j = 0; j < width; j++)
//            {
//                int grey = valid.get(j + i * width) & 0xff;
//                histogram[grey]++;
//            }
//        double[] prSum = new double[256];
//        prSum[0] =  (double) histogram[0] / width * length;
//        for (i = 1; i < 256; i++)
//        {
//            prSum[i] = prSum[i - 1] + (double)histogram[i] / width * length;
//        }
//
//        byte[] Equalized = new byte[length * width];
//        for (i = 0; i < length; i++) {
//            for (j = 0; j < width; j++) {
//                Equalized[j + i * width] = (byte) (255 * prSum[valid.get(j + i * width) & 0xff]);
//            }
//        }
//        return Equalized;
//    }
//
//    private Mat HPFfilter(Mat img)
//    {
//        //Optimal Size
//        Mat padded = new Mat();
//        int addPixelRows = Core.getOptimalDFTSize(img.rows());
//        int addPixelCols = Core.getOptimalDFTSize(img.cols());
//        Core.copyMakeBorder(img, padded, 0, addPixelRows - img.rows(), 0, addPixelCols - img.cols(), Core.BORDER_CONSTANT, Scalar.all(0));
//        padded.convertTo(padded, CvType.CV_32F);
//        List<Mat> planes = new ArrayList<Mat>();
//        Mat complexImage = new Mat();
//        planes.add(padded);
//        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
//        //dft
//        Core.merge(planes, complexImage);
//        Core.dft(complexImage, complexImage);
//        //Set HPF
//        Mat mag = new Mat(complexImage, new Rect(0, 0, complexImage.cols() & -2, complexImage.rows() & -2));
//        int n = 2;
//        double D0 = 5.0;
//        //shift
//        int cx = mag.cols() / 2;
//        int cy = mag.rows() / 2;
//        Mat tmp = new Mat();
//        Mat q0 = new Mat(mag, new Rect(0, 0, cx , cy));
//        Mat q1 = new Mat(mag, new Rect(cx, 0, cx, cy));
//        Mat q2 = new Mat(mag, new Rect(0, cy, cx, cy));
//        Mat q3 = new Mat(mag, new Rect(cx, cy, cx, cy));
//        q0.copyTo(tmp);
//        q3.copyTo(q0);
//        tmp.copyTo(q3);
//        q1.copyTo(tmp);
//        q2.copyTo(q1);
//        tmp.copyTo(q2);
//        //HPF
//        double h;
//        for (int y = 0; y < mag.rows(); y++)
//        {
//            int j = 0;
//            for (int x = 0; x < mag.cols(); x++)
//            {
//                double d = sqrt(pow((y - cy), 2) + pow((x - cx), 2));
//                if (d == 0)
//                    h = 0.0;
//                else
//                    h = 1.0 / (1.0 + pow((D0 / d), 2 * n));
//                mag.put(y, x + j, mag.get(y, x)[0] * h);
//                mag.put(y, x + j + 1, mag.get(y, x + j + 1)[0] * h);
//                j += 1;
//            }
//        }
//        //inverse shift
//        q0.copyTo(tmp);
//        q3.copyTo(q0);
//        tmp.copyTo(q3);
//        q1.copyTo(tmp);
//        q2.copyTo(q1);
//        tmp.copyTo(q2);
//        //idft
//        Mat invDFT = new Mat();
//        Core.idft(mag, invDFT, Core.DFT_SCALE | Core.DFT_REAL_OUTPUT, 0);
//        Mat BluredImage = new Mat();
//        invDFT.convertTo(BluredImage, CvType.CV_8U);
//        return new Mat(BluredImage, new Rect(0, 0, img.cols(), img.rows()));
//    }
//
//    private String Judge(Mat HPF) {
//        int[] num = new int[HPF.cols()];
//        for (int i = 0; i < HPF.rows(); i++)
//        {
//            byte[] ToSovled = new byte[HPF.cols()];
//            HPF.get(i, 0, ToSovled);
//            final WeightedObservedPoints obs = new WeightedObservedPoints();
//            for (int j = 0; j < HPF.cols(); j++)
//            {
//                obs.add(j + 1, ToSovled[j]);
//            }
//            final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
//
//            final double[] coeff = fitter.fit(obs.toList());
//
//            for (int j = 0; j < HPF.cols(); j++)
//            {
//                double fitted = coeff[0] + coeff[1] * (j+1) + coeff[2] * pow(j + 1, 2);
//                if (fitted < ToSovled[j])
//                    num[j]++;
//            }
//        }
//
//        char[] bits = new char[HPF.cols()];
//        for (int j = 0; j < HPF.cols(); j++)
//        {
//            if (num[j] > HPF.rows() / 2)
//                bits[j] = '1';
//            else
//                bits[j] = '0';
//        }
//
//        String s = String.copyValueOf(bits);
//        String rep = new String(new char[this.bitWidth]);
//        int ones, begin = s.indexOf(rep) % this.bitWidth;
//        String res = "";
//        for (int i = 0; begin + i * this.bitWidth < HPF.cols(); i++ )
//        {
//            ones = 0;
//            for (int j = begin + i * this.bitWidth; j < begin + (i + 1) * this.bitWidth; j++ )
//            {
//                ones += bits[j] - '0';
//            }
//            res += ones > this.bitWidth / 2 ? '1' : '0';
//        }
//
//        this.res = res;
//        return res;
//    }
//
//    public boolean judge(String inputStr){
//        int count = 0, index = 0;
//        while ((index = this.res.indexOf(inputStr, index)) != -1) {
//            index = index + this.bitWidth;
//            count++;
//        }
//        return count >= this.res.length()/ (this.bitWidth * 2);
//    }
//}
//
//
//


//package com.example.cameraalbumtest;

import android.os.Environment;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

public class ImgProcess{
//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // 得保证先执行该语句，用于加载库，才能调用其他操作库的语句，
//    }
    private Mat processedImg = new Mat();
    private int row, col, bitWidth, validRNum;
    private String res = "";
    public ImgProcess(byte[] yv21, int row, int col, int scanFreq, int LEDfrq){ //width=col height=row
        // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        this.col = col;
        this.row = row;
        System.out.println("col "+ col +" row "+ row);
        this.bitWidth = scanFreq * col / LEDfrq;
//        System.out.println("yv21： " + Arrays.toString(yv21));
        this.yv21ToValidGrey(yv21, col, row);
        this.decide(this.validRNum, col);
    }
    public void yv21ToValidGrey(byte[] yv21, int col, int row){
        this.col = col;
        this.row = row;
        byte[] aCol = new byte[row];
        byte[][] img = new byte[col][row];
        int value, sum;
        boolean isOver;
        this.validRNum = 0;
        for (int j = 0; j < col; j++){
            isOver = false;
            sum = 0;
            for(int i = 0; i < row; i++){
                value = yv21[i*row+j]>=0 ? yv21[i*row+j] : 256 + yv21[i*row+j];
                if (value == 255){
                    isOver = true;
                    System.out.println("overflow!");
                    break;
                }

                sum += value;
                aCol[i] = (byte) (value - 128);
                // System.out.print(yv21[i*row+j]);
                // System.out.print(value);
                // System.out.println(aRow[j]);
            }
            if (!isOver && sum / row >= 1){
                img[this.validRNum] = aCol.clone();
                this.validRNum++;
            }
        }
        System.out.println( "valid num: "+ this.validRNum);
//        System.out.println( "img");
        for (byte[] x:img){
//            System.out.println(Arrays.toString(x));
        }

        Mat initGrey = new Mat(this.validRNum, col, CvType.CV_8SC1), grey = new Mat();
        Mat equalizedGrey = new Mat();
        Mat kernel = new Mat(3, 3, CvType.CV_32FC1);
        double[] data = {0, -1, 0, -1, 5, -1, 0, -1, 0};
        kernel.put(0, 0, data);
        for (int i = 0; i < this.validRNum; i++){
            initGrey.put(i, 0, img[i]);
        }
        initGrey.convertTo(grey, CvType.CV_8UC1, 1, 128);
        String filenamesrcGray = Environment.getExternalStorageDirectory()+"/image/gray.jpg";
        Imgcodecs.imwrite(filenamesrcGray, initGrey);
        Imgproc.equalizeHist(grey, equalizedGrey);
        // Imgproc.equalizeHist(grey, this.processedImg);
        Imgproc.filter2D(equalizedGrey, this.processedImg, -1, kernel);
//        this.processedImg = equalizedGrey;
    }


    public void decide(int row, int col){
        double[] rowData = new double[col];
        double[] colIdx = new double[col];
        double[] value;
        for(int i=0; i<col; i++){
            colIdx [i] = i;
        }
        // int[][] pred = new int[row][col];
        int[] sum = new int[col];
        char[] finalPred = new char[col];
        System.out.println("img data");
        for(int i=0; i < row; i++){
            for(int j = 0; j<col; j++){
                value = this.processedImg.get(i, j);
                rowData[j] = value[0];
            }
            // this.processedImg.get(i, 0, rowData);
//            System.out.println(Arrays.toString(rowData));
            LeastSquare ls = new LeastSquare(rowData, colIdx, 2);
            for(int j = 0; j<col; j++){
                // pred[i][j] = ls.fit(rowData[j]) > rowData[i]? 0 : 1;
                // sum[j] += pred[i][j];
                sum[j] += ls.fit(rowData[j]) > rowData[i] ? 0: 1;
            }
        }
        for(int j=0; j<col; j++){
            finalPred[j] = sum[j] >= row/2 ? '1' : '0';
        }
        System.out.println("final pred");
        System.out.println(Arrays.toString(finalPred));
        String s = String.copyValueOf(finalPred);
        String rep = new String(new char[this.bitWidth]).replace("\0", "0");
        System.out.println("rep");
        System.out.println(rep);
        int zeros, begin = s.indexOf(rep) % this.bitWidth==-1? 0: s.indexOf(rep) % this.bitWidth;
        System.out.println("begin  " + begin);
        for(int i = 0; begin + i * this.bitWidth < col; i ++){
            zeros = 0;
            for(int j=i;j<i+this.bitWidth; j++){
                zeros += finalPred[j];
            }
            this.res += (zeros >= this.bitWidth/2? '0':'1');
        }
        System.out.println("res");
        System.out.println(this.res);
    }
    public boolean judge(String inputStr){
        int count = 0, index = 0;
        while ((index = this.res.indexOf(inputStr, index)) != -1) {
            index = index + 1;
            count++;
        }
        return count >= this.res.length()/ (this.bitWidth * 2);
    }

}



class LeastSquare {
    private double[] x;
    private double[] y;
    private double[] weight;
    private int n;
    private double[] coefficient;

    /**
     * Constructor method.
     *
     * @param x Array of x
     * @param y Array of y
     * @param n The order of polynomial
     */
    public LeastSquare(double[] x, double[] y, int n) {
        if (x == null || y == null || x.length < 2 || x.length != y.length
                || n < 2) {
            throw new IllegalArgumentException(
                    "IllegalArgumentException occurred.");
        }
        this.x = x;
        this.y = y;
        this.n = n;
        weight = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            weight[i] = 1;
        }
        compute();
    }

    /**
     * Constructor method.
     *
     * @param x      Array of x
     * @param y      Array of y
     * @param weight Array of weight
     * @param n      The order of polynomial
     */
    public LeastSquare(double[] x, double[] y, double[] weight, int n) {
        if (x == null || y == null || weight == null || x.length < 2
                || x.length != y.length || x.length != weight.length || n < 2) {
            throw new IllegalArgumentException(
                    "IllegalArgumentException occurred.");
        }
        this.x = x;
        this.y = y;
        this.n = n;
        this.weight = weight;
        compute();
    }

    /**
     * Get coefficient of polynomial.
     *
     * @return coefficient of polynomial
     */
    public double[] getCoefficient() {
        return coefficient;
    }

    /**
     * Used to calculate value by given x.
     *
     * @param x x
     * @return y
     */
    public double fit(double x) {
        if (coefficient == null) {
            return 0;
        }
        double sum = 0;
        for (int i = 0; i < coefficient.length; i++) {
            sum += Math.pow(x, i) * coefficient[i];
        }
        return sum;
    }

    /**
     * Use Newton's method to solve equation.
     *
     * @param y y
     * @return x
     */
    public double solve(double y) {
        return solve(y, 1.0d);
    }

    /**
     * Use Newton's method to solve equation.
     *
     * @param y      y
     * @param startX The start point of x
     * @return x
     */
    public double solve(double y, double startX) {
        final double EPS = 0.0000001d;
        if (coefficient == null) {
            return 0;
        }
        double x1 = 0.0d;
        double x2 = startX;
        do {
            x1 = x2;
            x2 = x1 - (fit(x1) - y) / calcReciprocal(x1);
        } while (Math.abs((x1 - x2)) > EPS);
        return x2;
    }

    /*
     * Calculate the reciprocal of x.
     * @param x x
     * @return the reciprocal of x
     */
    private double calcReciprocal(double x) {
        if (coefficient == null) {
            return 0;
        }
        double sum = 0;
        for (int i = 1; i < coefficient.length; i++) {
            sum += i * Math.pow(x, i - 1) * coefficient[i];
        }
        return sum;
    }

    /*
     * This method is used to calculate each elements of augmented matrix.
     */
    private void compute() {
        if (x == null || y == null || x.length <= 1 || x.length != y.length
                || x.length < n || n < 2) {
            return;
        }
        double[] s = new double[(n - 1) * 2 + 1];
        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < x.length; j++) {
                s[i] += Math.pow(x[j], i) * weight[j];
            }
        }
        double[] b = new double[n];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < x.length; j++) {
                b[i] += Math.pow(x[j], i) * y[j] * weight[j];
            }
        }
        double[][] a = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = s[i + j];
            }
        }

        // Now we need to calculate each coefficients of augmented matrix
        coefficient = calcLinearEquation(a, b);
    }

    /*
     * Calculate linear equation.
     * The matrix equation is like this: Ax=B
     * @param a two-dimensional array
     * @param b one-dimensional array
     * @return x, one-dimensional array
     */
    private double[] calcLinearEquation(double[][] a, double[] b) {
        if (a == null || b == null || a.length == 0 || a.length != b.length) {
            return null;
        }

        for (double[] x : a) {
            if (x == null || x.length != a.length)
                return null;
        }

        int len = a.length - 1;
        double[] result = new double[a.length];

        if (len == 0) {
            result[0] = b[0] / a[0][0];
            return result;
        }

        double[][] aa = new double[len][len];
        double[] bb = new double[len];
        int posx = -1, posy = -1;
        for (int i = 0; i <= len; i++) {
            for (int j = 0; j <= len; j++)
                if (a[i][j] != 0.0d) {
                    posy = j;
                    break;
                }
            if (posy != -1) {
                posx = i;
                break;
            }
        }
        if (posx == -1) {
            return null;
        }

        int count = 0;
        for (int i = 0; i <= len; i++) {
            if (i == posx) {
                continue;
            }
            bb[count] = b[i] * a[posx][posy] - b[posx] * a[i][posy];
            int count2 = 0;
            for (int j = 0; j <= len; j++) {
                if (j == posy) {
                    continue;
                }
                aa[count][count2] = a[i][j] * a[posx][posy] - a[posx][j] * a[i][posy];
                count2++;
            }
            count++;
        }

        // Calculate sub linear equation
        double[] result2 = calcLinearEquation(aa, bb);

        // After sub linear calculation, calculate the current coefficient
        double sum = b[posx];
        count = 0;
        for (int i = 0; i <= len; i++) {
            if (i == posy) {
                continue;
            }
            sum -= a[posx][i] * result2[count];
            result[i] = result2[count];
            count++;
        }
        result[posy] = sum / a[posx][posy];
        return result;
    }
}

// public static void main(String[] args) {
//     LeastSquare eastSquareMethod = new LeastSquare(
//             new double[]{
//                     2, 14, 20, 25, 26, 34,
//                     47, 87, 165, 265, 365, 465,
//                     565, 665
//             },
//             new double[]{
//                     0.7 * 2 + 20 + 0.4,
//                     0.7 * 14 + 20 + 0.5,
//                     0.7 * 20 + 20 + 3.4,
//                     0.7 * 25 + 20 + 5.8,
//                     0.7 * 26 + 20 + 8.27,
//                     0.7 * 34 + 20 + 0.4,

//                     0.7 * 47 + 20 + 0.1,
//                     0.7 * 87 + 20,
//                     0.7 * 165 + 20,
//                     0.7 * 265 + 20,
//                     0.7 * 365 + 20,
//                     0.7 * 465 + 20,

//                     0.7 * 565 + 20,
//                     0.7 * 665 + 20
//             },
//             2);

//     double[] coefficients = eastSquareMethod.getCoefficient();
//     for (double c : coefficients) {
//         System.out.println(c);
//     }

//     // 测试
//     System.out.println(eastSquareMethod.fit(4));
// }


