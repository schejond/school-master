#!/bin/bash
for i in {1..200}
do
	./a.out 20 80 200 > 20_80_200_$i.mwcnf
	./a.out 20 200 200 > 20_200_200_$i.mwcnf
	./a.out 20 500 200 > 20_500_200_$i.mwcnf
	./a.out 20 1000 200 > 20_1000_200_$i.mwcnf
	./a.out 20 1500 200 > 20_1500_200_$i.mwcnf
done