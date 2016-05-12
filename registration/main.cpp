#include "../TCP_cpp/TCPServerSimple.h"
#include "opencv2/opencv.hpp" 
#include <iostream>
#include "improc.h"

using namespace std;
using namespace cv;
using namespace registration;

/* args
rectangle width, rectangle height,
robot lane distance from right markers, robot lane length
*/
int main(int argc, char **argv)
{
    TCPServerSimple server;
    //server.waitForConnection();
    Point2d rectangle_size;
    rectangle_size.x = double(atoi(argv[1]));
    rectangle_size.y = double(atoi(argv[2]));
    double robot_lane_dist = double(atoi(argv[3]));
    double robot_lane_length = double(atoi(argv[4]));
    Mat marker_template = imread("marker.png", CV_LOAD_IMAGE_GRAYSCALE);
    marker_template.convertTo(marker_template, CV_32FC1);
    for (image_size row = 0; row < marker_template.rows; row++)
    {
        for (image_size col = 0; col < marker_template.cols; col++)
        {
            if (marker_template.at<float>(row, col) == 0)
                marker_template.at<float>(row, col) = -1;
        }
    }
    const int SCALES = 10;
    const double scale_min = 0.2;
    const double scale_max = 1.0;
    const double scale_step = (scale_max - scale_min) / (SCALES - 1);
    Mat marker_scales[SCALES];
    marker_scales[0] = marker_template;

    for (int i = 1; i < SCALES; i++)
    {
        const double scale_down_factor = scale_max - scale_step * i;
        resize(marker_scales[0], marker_scales[i], Size(0, 0), scale_down_factor, scale_down_factor);
    }

    Mat ball_element = getStructuringElement(MORPH_ELLIPSE, Size(20, 20), Point(-1, -1));
    Mat element = getStructuringElement(MORPH_ELLIPSE, Size(50, 50), Point(-1, -1));
    Point2f ball_pos;
    bool last_ball_pos_valid = false;
    VideoCapture cam(1);
    while (1)
    {
        //Mat original = imread("in.png");
        Mat original;
        cam >> original;
        
        Mat gray;
        cvtColor(original, gray, CV_RGB2GRAY);
        //gray = 255 - gray;

        Mat processed;
        gray.copyTo(processed);

        Mat ball;
        processed.copyTo(ball);

        morphologyEx(processed, processed, MORPH_OPEN, element);
        //dilate(processed, processed, dilationElement);
        subtract(gray, processed, processed);
        threshold(processed, processed, 0, 255, CV_THRESH_BINARY | CV_THRESH_OTSU);
        img<size_t> labels(processed.cols, processed.rows);
        size_t regions_n;
        {
            Mat dilated;
            dilate(processed, dilated, element);
            regions_n = segmentation(dilated, labels);
        }
        Mat processedf;
        processed.convertTo(processedf, CV_32FC1);
        float sums[SCALES];
        pos<image_size> markerPositions[SCALES][4];
        //printf("original size: %dx%d\n", processed.cols, processed.rows);
        for (int i = 0; i < SCALES; i++)
        {
            //printf("scale: %i\n", i);
            Mat corr;
            //printf("calling matchTemplate\n");
            matchTemplate(processedf, marker_scales[i], corr, CV_TM_CCORR_NORMED);
            //printf("matchTemplate retured\n");
            copyMakeBorder(corr, corr, marker_scales[i].rows / 2, 0, marker_scales[i].cols / 2, 0, BORDER_CONSTANT, Scalar(0));
            nullBackground<float>(corr, labels);
            //imwrite("corr.png", corr * 255);
            //threshold(out, out, 0.0, 1.0, CV_THRESH_BINARY | CV_THRESH_OTSU);
            float maximums[4];
            findMaxNSegments<float>(corr, labels, regions_n, maximums, markerPositions[i], 4);
            sortMarkers(maximums, markerPositions[i]);
            sums[i] = arraySum(maximums, 4);
            //printf("output size: %dx%d, kernel size: %dx%d\n", corr.cols, corr.rows, marker_scales[i].cols, marker_scales[i].rows);
            /*for (int j = 0; j < 4; j++)
            {
                printf("[%d, %d]: %f\n", markerPositions[i][j].col, markerPositions[i][j].row, maximums[j]);
            }*/
            //printf("\n");
            /*namedWindow("Display window", WINDOW_AUTOSIZE);
            imshow("Display window", corr);
            waitKey(0);*/
        }
        bool markersFound = true;
        const int bestFitIndex = arrayMax(sums, SCALES);
        if (sums[bestFitIndex] < 3 * 0.91)
        {
            printf("markers not found\n");
            markersFound = false;
        }
        if (markersFound)
        {
            nullAtMarkers(ball, labels, markerPositions[bestFitIndex]);
            erode(ball, ball, ball_element);
            threshold(ball, ball, 100, 255, CV_THRESH_BINARY);
            img<size_t> ball_labels(ball.cols, ball.rows);
            bool ballPresent = false;
            bool tooManyObjects = false;
            size_t ball_regions_n = segmentation(ball, ball_labels);
            Mat ball_pt(1, 1, CV_64FC3);
            ball_pt.at<Point2d>(0, 0) = Point2d(0, 0);
            switch (ball_regions_n)
            {
            case 1:
                last_ball_pos_valid = false;
                printf("no ball\n");
                break;
            case 2:
                ballPresent = true;
                pos<float> ball_center = segmentCenter(ball_labels, 1);
                ball_pt.at<Point3d>(0, 0).x = ball_center.col;
                ball_pt.at<Point3d>(0, 0).y = ball_center.row;
                break;
            default:
                printf("too many objects in scene\n");
                last_ball_pos_valid = false;
                tooManyObjects = true;
            }
            /*imshow("window", ball);
            waitKey(1);*/
            if (ballPresent && !tooManyObjects)
            {
                Point2f perspectivePoints[4];
                for (int i = 0; i < 4; i++)
                {
                    perspectivePoints[i].x = markerPositions[bestFitIndex][i].col;
                    perspectivePoints[i].y = markerPositions[bestFitIndex][i].row;
                }
                Point2f rectanglePoints[4];
                rectanglePoints[0] = Point2f(0, 0);
                rectanglePoints[1] = Point2f(rectangle_size.x, 0);
                rectanglePoints[2] = Point2f(0, rectangle_size.y);
                rectanglePoints[3] = Point2f(rectangle_size.x, rectangle_size.y);
                Mat transformMatrix = getPerspectiveTransform(perspectivePoints, rectanglePoints);
                Mat ball_pt_transformed;
                transform(ball_pt, ball_pt_transformed, transformMatrix);
                if (last_ball_pos_valid)
                {
                    Point2d motion_vector(ball_pt_transformed.at<Point2f>(0, 0) - ball_pos);
                    double len_sq = motion_vector.x * motion_vector.x + motion_vector.y * motion_vector.y;
                    const double MIN_SPEED = 1.0;
                    if (motion_vector.x > 0.0 && len_sq >= MIN_SPEED * MIN_SPEED)
                    {
                        double result = calcCrossingPoint(ball_pos, ball_pt_transformed.at<Point2d>(0, 0), rectangle_size, robot_lane_dist, robot_lane_length);
                        if (result < 0.0) result = 0.0;
                        if (result > 1.0) result = 1.0;
                        printf("SENDING: %f\n", result);
                        server.sentData(result);
                    }
                }
                ball_pos = ball_pt_transformed.at<Point2f>(0, 0);
                last_ball_pos_valid = true;

                printf("ball at %f %f\n", ball_pt_transformed.at<Point2d>(0, 0).x, ball_pt_transformed.at<Point2d>(0, 0).y);
            }
            /*for (int j = 0; j < 4; j++)
            {
                printf("[%d, %d]\n", markerPositions[bestFitIndex][j].col, markerPositions[bestFitIndex][j].row);
            }*/
            circle(original, Point(markerPositions[bestFitIndex][0].col, markerPositions[bestFitIndex][0].row), 5, Scalar(255, 0, 0), 3);
            circle(original, Point(markerPositions[bestFitIndex][1].col, markerPositions[bestFitIndex][1].row), 5, Scalar(0, 255, 0), 3);
            circle(original, Point(markerPositions[bestFitIndex][2].col, markerPositions[bestFitIndex][2].row), 5, Scalar(0, 0, 255), 3);
            circle(original, Point(markerPositions[bestFitIndex][3].col, markerPositions[bestFitIndex][3].row), 5, Scalar(0, 255, 255), 3);
            if (ballPresent)
                circle(original, Point(ball_pt.at<Point3d>(0, 0).x, ball_pt.at<Point3d>(0, 0).y), 10, Scalar(0, 140, 255), 3);
        }
        imshow("Display window", original);
        waitKey(1);
    }

    return 0;
}