#ifndef REGISTRATION_TYPES_H
#define REGISTRATION_TYPES_H

namespace registration {

typedef unsigned char sample;
typedef size_t image_size;

template <typename T>
struct img
{
    const image_size width;
    const image_size height;
    T *samples;

    img(image_size width, image_size height)
        : width(width), height(height)
    {
        samples = new T[width * height];
    }

    virtual ~img() { delete[] samples; }

    T& operator() (image_size col, image_size row)
    {
        return samples[row * width + col];
    }

    const T& operator() (image_size col, image_size row) const
    {
        return samples[row * width + col];
    }
};

template<typename T>
struct pos
{
    T col;
    T row;
};

template <typename T>
pos<T> make_pos(T col, T row)
{
    pos<T> tmp;
    tmp.col = col;
    tmp.row = row;
    return tmp;
}

} // namespace registration

#endif // REGISTRATION_TYPES_H