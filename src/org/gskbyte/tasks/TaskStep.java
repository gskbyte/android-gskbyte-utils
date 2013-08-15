package org.gskbyte.tasks;

import android.os.AsyncTask;

final class TaskStep
{

private final QueuedTaskExecutor taskCentral;
private final int indexInTaskCentral;
private final Task[] tasks;
private final boolean[] taskResults;
private int finishedTasks = 0;
private boolean totalSuccess = true;
private boolean executed = false;

TaskStep(QueuedTaskExecutor taskCentral, int indexInTaskCentral, Task[] tasks)
{
    this.taskCentral = taskCentral;
    this.indexInTaskCentral = indexInTaskCentral;
    
    this.tasks = tasks;
    for(int i=0; i<tasks.length; ++i) {
        Task sr = tasks[i];
        sr.step = this;
        sr.indexInStep = i;
    }
    this.taskResults = new boolean[tasks.length];
}

void taskFinishedWithResult(int taskIndex, boolean result)
{
    taskResults[taskIndex] = result;
    ++finishedTasks;
    totalSuccess = totalSuccess && result;
    
    if(finishedTasks == taskResults.length) { // all my threads have finished        
        taskCentral.stepFinished(indexInTaskCentral, totalSuccess);
    }
}

void executeTasks()
{
    if(executed)
        throw new IllegalArgumentException("Step #"+indexInTaskCentral+" already runned");
    executed = true;
    for(Task sr : tasks) {
        sr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

}
