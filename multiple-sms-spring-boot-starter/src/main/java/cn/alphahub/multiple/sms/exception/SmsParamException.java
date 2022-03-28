package cn.alphahub.multiple.sms.exception;

/**
 * sms param empty exception
 *
 * @author lwj
 * @version 1.0
 * @date 2021-09-28 18:30
 */
public class SmsParamException extends RuntimeException {

    public SmsParamException() {
        super();
    }

    public SmsParamException(String message) {
        super(message);
    }

    public SmsParamException(String message, Throwable cause) {
        super(message, cause);
    }
}
