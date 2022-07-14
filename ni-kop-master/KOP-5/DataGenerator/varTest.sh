#!/bin/bash
for i in {1..200}
do
	./a.out 20 400 500 > 20_400_500_$i.mwcnf
	./a.out 80 400 500 > 80_400_500_$i.mwcnf
	./a.out 160 400 500 > 160_400_500_$i.mwcnf
	./a.out 240 400 500 > 240_400_500_$i.mwcnf
done