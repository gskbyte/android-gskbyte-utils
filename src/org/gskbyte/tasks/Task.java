package org.gskbyte.tasks;

import android.os.AsyncTask;

public abstract class Task
extends AsyncTask<Void, Void, Boolean>
{

/** @hide This instance variables are touched by TaskCentralStep */
/* package */ TaskStep step; 
/* package */ int indexInStep;

@Override
protected Boolean doInBackground(Void... params)
{
    if(step == null) {
        throw new IllegalStateException("StepTask can't be run outside a " + QueuedTaskExecutor.class.getSimpleName());
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
