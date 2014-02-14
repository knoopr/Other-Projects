#include <stdlib.h>
#include "Queue.h"
#include "Linked_list.h"
#include <stdio.h>


struct A_Queue
{
    List * list;
};

Queue * Create_queue()
{
    Queue * new_Queue;
    new_Queue = malloc(sizeof(Queue));
    new_Queue->list = Create_list();
    return new_Queue;
}

void Destroy_queue(Queue * the_Queue, void (* Destroy_data) (void *))
{
    if (the_Queue != NULL)
    { 
        int result_Value;
        
        if (the_Queue->list != NULL)
            result_Value = Destroy_list(the_Queue->list);
        
        if(result_Value == 0)
        {
            Wipe_list(the_Queue->list, Destroy_data);
            Destroy_list(the_Queue->list);
        }
            
        free(the_Queue);
    }
}

void Enqueue(Queue * the_Queue, void * the_Data)
{
    if(the_Queue != NULL && the_Queue->list != NULL)
        Add_back(the_Queue->list, the_Data);
}

void * Dequeue(Queue * the_Queue)
{
    void * the_Data;  
    if (the_Queue != NULL)
    {
        the_Data = Get_front(the_Queue->list);
        return the_Data;
    }
    return NULL;
}