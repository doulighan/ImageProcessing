A couple simple image processing filters. Takes an image and creates a radial Sobel Blur output by first applying a Gaussian blur to reduce noise, a grayscale of that blurred image, then a Sobel algorithm. The color of the edge coresponds to its' angle.

The Guassian blur implementation is perfectly accurate but very slow, be warned. The kernal size of the gaussianBlur() function can be lowered within the edgeDetect() function to speed the algorithm up. 

This project was built to understand how a kernal matrix interacts with an image. Hopefully this will be used later
for an attempt at a simple number recognition program, using a neural network.
