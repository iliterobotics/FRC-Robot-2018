set key autotitle columnhead
set datafile separator ","

plot 'ACCEL.csv' using 1:2 with linespoints, '' using 1:3 with linespoints, '' using 1:4 with linespoints, '' using 1:5 with linespoints