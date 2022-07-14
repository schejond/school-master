BINDIR=~/bin
LDLIBS+=-lm
CC=gcc ${DBG} -O ${TRACE} -Wall -Wpedantic
#
# kg2:     Knapsack instance generator
# kg_perm: random permutation of an instance
# 


PROGS=kg2 kg_perm 

all: ${PROGS}

kg2: kg2.o kg_inst.o kg_rngctrl.o xoshiro256plus.o

kg_perm: kg_perm.o kg_rngctrl.o kg_inst.o xoshiro256plus.o

clean:
	- rm *.o *.log ${PROGS}
	
install:
	cp ${PROGS} ${BINDIR}

kg2.o::        kg_inst.h kg_rngctrl.h xoshiro256plus.h
kg_perm.o::    kg_inst.h kg_rngctrl.h xoshiro256plus.h

kg_inst.o::    kg_inst.h
kg_rngctrl.o:: kg_rngctrl.h xoshiro256plus.h
xoshiro256plus.o:: xoshiro256plus.h

