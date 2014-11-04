#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdio.h>
#include "ParameterManager.h"
#include "Queue.h"

struct ParameterList{
    Queue * param_List;
};

struct ParameterManager{
    Boolean param_Assigned;
    int elem_Count;
    param_t param_Type;
    char * param_Name;
    union param_value param_Data;
} ;


/*__________________________________________________________________*/
void List_free(void *the_Data)
{
    char * the_String;
    
    the_String = (char *) the_Data;
    
    if (the_String != NULL)
        free(the_String);
}
/*__________________________________________________________________*/




/*__________________________________________________________________*/
ParameterManager * PM_create(int size)
{
    int i;
    ParameterManager * new_Manager;
    
    if (size < 0) 
        return NULL;
    
    new_Manager = malloc(sizeof(ParameterManager) * size);
    for( i = 0 ; i < size; i ++){
        new_Manager[i].elem_Count = size;
        new_Manager[i].param_Name = NULL;
    }
    return new_Manager;
}
int PM_destroy(ParameterManager *p){
    int i;
    
    if (p != NULL){
        for(i = 0 ; i < p[0].elem_Count; i++)
        {
            if(p[i].param_Name != NULL)
            {
                if (p[i].param_Type == STRING_TYPE && p[i].param_Assigned == true){
                    free(p[i].param_Data.str_val);
                }
                if (p[i].param_Type == LIST_TYPE && p[i].param_Assigned == true){
                    if (p[i].param_Data.list_val->param_List != NULL){
                        Destroy_queue(p[i].param_Data.list_val->param_List, List_free);
                    }
                    free(p[i].param_Data.list_val);
                }
            }
            free(p[i].param_Name);
        }
        free(p);
        return 1;
    }
    return 0;
}
/*__________________________________________________________________*/





/*__________________________________________________________________*/
/*Find's the parameter in the list and if it is already there assigns it the value parse_Value*/
int Add_manager(ParameterManager *p, char parse_Name[100], char parse_Value[500], param_t the_Type)
{
    char * list_Elements, * list_Pointer;
    int i = 0, j = 0;
    
    for(i = 0; i < p[0].elem_Count; i++){
        if(p[i].param_Name != NULL)
        {
            if(strcmp(p[i].param_Name,parse_Name) == 0)
            {
                if(p[i].param_Type != the_Type){
                    if(!(p[i].param_Type = REAL_TYPE && the_Type == INT_TYPE) ){
                        return 0;
                    }
                }
                if(!p[i].param_Assigned == true){
                    switch (p[i].param_Type) {
                        case REAL_TYPE:
                            p[i].param_Data.real_val = atof(parse_Value);
                            p[i].param_Assigned = true;
                            break;
                        case INT_TYPE:
                            p[i].param_Data.int_val = atoi(parse_Value);
                            p[i].param_Assigned = true;
                            break;
                        case BOOLEAN_TYPE:
                            for(j = 0; j < strlen(parse_Value); j++)
                                parse_Value[j] = tolower(parse_Value[j]);
                            
                            if (strcmp(parse_Value,"true") == 0)
                                p[i].param_Data.bool_val = true;
                            else if (strcmp(parse_Value,"false") == 0)
                                p[i].param_Data.bool_val = false;
                            else{
                                return 0;
                            }
                            p[i].param_Assigned = true;
                            break;
                        case LIST_TYPE:
                            j = 0;
                            while (parse_Value[j+1] != '\0') {
                                parse_Value[j] = parse_Value[j+1];
                                j++;
                            }
                            parse_Value[j-1] = '\0';
                            p[i].param_Data.list_val = malloc(sizeof(ParameterList));
                            p[i].param_Data.list_val->param_List = Create_queue();
                            
                            list_Pointer = strtok(parse_Value,",");
                            while (list_Pointer != NULL){
                                list_Elements = malloc(sizeof(char) * strlen(list_Pointer)+1);
                                list_Elements = strcpy(list_Elements,list_Pointer);
                                Enqueue(p[i].param_Data.list_val->param_List, list_Elements);
                                list_Pointer = strtok(NULL,",");
                            }
                            p[i].param_Assigned = true;
                            break;
                        case STRING_TYPE:
                            p[i].param_Data.str_val = malloc(sizeof(char) * strlen(parse_Value)+ 1);
                            strcpy(p[i].param_Data.str_val,parse_Value);
                            p[i].param_Assigned = true;
                            break;
                        default:
                            return 0;
                            break;
                    }
                }
                return 1;
            }
        }
    }
    return 0;
}
param_t Determine_type(char parse_Value[500]){
    char bool_Check [500];
    int i;
    
    strcpy(bool_Check,parse_Value);
    for(i = 0; i < strlen(bool_Check); i++)
        bool_Check[i] = tolower(bool_Check[i]);
    
    
    if (strcmp(bool_Check,"true") == 0 || strcmp(bool_Check,"false") == 0){
        return BOOLEAN_TYPE;
    }
    
    else if(parse_Value[0] == '-' || isdigit(parse_Value[0]))
    {
        int j, dec_Count;
        dec_Count = 0;
        
        for(j = 0; j < strlen(parse_Value); j ++)
        {                        
            if(!isdigit(parse_Value[j]))
            {
                if(parse_Value[j] == '-')
                {
                    if(j != 0)
                        break;
                }
                else if (parse_Value[j] == '.')
                {
                    dec_Count++;
                    if (dec_Count > 1)
                        break;
                }else
                    break;
            }
            if (j == strlen(parse_Value)-1)
            {
                if (dec_Count > 0)
                {
                    return REAL_TYPE;
                }
                else
                {
                    return INT_TYPE;
                }
            }
        }
    }
    else if (parse_Value[0] == '{')
    {
        return LIST_TYPE;
    }
    return STRING_TYPE;
}
/*__________________________________________________________________*/





