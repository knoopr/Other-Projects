typedef struct A_Queue Queue;

/* Creates a queue allocate everything in the queue and return's the queue to the caller*/
Queue * Create_queue();

/*Destroys the queue and deletes the data left in the queue using the provided data destruction algorthm*/
void Destroy_queue(Queue * the_Queue, void (* Destroy_data) (void *));

/*If the queue is valid, adds the data to the queue*/
void Enqueue(Queue * the_Queue, void * the_Data);

/*Dequeus the top item in the queue and provides the user with a pointer to it, return's NULL if the queue is empty*/
void * Dequeue(Queue * the_Queue);