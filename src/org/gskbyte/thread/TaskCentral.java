package org.gskbyte.thread;

import java.util.ArrayList;

import lombok.Getter;

import android.os.AsyncTask;

public class TaskCentral
{

protected final ArrayList<Step> steps = new ArrayList<Step>();

@Getter
protected int executedSteps = 0;
protected Listener listener;

public interface Listener
{
    public void threadCentralFinished(TaskCentral central, int executedSteps, boolean totalSuccessInLastStep);
}

public abstract static class Task
extends AsyncTask<Void, Void, Boolean>
{
private Step step; 
private int indexInStep;

@Override
protected Boolean doInBackground(Void... params)
{
    if(step == null) {
        throw new IllegalStateException("StepTask can't be run outside a " + TaskCentral.class.getSimpleName());
    }
    
    return runInBackground();
}

protected abstract boolean runInBackground();

@Override
protected void onPostExecute(Boolean result)
{
    step.taskFinishedWithResult(indexInStep, result);
}

}

private final class Step
{

final int index;
final boolean requiresTotalSuccessOnPreviousStep;
final Task[] tasks;
final boolean[] taskResults;
int finishedTasks = 0;
boolean totalSuccess = true;
boolean executed = false;

Step(int index, boolean requiresTotalSuccessOnPreviousStep, Task[] tasks)
{
    this.index = index;
    this.requiresTotalSuccessOnPreviousStep = requiresTotalSuccessOnPreviousStep;
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
        TaskCentral.this.stepFinished(index, totalSuccess);
    }
}

void executeTasks()
{
    if(executed)
        throw new IllegalArgumentException("Step #"+index+" already runned");
    executed = true;
    for(Task sr : tasks) {
        sr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

}

public boolean run(Listener listener)
{
    this.listener = listener;
    if(steps.size() > 0) {
        steps.get(0).executeTasks();
    } else {
        postListenerNotification(0, true);
        return true;
    }
    
    return true;
}

public synchronized void addStep(Task ... tasks)
{
    addStep(true, tasks);
}

public synchronized void addStep(boolean requiresTotalSuccessOnPreviousStep, Task ... tasks)
{
    if(tasks.length == 0)
        throw new IllegalArgumentException("You must pass at least one StepRunnable");
    
    Step step = new Step(steps.size(), requiresTotalSuccessOnPreviousStep, tasks);
    steps.add(step);
}

protected synchronized void stepFinished(int index, boolean totalSuccess)
{
    if(index != executedSteps)
        throw new IllegalStateException("Oh fuck fuck fuck!!!");
    ++executedSteps;
    
    if(index == steps.size()-1) { // last step?
        postListenerNotification(executedSteps, totalSuccess);
    } else { 
        Step nextStep = steps.get(index+1);
        if(!totalSuccess && nextStep.requiresTotalSuccessOnPreviousStep) {
            postListenerNotification(executedSteps, false);
        } else {
            nextStep.executeTasks();
        }
    }
    
}

protected void postListenerNotification(final int executedSteps, final boolean totalSuccess)
{
    if(listener != null) {
        listener.threadCentralFinished(TaskCentral.this, executedSteps, totalSuccess);
    }
}

}