/*__________________________________________________________________*/
int Clean_input(char * file_String, char comments)
{
    int i;
    
    for(i = 0; i < strlen(file_String);i++)
    {
        if(file_String[i] == comments)
        {
            while (file_String[i] != '\n') {
                if (file_String[i] == '\0')
                    break;
                file_String[i] = ' ';
                i++;
            }
        }
    }
    for(i = 0; file_String[i] != '\0'; i++)
    {
        if(file_String[i] == '\n')
            file_String[i] = ' ';
    }
    for(i = 0; file_String[i] != '\0';i++)
    {        
        /*Loops through and removes all optional comments */
        if(file_String[i] == '[')
        {
            int j, bracket_Level;
            bracket_Level = 1;
            j= i;
            while (bracket_Level > 0) {
                if(file_String[j] == '\n')
                    break;
                if(file_String[j] == ';')
                {
                    free(file_String);
                    return 0;
                }
                if(file_String[j] == '[' && j != i)
                    bracket_Level++;
                if(file_String[j] == ']')
                    bracket_Level--;
                file_String[j] = ' ';
                j++;
            }
        }
        
        /*Difference is used here to reduce on extra non needed looping*/
        if(file_String[i] == ' ')
        {
            int j,difference = 0;
            for(j = i; file_String[j+difference] != '\0'; j++)
            {
                if (file_String[j+difference] ==' '){
                    difference++;
                    j--;
                }
                else
                    file_String[j] = file_String[j+difference];
            }
            file_String[j] = '\0';
            i --;
        }
        
        if(file_String[i] == '\t')
        {
            int j,difference = 0;
            for(j = i; file_String[j+difference] != '\0'; j++)
            {
                if (file_String[j+difference] =='\t'){
                    difference++;
                    j--;
                }
                else
                    file_String[j] = file_String[j+difference];
            }
            file_String[j] = '\0';
            i --;
        }
    }
    return 1;
}
int PM_parseFrom(ParameterManager *p, char * file_String, char comment)
{
    if (file_String != NULL)
    {
        {
            int error_Check;
            error_Check = Clean_input(file_String, comment);
            if (error_Check == 0)
                return 0;
        }
        {
            int i, next_Expected = 0;
            char parse_Name[100], parse_Value[500];
            param_t the_Type;
            
            for (i = 0; i < strlen(file_String);i ++)
            {                
                /*Pre = must be the name, Parse's it then shifts the characters*/
                if(file_String[i] == '=' && next_Expected == 0)
                {
                    int j;   
                    next_Expected = 1;
                    file_String[i] = '\0';
                    strcpy(parse_Name,file_String);
                    file_String[i] = '=';
                    i++;
                    for(j = 0; file_String[i] != '\0';j++,i++)
                        file_String[j] = file_String[i];
                    i = -1;
                    file_String[j] = '\0';
                }             
                /*Between = and ; must be the parse value, Parse's it then shifts the characters*/
                else if (file_String[i] == ';' && next_Expected == 1)
                {
                    int j, return_Value;
                    next_Expected = 0;
                    file_String[i] = '\0';
                    strcpy(parse_Value,file_String);
                    file_String[i] = '=';
                    i++;
                    for(j = 0; file_String[i] != '\0';j++,i++)
                        file_String[j] = file_String[i];
                    file_String[j] = '\0';
                    i = -1;
                    
                    the_Type = Determine_type(parse_Value);
                    return_Value = Add_manager(p, parse_Name, parse_Value, the_Type);
                    if (return_Value == 0){
                        return 0;
                    }
                }
                else if(file_String[i] == '=' && next_Expected == 1)
                {
                    free(file_String);
                    return 0;  
                }
                else if(file_String[i] == ';' && next_Expected == 0)
                {
                    free(file_String);
                    return 0;
                }
            }
        }        
        return 1;
    }
    return 0;
}
/*__________________________________________________________________*/




