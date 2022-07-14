#!/bin/bash

for x in {1..10}
do
	for i in {6,7,12}
	do
   		qrun 20c 1 seq$i ./seq/seq_$i.sh
	done
done
