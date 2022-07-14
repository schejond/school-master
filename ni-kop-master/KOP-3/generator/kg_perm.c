#include <stdlib.h>             /* strtol */
#include <stdio.h>              /* printf */
#include <string.h>             /* strcmp */
#include <math.h>               /* isnan etc. */
#ifdef _MSC_VER
#include "getopt.h"
#undef max
#undef min
/* gettimeofday replacement here */
#else
#include <unistd.h>             /* getopt */
#include <sys/time.h>           /* gettimeofday */
#endif
#include "kg_inst.h"
#include "kg_rngctrl.h"
/*-----------------------------------------------------------------------------
Permute an instance to obtain another instance
Synopsis: kg_perm <options> < infile > outfile
    -d <inst id delta> no default 
    -N <# of permutations of each instance>  default 1

    RNG control
    -r <long seed> | time | <file>   seed from number, time, file
    -R <hex status> | <file>         status from hex string
    -s <file>                        status to file at start (repeatibility)
    -S <file>                        status to file at end (continuation)
-------------------------------------------------------------------------------*/
/* global parametrs                                                            */

typedef enum { w_bal, w_light, w_heavy, w_none } w_ctrl_t;
typedef enum { c_uni, c_corr, c_strong } c_ctrl_t;

typedef struct {
    unsigned D;        /* instance id delta */
    unsigned N;        /* # of permutations */
} pars_t;

/*-----------------------------------------------------------------------------*/
index_t* index_alloc (unsigned n) {
    unsigned i;
    index_t* index = malloc (n * sizeof(index_t));
    if (index) for (i=0; i<n; i++) index[i] = i;
    return index;
}

/*-----------------------------------------------------------------------------*/
#define U_NONE 0xFFFFFFFF
int main (int argc, char** argv) {

    int opt, rtn, err=0;
    char *     epf;                              /* end-of-numeric-string from strtol etc. */
    unsigned   id;                               /* permutation counter */
    int        anon = 0;

    /* ---------------------- */
    
    pars_t pars = { U_NONE, 1 };  
    inst_t inst_in, inst_out;
    index_t* index = NULL;
        
    while ((opt = getopt(argc, argv, "d:N:r:R:S:s:a")) != -1) {
         switch (opt) {
         case 'd': pars.D = strtoul (optarg, &epf, 0);
                   if (*epf != 0) { fprintf (stderr, "%s: the arg to -d should be numeric\n", argv[0]); err++; }
                   break;

         case 'N': pars.N = strtoul (optarg, &epf, 0);
                   if (*epf != 0) { fprintf (stderr, "%s: the arg to -N should be numeric\n", argv[0]); err++; }
                   break;

         case 'r': 
         case 'R': 
         case 's': 
         case 'S': rng_options (opt, optarg, argv[0]); break;
         case 'a': anon = 1; break;
         default:  break;
         }
    }
    if (pars.D == U_NONE) { fprintf(stderr, "%s: -d must be given\n", argv[0]); err++; }
    if (err) return 1;
    
    /* ------------------ RNG control ----------------------------------- */
    rng_apply_options (argv[0]);

    /* ------------------------------------------------------------------ */
    
    while (inst_read_choice (&inst_in, stdin, anon)) {
        index = index_alloc (inst_in.n);
        rtn = inst_copy (&inst_in, &inst_out);
        if (!rtn) {fprintf(stderr, "%s: out of memory\n", argv[0]); return 1; }
        for (id=1; id <= pars.N; id++) {
            if (!anon) inst_out.id += pars.D; /* instances anonymous */
            rng_permute (index, inst_out.n);
            inst_permute(&inst_in, &inst_out, index);
            inst_write(&inst_out, stdout);
        }
        inst_free (&inst_out);
        inst_free (&inst_in);
        free (index);
    }

    /* ------------------ RNG control ----------------------------------- */
    rng_end_options (argv[0]);

    /* ------------------------------------------------------------------ */

    return 0;
}

