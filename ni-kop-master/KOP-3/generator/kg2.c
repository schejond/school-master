/* Nov 28 2019 missing default value in pars corrected J Schmidt */
#include <stdlib.h>             /* strtol */
#include <stdio.h>              /* printf */
#include <string.h>             /* strcmp */
#include <math.h>               /* isnan etc. */
#ifdef _MSC_VER
#include "getopt.h"
#undef max
#undef min
#else
#include <unistd.h>             /* getopt */
#endif
#include "kg_inst.h"
#include "kg_rngctrl.h"
/*-----------------------------------------------------------------------------*/
char synopsis[] = "knapgen <options>\n"
"\t-I <initial id>\t\tdefaults to 0 \n"
"\t-n <total things #> \n"
"\t-N <instances #>\n"
"\t-m <the ratio of max knapsack capacity to total weight> | <min ratio>,<max ratio>\n"
"\n"
"\tWeights control\n"
"\t-w bal | light | heavy           weight distribution - default bal\n"
"\t-W <max weight>\n"
"\t-k <exponent (real)>\n"
"\n"
"\tCost control\n"
"\t-C <max cost>\n"
"\t-c uni | corr | strong           cost distribution/style - default uni\n"
"\n"
"\tRNG control\n"
"\t-r <long seed> | time | <file>   seed from a number (64bit), time, file\n"
"\t-R <hex status> | <file>         rng state from string 0xaa,0xbb,0xcc,0xdd\n"
"\t                                 (4 64-bit numbers)\n"
"\t-s <file>                        state to file at the beginning\n"
"\t-S <file>                        state to file at the end\n";
/*-----------------------------------------------------------------------------*/
/* global parametrs                                                            */

#define U_NONE 0xFFFFFFFF

typedef enum { w_bal, w_light, w_heavy, w_none } w_ctrl_t;
typedef enum { c_uni, c_corr, c_strong, c_none } c_ctrl_t;

typedef struct {
    unsigned I;        /* current instance id */
    unsigned n;        /* # of things */
    unsigned N;        /* # of instances */
    double   mlow, mhigh;   /* capacity to total weight */
    
    w_ctrl_t      w_ctrl;   /* weight distribution */
    c_ctrl_t      c_ctrl;   /* cost construction */
    inst_weight_t W;        /* max weight */
    inst_cost_t   C;        /* max cost */
    double        k;        /* distribution exponent */
} pars_t;
/*   I  n       N       mlow mhigh  w_ctrl c_ctrl W       C         k    */
/* { 1, U_NONE, U_NONE, 0.8, 0.8,   w_bal, c_uni, U_NONE, U_NONE,  1.0}; */
/* --------------------------------------------------------------------------- */
/*  option decoding strings                                                    */

#define KEYLEN_MAX 16
#define TIME_KEY "time"

char sentinel[KEYLEN_MAX+1];
const char* w_keys [] = {"bal", "light", "heavy", sentinel};
const char* c_keys [] = {"uni", "corr", "strong", sentinel};
/*-----------------------------------------------------------------------------*/
/*  distribution function for light/heavy things                               */
/*-----------------------------------------------------------------------------*/
double pw (double arg, void* par) {
    double ex = *(double*)par;
    return pow (arg, ex);
}
/*-----------------------------------------------------------------------------*/
int inst_gen (pars_t* pars, inst_t* inst) {
    unsigned i;
    double relw, relc;      /* generated weight and cost in the (0,1) range */
    double disturb = 0.1;
    inst_weight_t total=0;

    inst->n = pars-> n;
    inst->id = pars->I;
    
    for (i=0; i<pars->n; i++) {
        /* generate weights */
        switch (pars->w_ctrl) {
        case w_bal:   relw = rng_next_double(); break;
        case w_light: relw = 1.0 - rng_next_dist(&pw, &(pars->k)); break;
        case w_heavy: relw = rng_next_dist(&pw, &(pars->k)); break;
        default:      relw = rng_next_double(); break;
        }
        inst->weights[i] = rng_to_range (1, pars->W, relw);
        total += inst->weights[i];
	    /* generate costs */
        switch (pars->c_ctrl) {
        case c_uni:    relc = rng_next_double(); break;
        case c_corr:   do {
                           relc = (rng_next_double()-0.5) * disturb * 2.0;    /* random excursion */
                           relc = (1.0-relc) * relw + relc;                   /* from relw */
                       } while (relc < 0.0);
                       break;
        case c_strong: relc = (1.0-disturb) * relw + disturb; break;
        default: relc = rng_next_double(); break;
        }
        inst->costs[i] = rng_to_range (1, pars->C, relc);
    }
    inst->capacity = rng_next_range (total*pars->mlow, total*pars->mhigh);   /* pars->m * total; */
    return 1;
}

