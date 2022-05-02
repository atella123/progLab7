package lab.common.data;

import java.util.Collection;

public class DataManagerResponse<T> {

    private final boolean success;

    private final Collection<T> changedElements;

    private final String message;

    public DataManagerResponse() {
        this.success = true;
        this.changedElements = null;
        this.message = "";
    }

    public DataManagerResponse(boolean success, String message) {
        this.success = success;
        this.changedElements = null;
        this.message = message;
    }

    public DataManagerResponse(boolean success, Collection<T> changedElements) {
        this.success = success;
        this.changedElements = changedElements;
        this.message = "";
    }

    public DataManagerResponse(boolean success, Collection<T> changedElements, String message) {
        this.success = success;
        this.changedElements = changedElements;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Collection<T> getChangedElements() {
        return changedElements;
    }

    public String getMessage() {
        return message;
    }

}
