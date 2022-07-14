#!/bin/bash


for x in {1..10}
do
	for i in {6,7,12}
	do
		for j in {1,5,10,15,20}
		do
			qrun 20c 1 pdp_serial ./ompDat/ompDat\_$i\_$j.sh
		done
	done
done
