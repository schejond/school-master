#!/bin/bash
for i in {1..20}
do
	./a.out 10 20 100 > 10_20_100_$i.mwcnf
	./a.out 10 20 500 > 10_20_500_$i.mwcnf
	./a.out 10 20 2000 > 10_20_2000_$i.mwcnf
	
	./a.out 10 80 100 > 10_80_100_$i.mwcnf
	./a.out 10 80 500 > 10_80_500_$i.mwcnf
	./a.out 10 80 2000 > 10_80_2000_$i.mwcnf

	./a.out 10 400 100 > 10_400_100_$i.mwcnf
	./a.out 10 400 500 > 10_400_500_$i.mwcnf
	./a.out 10 400 2000 > 10_400_2000_$i.mwcnf



	./a.out 50 80 100 > 50_80_100_$i.mwcnf
	./a.out 50 80 500 > 50_80_500_$i.mwcnf
	./a.out 50 80 2000 > 50_80_2000_$i.mwcnf

	./a.out 50 400 100 > 50_400_100_$i.mwcnf
	./a.out 50 400 500 > 50_400_500_$i.mwcnf
	./a.out 50 400 2000 > 50_400_2000_$i.mwcnf



	./a.out 200 400 100 > 200_400_100_$i.mwcnf
	./a.out 200 400 500 > 200_400_500_$i.mwcnf
	./a.out 200 400 2000 > 200_400_2000_$i.mwcnf
done