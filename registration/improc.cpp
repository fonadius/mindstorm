#include "improc.h"
#include <memory>
#include <list>

using namespace std;
using namespace cv;

namespace registration {

size_t segmentation(const Mat &im, img<size_t> &labels)
{
    memset(labels.samples, 0, labels.width * labels.height * sizeof(size_t)); // label all pixels as background
    size_t regions = 1;

    for (image_size row = 0; row < im.rows; row++)
    {
        for (image_size col = 0; col < im.cols; col++)
        {
            if (im.at<sample>(row, col) > 0 && labels(col, row) == 0)
            {
                floodFillRegion(im, labels, col, row, regions++);
            }
        }
    }
    return regions;
}

void floodFillRegion(const Mat &im, img<size_t> &labels, image_size col, image_size row, size_t regionLabel)
{
    const double regionValue = im.at<sample>(row, col);
    list<pos<image_size> > found;
    found.push_back(make_pos(col, row));
    while (found.size() != 0)
    {
        const image_size curCol = found.front().col;
        const image_size curRow = found.front().row;
        found.pop_front();
        if (labels(curCol, curRow) == 0)
        {
            labels(curCol, curRow) = regionLabel;
            if (curCol != 0 && im.at<sample>(curRow, curCol - 1) == regionValue)
                found.push_back(make_pos(curCol - 1, curRow)); // left
            if (curCol < im.cols - 1 && im.at<sample>(curRow, curCol + 1) == regionValue)
                found.push_back(make_pos(curCol + 1, curRow)); // right
            if (curRow != 0 && im.at<sample>(curRow - 1, curCol) == regionValue)
                found.push_back(make_pos(curCol, curRow - 1)); // top
            if (curRow < im.rows - 1 && im.at<sample>(curRow + 1, curCol) == regionValue)
                found.push_back(make_pos(curCol, curRow + 1)); // bottom
        }
    }
}

void nullAtMarkers(Mat &im, const img<size_t> &labels, const pos<image_size> marker_positions[4])
{
    for (image_size row = 0; row < im.rows; row++)
    {
        for (image_size col = 0; col < im.cols; col++)
        {
            if (labels(col, row))
            {
                for (int i = 0; i < 4; i++)
                {
                    if (labels(col, row) == labels(marker_positions[i].col, marker_positions[i].row))
                    {
                        im.at<uchar>(row, col) = 0;
                        break;
                    }
                }
            }
        }
    }

}

pos<float> segmentCenter(const img<size_t> &labels, size_t segmentLabel)
{
    pos<float> center = make_pos(0.0f, 0.0f);
    image_size count = 0;
    for (image_size row = 0; row < labels.height; row++)
    {
        for (image_size col = 0; col < labels.width; col++)
        {
            if (labels(col, row) == segmentLabel)
            {
                center.col += col;
                center.row += row;
                count++;
            }
        }
    }
    if (count)
    {
        center.col /= count;
        center.row /= count;
    }
    return center;
}

double calcCrossingPoint(Point2d ball_pos1, Point2d ball_pos2, Point2d rectangle_size, double robot_lane_dist, double robot_lane_length)
{
    Point2d robot_lane_top(rectangle_size.x + robot_lane_dist, -(robot_lane_length - rectangle_size.y) / 2);
    Point2d robot_lane_bottom(rectangle_size.x + robot_lane_dist, (robot_lane_length + rectangle_size.y) / 2);
    Point2d intersection;
    int intersection_result = twoLinesIntersection(ball_pos1, ball_pos2, robot_lane_top, robot_lane_bottom, intersection);
    if (intersection_result != 0)
        return 0.0;
    double ratio = sqrt(
        (sqr(intersection.x - robot_lane_top.x) + sqr(intersection.y - robot_lane_top.y)) /
        (sqr(robot_lane_bottom.x - robot_lane_top.x) + sqr(robot_lane_bottom.y - robot_lane_top.y))
        );
    return 1.0 - ratio;
}

int twoLinesIntersection(Point2d k, Point2d l, Point2d p, Point2d q, Point2d &intersection)
{
    const double a1 = l.y - k.y;
    const double b1 = k.x - l.x;
    const double c1 = a1 * k.x + b1 * k.y;
    const double a2 = q.y - p.y;
    const double b2 = p.x - q.x;
    const double c2 = a2 * p.x + b2 * p.y;

    const double denom = a1 * b2 - b1 * a2;
    if (abs(denom) < 0.00001)
        return -1;
    intersection.x = (c1 * b2 - b1 * c2) / denom;
    intersection.y = (a1 * c2 - c1 * a2) / denom;
    return 0;
}

} // namespace registration