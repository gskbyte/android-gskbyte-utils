package org.gskbyte.tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to run groups of threads (represented by tasks) in an ordered basis.
 * 
 * The idea is to run a set of threads concurrently on every step, and once all threads in a step are finished,
 * execute the next one. Once all steps are finished, a callback method on a listener object is called.
 * */
public class QueuedTaskExecutor
{

/**
 * Listener with a callback method to be called when all steps are finished, or when there is an error
 * in one of the thread steps.
 * 
 * */
public interface Listener
{
    /**
     * Will be called when given two conditions:
     * - All threads in all steps have finished.
     * - A thread in a step didn't finish with success and the following steps requires the current to have success
     * in all threads.
     * 
     * @param executor The TaskCentral calling this method. You can call information methods to get info about the execution.
     * */
    public void queuedTaskExecutorFinished(QueuedTaskExecutor executor);
}

public enum Status
{
    PENDING, // waiting to be executed
    RUNNING, // threads are running
    FINISHED // execution completed
}

protected final ArrayList<TaskStep> steps = new ArrayList<TaskStep>();
protected final List<Exception> capturedExceptions = new ArrayList<Exception>();

protected int executedSteps = 0;
protected Listener listener = null;
protected boolean totalSuccessOnLastExecutedStep = false;
protected Status status = Status.PENDING;

public boolean run(Listener listener)
{
    if(status != Status.PENDING)
        throw new IllegalStateException("This " + getClass().getSimpleName() + " is running or already finished. It can be run only once.");
    this.listener = listener;
    if(steps.size() > 0) {
        steps.get(0).executeTasks();
    } else {
        notifyListener();
        return true;
    }
    
    return true;
}

/**
 * @return the number of steps
 * */
public synchronized int countSteps()
{ return steps.size(); }

/**
 * @return the umber of steps executed until the moment
 * */
public synchronized int countExecutedSteps()
{ return executedSteps; }

/**
 * @return true if all steps and all their tasks have been completed
 * */
public synchronized boolean didExecuteAllStepsWithSuccess()
{ return executedSteps == steps.size() && totalSuccessOnLastExecutedStep; }

/**
 * @return all captured exceptions in case of not success. Not having success doesn't neccesary mean that there are
 * exceptions.
 * */
public synchronized List<Exception> getCapturedExceptions()
{ return capturedExceptions;}

/**
 * @return the first captured exception, if any, or null if no exceptions are captured.
 * */
public synchronized Exception getFirstCapturedException()
{
    if(capturedExceptions.size()>0)
        return capturedExceptions.get( 0 );
    else
        return null;
}

/**
 * @return the last captured exception, if any, or null if no exceptions are captured.
 * */
public synchronized Exception getLastCapturedException()
{
    if(capturedExceptions.size()>0)
        return capturedExceptions.get( capturedExceptions.size()-1 );
    else
        return null;
}


/**
 * 
 * */
public synchronized void addStep(Task ... tasks)
{
    if(tasks.length == 0)
        throw new IllegalArgumentException("You must pass at least one StepRunnable");
    
    TaskStep step = new TaskStep(this, steps.size(), tasks);
    steps.add(step);
}

protected synchronized void stepFinished(int index, boolean totalSuccess, List<Exception> capturedExceptionsInStep)
{
    if(index != executedSteps)
        throw new IllegalStateException("Oh fuck fuck fuck!!!");
    ++executedSteps;
    totalSuccessOnLastExecutedStep = totalSuccess;
    
    if(capturedExceptionsInStep != null)
        this.capturedExceptions.addAll(capturedExceptionsInStep);
    
    if(index == steps.size()-1) { // last step?
        notifyListener();
    } else { 
        TaskStep nextStep = steps.get(index+1);
        if(!totalSuccess) {
            notifyListener();
        } else {
            nextStep.executeTasks();
        }
    }
    
}

protected void notifyListener()
{
    if(listener != null) {
        listener.queuedTaskExecutorFinished(QueuedTaskExecutor.this);
    }
}

}
