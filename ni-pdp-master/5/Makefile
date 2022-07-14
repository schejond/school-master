# all after symbol '#' is comment

# === which communication library to use ===
CC	=	mpiCC      # for ethernet and infiniband networks

CFLAGS	=
LIBS	=	-std=c++11 -Wall -pedantic -O3 -funroll-loops -fopenmp

default:	mpi.out

mpi.out:mpi_main.cpp
	$(CC) $(CFLAGS) -o mpi.out mpi_main.cpp $(LIBS)
clean:
	rm mpi.out

