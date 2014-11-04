#include <stdio.h>

typedef struct ParameterManager ParameterManager;
typedef struct ParameterList ParameterList;

typedef enum Boolean {true, false} Boolean;
typedef enum param_t {INT_TYPE , REAL_TYPE, BOOLEAN_TYPE, STRING_TYPE, LIST_TYPE, FIELD_TYPE, BUTTON_TYPE} param_t;

union param_value
{
    int           int_val;
    float         real_val;
    Boolean       bool_val;   
    char          *str_val;
    ParameterList *list_val;
};



/*Creates a Paramater Manager of the size passed in */
ParameterManager * PM_create(int size);

/*Destroy's the Paramater Manager passed in */
int PM_destroy(ParameterManager *p);

/*Parses through the file and add's it to the paramater manager, if the paramater has already been managed*/
int PM_parseFrom(ParameterManager *p, char * file_String, char comment);

/*Tell's the paramater manager that a variable of name  pname will be in the stream*/
int PM_manage(ParameterManager *p, char *pname, param_t ptype, int required);

/*Determines if the paramater manager has the value pname, return's 1 if it is available and the parameter has been assigned a value*/
int PM_hasValue(ParameterManager *p, char *pname);

/*Returns the union param_value for the value pname*/
union param_value PM_getValue(ParameterManager *p, char *pname);

/*Used to map the field names*/
char * Map_manager(ParameterManager *p, int i);

/*Return's the next value in the Parameter list or NULL if the list is empty*/
char * PL_next(ParameterList *l);