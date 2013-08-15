package org.gskbyte.tasks;

import android.os.AsyncTask;

public abstract class Task
extends AsyncTask<Void, Void, Boolean>
{

/** @hide This instance variables are touched by TaskCentralStep */
/* package */ TaskStep step; 
/* package */ int indexInStep;
/* package */ Exception exception;

@Override
protected Boolean doInBackground(Void... params)
{
    if(step == null) {
        throw new IllegalStateException("StepTask can't be run outside a " + QueuedTaskExecutor.class.getSimpleName());
    }
    
    try {
        return runInBackground();
    } catch(Exception e) {
        
        return false;
    }
}

protected abstract boolean runInBackground() throws Exception;

@Override
protected void onPostExecute(Boolean result)
{
    step.taskFinishedWithResult(indexInStep, result, exception);
}

}
