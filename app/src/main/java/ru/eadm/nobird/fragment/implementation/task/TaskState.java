package ru.eadm.nobird.fragment.implementation.task;

/**
 * Enum that display states of async tasks
 */
public enum  TaskState {
    IDLE, // task created but not started
    PROCESSING, // method doInBackground was called
    COMPLETED,
    ERROR
}