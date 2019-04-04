//////////////////////////////////
/////// OS MINI PROJECT 1 ////////
//////////////////////////////////


//// Author: Junlan, Sachin and Ausaf 


#include <iostream>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>

using namespace std;

#define MIN_PID 300
#define MAX_PID 5000

int threadVar = 0;
pthread_mutex_t mutex1;

bitset<4701> pidArr;


int assignBitset(void)// Allocates bitset values
{

    pidArr.reset();
    return 1;

}

// Assign a pid to the new process
int assignPid(void)                                  
{
    for(int j =0; j <= MAX_PID-MIN_PID; j++)
    {
        if(!pidArr.test(j))
        {
            pidArr.set(j);
            return (j+300);

        }
    }

    return -1;
}

// Release pid of the process
void releasePid(int pid)                          
{
    pidArr.reset(pid - MIN_PID);
}

void * threadCall(void* voidA)             
{
	// assign pid
    int ret = assignPid();
    while (threadVar < 100)
    {
        
		pthread_mutex_lock(&mutex1);// mutex locked
        if (threadVar >= 100)
        {
            pthread_mutex_unlock(&mutex1);
            break;
        }
        
        threadVar++;                    
        usleep(100);
        cout<<"\n "<<threadVar;
		
        pthread_mutex_unlock(&mutex1);// mutex unlocked
    }
    usleep(5);

    releasePid(ret);// release pid
}


int main()
{
    int i = 0;
    cout << "Thread creation begins!" << endl;
	
	// creating 100 threads
    pthread_t thread[100];
    cout<<"100 threads created successfully!" << endl;
	cout<<"Every thread will have 'threadVar' incremented by 1 with a delay of 100ms in each execution" << endl;
	
    usleep(3000);       
    
    for(i = 0; i < 100; i++)
    {
        pthread_mutex_init(&mutex1, NULL);
        pthread_create(&thread[i], NULL, threadCall, NULL);
        threadCall(NULL);
    }
    
    for(i = 0; i < 100; i++)
    {
        pthread_join(thread[i], NULL);
        pthread_mutex_destroy(&mutex1);
    }
    
    return 0;
}

