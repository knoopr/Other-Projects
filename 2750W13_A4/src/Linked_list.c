#include <stdlib.h>
#include <ctype.h>
#include <stdio.h>
#include "Linked_list.h"

typedef struct ListNode{ 
	void * data;
	struct ListNode * next;
} node;

struct ListHead{
    node * head;
};




/*____________________________________________________________________________*/
node * Create_node(void * the_Data)
{
    node * new_Node;
    new_Node = malloc(sizeof(node));
    new_Node->data = the_Data;
    new_Node->next = NULL;
    return new_Node;   
}
List * Create_list()
{
    List *new_List;
    new_List = malloc(sizeof(List));
    new_List->head = NULL;   
    return new_List;     
}
int Destroy_list(List * the_List)
{
    if(the_List != NULL)
    {
        if (the_List->head == NULL)
        {
            free(the_List);
            return 1;
        }
    }
    return 0;
}
/*____________________________________________________________________________*/





/*____________________________________________________________________________*/
void Add_front (List *the_List, void * the_Data)
{
    node * temp_Node;
    node * new_Node;
    
    if(the_List != NULL)
    {
        temp_Node = the_List->head;
        new_Node = Create_node(the_Data);
        
        new_Node->next = temp_Node;
        the_List->head = new_Node;
        
    }
}
void Add_back (List *the_List, void * the_Data)
{
    node * temp_Node;
    node * new_Node;
    if(the_List != NULL)
    {
        temp_Node = the_List->head;
        new_Node = Create_node(the_Data);
        
        if(temp_Node != NULL)
        {
            while(temp_Node->next != NULL){
                temp_Node = temp_Node->next;
            }
            temp_Node->next = new_Node;
        }else
            the_List->head = new_Node;
    }
    
}
/*____________________________________________________________________________*/





/*____________________________________________________________________________*/
void * Get_front(List *the_List){
    void * the_Data;
    
    if(the_List != NULL)
    {
        the_Data = Get_node(the_List,1);
        return the_Data;
    }else
        return NULL;
}

void * Get_node(List * the_List, int the_Position)
{
    node * next_Node, * prev_Node;
    void * the_Data;
    int i;
    
    prev_Node = the_List->head;
    
    
    if(prev_Node != NULL)
    {
        if(the_Position == 1)
        {
            next_Node = prev_Node->next;
            the_List->head = next_Node;
            the_Data = prev_Node->data;
            free(prev_Node);
            return the_Data;
        }
        else
        {
            next_Node = prev_Node;
            for(i=1;i<the_Position;i++)
            {
                prev_Node = next_Node;
                next_Node = prev_Node->next;
            }
            the_Data = next_Node->data;
            prev_Node->next = next_Node->next;
            free(next_Node);
            return the_Data;
        }
    }
    else
        return NULL;
}

void * Get_back(List * the_List)
{
    node * temp_Node;
    void * the_Data;
    
    if(the_List->head != NULL)
    {
        temp_Node = the_List->head;
        while(temp_Node->next != NULL)
            temp_Node = temp_Node->next;
        if(the_List->head == temp_Node)
            the_List->head = NULL;
        the_Data = temp_Node->data;
        return the_Data;
    }
    else
        return NULL;
}
/*____________________________________________________________________________*/


int Check_list(List *the_List, void * the_Data, int (*Compare_data)(void *, void*))
{
    node * temp_Node;
    int position;
 
    position = 0;
    if (the_List != NULL){
        temp_Node= the_List->head;
        
        if (temp_Node != NULL)
        {
            if (Compare_data(temp_Node->data , the_Data) == 0)
                return 1;
            else
            {
                while(temp_Node->next != NULL)
                {
                    position ++;
                    if (Compare_data(temp_Node->data , the_Data) == 0){
                        return position;
                    }
                    temp_Node = temp_Node->next;
                }
            }
        }
    }
    return -1;
}


void Wipe_list(List * the_List,void (* Destroy_data)(void *))
{
    node * next_Node;
    node * prev_Node;
    
    prev_Node = the_List->head;
    next_Node = the_List->head;
    
    while (next_Node != NULL) 
    {
        prev_Node = next_Node;
        next_Node = prev_Node->next;
        
        Destroy_data(prev_Node->data);
        
        free(prev_Node);
    }
    free(the_List);  
}