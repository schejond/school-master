#!/bin/bash
for i in {1..200}
do
	./a.out 20 80 50 > 20_80_50_$i.mwcnf
	./a.out 20 80 500 > 20_80_500_$i.mwcnf
	./a.out 20 80 1000 > 20_80_1000_$i.mwcnf
	./a.out 20 80 1500 > 20_80_1500_$i.mwcnf
	./a.out 20 80 2000 > 20_80_2000_$i.mwcnf
done