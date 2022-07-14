#!/bin/bash

for x in {1..10}
do
	for i in {6,7,12}
	do
		for c in {2,3,4}
		do
			for t in {5,10,15,20}
			do
				qrun 20c $c pdp_long ./mpi/mpi\_$i\_$c\_$t.sh
			done
		done
	done
done
