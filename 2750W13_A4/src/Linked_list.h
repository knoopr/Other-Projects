typedef struct ListHead List;

/*Deals with the node creation and destruction, destroy return's 1 on success 0 if there is still nodes in the list*/
List * Create_list();
int Destroy_list(List * the_List);

/*Add's given data to the list*/
void Add_front (List *the_List, void * the_Data);
void Add_back (List *the_List, void * the_Data);

/*Two function's both related to returning data from the list, one for the front, and one that takes an index number*/
void * Get_front(List *the_List);
void * Get_node(List *the_List, int the_Position);
void * Get_back(List * the_List);

/*Check's the list for the given data using the given compare function and return's the position in the list if found*/
int Check_list(List *the_List, void * the_Data, int (*Compare_data)(void *, void*));

/*Wipe's the list of all data from a provided function*/
void Wipe_list(List * the_List,void (* Destroy_data)(void *));