/*-----------------------------------------------------------------------------*/
#define DEFAULT_SEED 1
int main (int argc, char** argv) {

    int opt, rtn, err=0;
    char *     epf;                              /* end-of-numeric-string from strtol etc. */
    unsigned   instno;                           /* current instance number */
    
    pars_t pars = { 1, U_NONE, U_NONE, 0.8, 0.8,   w_bal, c_uni, U_NONE, U_NONE,  1.0}; 
    inst_t inst;

    while ((opt = getopt(argc, argv, "aI:n:N:m:w:W:k:c:C:r:R:s:S:h")) != -1) {
         switch (opt) {
         case 'I': pars.I = strtoul (optarg, &epf, 0);
                   if (*epf != 0) { fprintf (stderr, "%s: the arg to -I should be numeric\n", argv[0]); err++; }
                   break;                       /* id=0 also means anonymous instances */
                   
         case 'a': pars.I = INST_NONE;          /* anonymous instances */
                   break;

         case 'n': pars.n = strtoul (optarg, &epf, 0);
                   if (*epf != 0) { fprintf (stderr, "%s: the arg to -n should be numeric\n", argv[0]); err++; }
                   break;

         case 'N': pars.N = strtoul (optarg, &epf, 0);
                   if (*epf != 0) { fprintf (stderr, "%s: the arg to -N should be numeric\n", argv[0]); err++; }
                   break;

         case 'm': pars.mlow = pars.mhigh = strtod (optarg, &epf);
                   if (*epf == ',') pars.mhigh = strtod (epf+1, &epf);
                   if (*epf != 0) { fprintf (stderr, "%s: the arg to -m should be a number or a comma-separated pair of numbers\n", argv[0]); err++; }
                   break;
                   
         case 'w': pars.w_ctrl=0; memset (sentinel, 0, KEYLEN_MAX); strncpy (sentinel, optarg, KEYLEN_MAX);
                   while (strcmp (w_keys[pars.w_ctrl], optarg) != 0) pars.w_ctrl++;
                   if (w_keys[pars.w_ctrl] == sentinel) { fprintf (stderr, "%s: %s: unknown weight distribution\n", argv[0], optarg); err++; }
                   break;

         case 'W': pars.W = strtoul (optarg, &epf, 0);
                   if (*epf != 0) { fprintf (stderr, "%s: the arg to -W should be numeric\n", argv[0]); err++; }
                   break;

         case 'c': pars.c_ctrl=0; memset (sentinel, 0, KEYLEN_MAX);  strncpy (sentinel, optarg, KEYLEN_MAX);
                   while (strcmp (c_keys[pars.c_ctrl], optarg) != 0) pars.c_ctrl++;
                   if (c_keys[pars.c_ctrl] == sentinel) { fprintf (stderr, "%s: %s: unknown cost distribution\n", argv[0], optarg); err++; }
                   break;

         case 'C': pars.C = strtoul (optarg, &epf, 0);
                   if (*epf != 0) { fprintf (stderr, "%s: the arg to -C should be numeric\n", argv[0]); err++; }
                   break;

         case 'k': pars.k = strtod (optarg, &epf);
                   if (*epf != 0) { fprintf (stderr, "%s: the arg to -k should be numeric\n", argv[0]); err++; }
                   break;

         case 'r': 
         case 'R': 
	     case 's':
         case 'S': if (!rng_options (opt, optarg, argv[0])) err++;
                   break;
         default: fprintf (stderr, synopsis); return 0;
         }
    }
    if (pars.n == U_NONE) { fprintf(stderr, "%s: -n must be given\n", argv[0]); err++; }
    if (pars.N == U_NONE) { fprintf(stderr, "%s: -N must be given\n", argv[0]); err++; }
    if (pars.W == U_NONE) { fprintf(stderr, "%s: -W must be given\n", argv[0]); err++; }
    if (pars.C == U_NONE) { fprintf(stderr, "%s: -C must be given\n", argv[0]); err++; }
    
    if (err) return 1;
    
    /* ----------------- RNG controls ---------------------------------- */
    rng_apply_options (argv[0]);

    /* rng_state_write(stderr, &rng_status); */
    /* ----------------------------------------------------------------- */
    
    rtn = inst_create (&inst, pars.n, pars.I);
    if (!rtn) {fprintf(stderr, "%s: out of memory\n", argv[0]); return 1; }
    
    for (instno=0; instno < pars.N; instno++) {
        inst_gen (&pars, &inst);
        inst_write (&inst, stdout);
        if (pars.I != 0) pars.I++;
    }

    rng_end_options (argv[0]);
    return 0;
}