/*__________________________________________________________________*/
int PM_manage(ParameterManager *p, char *pname, param_t ptype, int required)
{
    int i;
    
    if (p != NULL){
        for(i = 0; i < p[0].elem_Count; i++){
            if (p[i].param_Name != NULL)
            {
                if (strcmp(p[i].param_Name,pname) == 0){
                    return 0;
                }
            }
        }
        i = 0;
        while (p[i].param_Name != NULL){
            i++;
        }
        p[i].param_Name = malloc(sizeof(char)*strlen(pname)+1);
        strcpy(p[i].param_Name, pname);
        p[i].param_Type = ptype;
        p[i].param_Assigned = false;
        return 1;
    }
    return 0;
}
/*__________________________________________________________________*/




/*__________________________________________________________________*/
int PM_hasValue(ParameterManager *p, char *pname)
{
    if (p!= NULL)
    {
        int i;
        
        for (i = 0; i < p[0].elem_Count; i++)
        {
            if (p[i].param_Name != NULL)
            {
                if (strcmp(p[i].param_Name,pname) == 0 && p[i].param_Assigned == true)
                    return 1;
            }
        }
    }
    return 0;
}
union param_value PM_getValue(ParameterManager *p, char *pname)
{
    union param_value undefined;
    
    if (p != NULL){
        int i;
        
        for (i = 0; i < p[0].elem_Count; i++){
            if (p[i].param_Name != NULL){
                if(strcmp(p[i].param_Name,pname) == 0 ){
                    return p[i].param_Data;   
                }
            }
        }
    }
    return undefined;
}
/*__________________________________________________________________*/



/*__________________________________________________________________*/
char * Map_manager(ParameterManager *p, int i){
    
    if (p != NULL){
        if (i <= p[0].elem_Count-1){
            if (p[i].param_Name != NULL){
                return p[i].param_Name;
            }
        }
    }
    return NULL;
}
/*__________________________________________________________________*/



/*__________________________________________________________________*/

char * PL_next(ParameterList *l)
{
    void * the_Data;
    
    if (l != NULL)
    {
        the_Data = Dequeue(l->param_List);
        return (char *) the_Data;
    }
    return NULL;
}
/*__________________________________________________________________*/