package ru.eadm.nobird.fragment.task;

/**
 * Created by ruslandavletshin on 21/08/16.
 */
public enum  TaskState {
    IDLE, // task created but not started
    PROCESSING, // method doInBackground was called
    COMPLETED,
    ERROR
}