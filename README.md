# BearMaps
BearMaps implements a web API that returns the shortest route between 2 locations of interest in Berkeley, CA.

I implemented the backend of this project which required:
- the recursive construction of a quadtree that contains various images of Berkeley at various levels of zoom, and returns the appropriate set of images to the user based on their 2 locations of interest
- parsing real XML data to generate a graph of Berkeley based on locations as nodes and streets as edges
- implemented an A* search algorithm to return the shortest route between two locations in better time than Djikstra's algorithm

A screenshot from the application:

https://ibb.co/mzJLJb
