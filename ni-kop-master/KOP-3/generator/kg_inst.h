#ifndef KG_INST_H
#define KG_INST_H
#include <stdio.h>
/* ------------------------------------------------------- */
/* instance representation and manipulation                */

/* types */
typedef unsigned int inst_weight_t;      /* weight repr */
typedef unsigned int inst_cost_t;        /* cost repr   */
typedef          int inst_id_t;          /* decision instances have negative id */
#define INST_NONE 0

typedef struct {
    inst_weight_t* weights;
    inst_cost_t*   costs;
    inst_weight_t  capacity;
    inst_cost_t    required;
    unsigned       n;
    inst_id_t      id;
} inst_t;
typedef unsigned index_t;

#define COST_INF 0xffffffff

/* functions */
int  inst_alloc     (inst_t* inst, unsigned n);
int  inst_realloc   (inst_t* inst, unsigned n);
int  inst_create    (inst_t* inst, unsigned n, inst_id_t id);
int  inst_copy      (inst_t* src, inst_t* dest);    /* dest must be freed before */
int  inst_empty     (inst_t* inst);
void inst_free      (inst_t* inst);
int  inst_write     (inst_t* inst, FILE* out);
int  inst_read      (inst_t* inst, FILE* in);       /* Read instance with id; inst must be freed before */
int  inst_read_anon (inst_t* inst, FILE* in);       /* Read instance without id; inst must be freed before */
int  inst_read      (inst_t* inst, FILE* out);      /* inst must be freed before */
int  inst_is_dec    (inst_t* inst);
void inst_permute   (inst_t* src, inst_t* dest, index_t* index);

static inline int  inst_read_choice (inst_t* inst, FILE* in, int anon) { /* Read instance with/without id; inst must be freed before */
    return anon ? inst_read_anon (inst, in) : inst_read (inst, in);
}




#endif
