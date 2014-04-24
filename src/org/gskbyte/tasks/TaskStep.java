package org.gskbyte.tasks;

import java.util.ArrayList;
import java.util.List;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

final class TaskStep
{

private final QueuedTaskExecutor taskCentral;
private final int indexInTaskCentral;
private final Task[] tasks;
private final boolean[] taskResults;
private int finishedTasks = 0;
private boolean totalSuccess = true;
private boolean executed = false;
private List<Exception> capturedExceptions = null;

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

void taskFinishedWithResult(int taskIndex, boolean result, Exception exception)
{
    taskResults[taskIndex] = result;
    ++finishedTasks;
    totalSuccess = totalSuccess && result;
    
    if(exception != null) {
        if(capturedExceptions == null)
            capturedExceptions = new ArrayList<Exception>();
        capturedExceptions.add(exception);
    }
    
    if(finishedTasks == taskResults.length) { // all my threads have finished        
        taskCentral.stepFinished(indexInTaskCentral, totalSuccess, capturedExceptions);
    }
}

void executeTasks()
{
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        executeTasksHoneycomb();
    } else {
        executeTasksFroyo();
    }
}

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
private void executeTasksHoneycomb()
{
    if(executed)
        throw new IllegalArgumentException("Step #"+indexInTaskCentral+" already runned");
    executed = true;
    for(Task sr : tasks) {
        sr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

private void executeTasksFroyo()
{
    if(executed)
        throw new IllegalArgumentException("Step #"+indexInTaskCentral+" already runned");
    executed = true;
    for(Task sr : tasks) {
        sr.execute();
    }
}

}
