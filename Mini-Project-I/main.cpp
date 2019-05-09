//////////////////////////////////
/////// OS MINI PROJECT 1 ////////
//////////////////////////////////


//// Author: Junlan, Sachin and Ausaf 


#include <iostream>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <bitset>

using namespace std;

#define MIN_PID 300
#define MAX_PID 5000

int threadVar = 0;
pthread_mutex_t mutex1;

bitset<4701> pidArr;

int allocate_map(void)                                  //allocates bitmap values to the data structure
{

    pidArr.reset();
    return 1;

}

int allocate_pid(void)                                  //allocates a pid to the new process
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

void release_pid(int pid)                               //releases pid
{
    pidArr.reset(pid - MIN_PID);
}

/* below function executes such that every thread only increments the threadVar by 1. Hence the output is numbers from 1 to 100 printed corresponding to each thread's execution.
 The thread increments the value of threadVar by 1 and exits. Then the next thread increments by 1 again and exits. Every execution consists of a lock and unlock. */

void * threadCall(void* voidA)                          //function called by the created thread
{
    int ret = allocate_pid();       //allocates a pid
    while (threadVar < 100)
    {
        pthread_mutex_lock(&mutex1);     //mutex lock occurs
        if (threadVar >= 100)
        {
            pthread_mutex_unlock(&mutex1);
            break;
        }

        threadVar++;                    //threadVar increments at least once
        usleep(100);
        cout<<"\n "<<threadVar;
        pthread_mutex_unlock(&mutex1);      //mutex now unlocked
    }
    usleep(5);
    release_pid(ret);           //pid released
}

int main()
{
    int i =0;
    cout << "creating threads \n";
    pthread_t thread[100];
    cout<<"\n 100 threads created. Every thread will print the value of variable 'threadVar' and increment it by 1 with a delay of 100ms each process execution";
    usleep(3000);        //delay only so that the above can be read in output screen before execution of the rest of the code

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
