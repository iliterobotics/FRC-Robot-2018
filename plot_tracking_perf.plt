set key autotitle columnhead
set datafile separator ","
set xrange [-200:200]
set yrange [-200:200]

plot 'ODOMETRY.csv' using 1:2, \
	 'TRAJECTORY.csv' using 1:2