set terminal png size 1024,768
set output "{0}"
set title "mytitle"
set key right bottom
plot "{1}" with linespoints title "data1","{2}" with linespoints title "data2"
