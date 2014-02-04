package org.plasync.client.android.testapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by ericwood on 1/25/14.
 */

/**
 * An abstract class for fragments that require an activity to update their view and hence may
 * need to defer display if the activity has not attched yet
 *
 * This class supports requesting a display change which will be immediately performed if the activity is
 * attached or deferred until the activity is attached.
 */
public abstract class DeferedDisplayFragmentSupport extends Fragment{
    private Object lock = new Object();
    private DisplayCommand pendingCommand;

    public void requestDisplay(DisplayCommand command) {
        if (getActivity() == null) {
            synchronized(lock) {
                pendingCommand = command;
            }
        }
        else {
            command.execute();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Make sure the activity is attached
        if (getActivity() != null) {
            // If a display command is pending, execute the command
            executePendingDisplayCommand();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Make sure the view has been created
        if (getView() != null) {
            // If a display command is pending, execute the command
            executePendingDisplayCommand();
        }
    }

    private void executePendingDisplayCommand() {
        synchronized(lock) {
            if (pendingCommand != null) {
                pendingCommand.execute();
                pendingCommand = null;
            }
        }
    }

    protected interface DisplayCommand {
        void execute();
    }
}
