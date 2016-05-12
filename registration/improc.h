#ifndef REGISTRATION_IMPROC_H
#define REGISTRATION_IMPROC_H

#include "opencv2/opencv.hpp"
#include "types.h"

namespace registration {

const int GRAYLEVEL = 256;

/**
 * @return number of regions including background
 */
size_t segmentation(const cv::Mat &im, img<size_t> &labels);

void floodFillRegion(const cv::Mat &im, img<size_t> &labels, image_size col, image_size row, size_t regionLabel);

void findMarkers(const img<size_t> &labels, pos<float> markers[4]);

template <typename intensity_t, typename coords_t>
void sortMarkers(intensity_t values[4], pos<coords_t> markers[4])
{
    { // sort vertically
        coords_t y_coords[4];
        for (int i = 0; i < 4; i++)
            y_coords[i] = markers[i].row;
        size_t maxIndex = arrayMax(y_coords, 4);
        if (maxIndex < 3)
        {
            swap(markers[maxIndex], markers[3]);
            swap(values[maxIndex], values[3]);
            y_coords[maxIndex] = y_coords[3];
            y_coords[3] = 0;
        }
        else
        {
            y_coords[maxIndex] = 0;
        }
        maxIndex = arrayMax(y_coords, 4);
        if (maxIndex < 2)
        {
            swap(markers[maxIndex], markers[2]);
            swap(values[maxIndex], values[2]);
            y_coords[maxIndex] = y_coords[2];
        }
    }
    { // sort horizontally
        if (markers[1].col < markers[0].col)
        {
            swap(markers[0], markers[1]);
            swap(values[0], values[2]);
        }
        if (markers[3].col < markers[2].col)
        {
            swap(markers[2], markers[3]);
            swap(values[2], values[3]);
        }
    }
}

/**
 * @return index of max item
 */
template <typename T>
size_t arrayMax(const T *array, size_t arraySize)
{
    size_t index = 0;
    for (size_t i = 1; i < arraySize; i++)
    {
        if (array[i] > array[index]) index = i;
    }
    return index;
}

/**
* @return sum of first n items in array
*/
template <typename T>
T arraySum(const T *array, size_t n)
{
    T sum = 0;
    for (size_t i = 0; i < n; i++)
        sum += array[i];
    return sum;
}

template <typename T>
void findMaxNSegments(const cv::Mat &im, const img<size_t> &labels, size_t labels_n, T *maximums, pos<image_size> *positions, int n_to_find)
{
    T *_max = new T[labels_n];
    pos<image_size> *_pos = new pos<image_size>[labels_n];
    for (int i = 0; i < labels_n; i++)
    {
        _max[i] = 0;
        _pos[i] = make_pos(0ull, 0ull);
    }
    for (image_size row = 0; row < im.rows; row++)
    {
        for (image_size col = 0; col < im.cols; col++)
        {
            const size_t label = labels(col, row);
            const T val = im.at<T>(row, col);
            if (val > _max[label])
            {
                _max[label] = val;
                _pos[label] = make_pos(col, row);
            }
        }
    }
    for (int i = 0; i < n_to_find; i++)
    {
        const size_t maxIndex = arrayMax(_max, labels_n);
        maximums[i] = _max[maxIndex];
        positions[i] = _pos[maxIndex];
        _max[maxIndex] = 0;
    }
    delete[] _max;
    delete[] _pos;
}

template <typename T>
void nullBackground(cv::Mat &im, const img<size_t> &labels)
{
    for (image_size row = 0; row < im.rows; row++)
    {
        for (image_size col = 0; col < im.cols; col++)
        {
            if (labels(col, row) == 0)
                im.at<T>(row, col) = T(0);
        }
    }
}

void nullAtMarkers(cv::Mat &im, const img<size_t> &labels, const pos<image_size> marker_positions[4]);

pos<float> segmentCenter(const img<size_t> &labels, size_t segmentLabel);

double calcCrossingPoint(cv::Point2d ball_pos, cv::Point2d motion_vector, cv::Point2d rectangle_size, double robot_lane_dist, double robot_lane_length);

int twoLinesIntersection(cv::Point2d k, cv::Point2d l, cv::Point2d p, cv::Point2d q, cv::Point2d &intersection);

template <typename T>
T sqr(T n) { return n * n; }

} // namespace registration

#endif // REGISTRATION_IMPROC_H