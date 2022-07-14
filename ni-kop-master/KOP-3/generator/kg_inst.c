#include <stdlib.h>
#include <stdio.h>              /* printf */

#include "kg_inst.h"
int  inst_is_dec (inst_t* inst) { return inst->id < 0; }
/*-----------------------------------------------------------------------------*/
int inst_write (inst_t* inst, FILE* out) {
    unsigned i;
    if (inst->id != INST_NONE) fprintf (out, "%d ",inst->id);   /* write an anonymous instance */
    fprintf (out, "%u %u", inst->n, inst->capacity);
    if (inst_is_dec(inst)) fprintf (out, " %u",inst->required);
    for (i=0; i<inst->n; i++) fprintf (out, " %u %u", inst->weights[i], inst->costs[i]);
    fprintf (out, "\n");
    return 1;
}

/*-----------------------------------------------------------------------------*/
int inst_read (inst_t* inst, FILE* in) {
    unsigned i;
    fscanf (in, "%d%u%u",&(inst->id),&(inst->n), &(inst->capacity));
    if (inst_is_dec(inst)) fscanf (in, "%u",&(inst->required));
    if (feof(in)) return 0;
    inst_alloc(inst, inst->n);
    for (i=0; i<inst->n; i++) {
        fscanf(in, "%u%u",&(inst->weights[i]), &(inst->costs[i]));
    }
    return 1;
}
/*-----------------------------------------------------------------------------*/
int inst_read_anon (inst_t* inst, FILE* in) {
    unsigned i;
    inst->id = INST_NONE;
    fscanf (in, "%u%u", &(inst->n), &(inst->capacity));
    if (inst_is_dec(inst)) fscanf (in, "%u",&(inst->required));
    if (feof(in)) return 0;
    inst_alloc(inst, inst->n);
    for (i=0; i<inst->n; i++) {
        fscanf(in, "%u%u",&(inst->weights[i]), &(inst->costs[i]));
    }
    return 1;
}

int inst_empty (inst_t* inst) {
    inst->weights = NULL;
    inst->costs   = NULL;
    inst->required = 0;
    inst->n = 0;
    inst->id = 0;
    inst->capacity=0;
    return 1;
}

int inst_alloc (inst_t* inst, unsigned n) {
    inst->weights = malloc(n * sizeof(inst_weight_t));
    if (!inst->weights) return 0;
    inst->costs   = malloc(n * sizeof(inst_cost_t));
    if (!inst->costs) return 0;
    return 1;
}

int inst_realloc (inst_t* inst, unsigned n) {
    inst->weights = realloc(inst->weights, n * sizeof(inst_weight_t));
    if (!inst->weights) return 0;
    inst->costs   = realloc(inst->costs, n * sizeof(inst_cost_t));
    if (!inst->costs) return 0;
    return 1;
}


int inst_create (inst_t* inst, unsigned n, inst_id_t id) {
    if (!inst_alloc (inst, n)) return 0;
    inst->n = n;
    inst->id = id;
    inst->capacity=0;
    inst->required = 0;
    return 1;
}

int  inst_copy   (inst_t* src, inst_t* dest) {    /* dest must be freed before */
    unsigned i;

    if (!inst_alloc (dest, src->n)) return 0;
    dest->n = src->n;
    dest->id = src->id;
    dest->capacity=src->capacity;
    dest->required = src->required;
    for (i=0; i<src->n; i++) {
        dest->weights[i] = src->weights[i];
        dest->costs[i]   = src->costs[i];
    }
    return 1;
}

void inst_permute(inst_t* src, inst_t* dest, index_t* index) { /* dest already allocated */
    unsigned i;
    for (i=0; i<src->n; i++) {
        dest->weights[i] = src->weights[index[i]];
        dest->costs[i]   = src->costs[index[i]];
    }
}

void inst_free (inst_t* inst) {
    free(inst->weights); inst->weights = NULL;
    free(inst->costs);   inst->costs   = NULL;
}


