package lab.common.data;

public class DataManagerResponse {

    private final boolean success;

    private final String message;

    public DataManagerResponse() {
        this.success = true;
        this.message = "";
    }

    public DataManagerResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

}
