package cn.alphahub.multiple.sms.exception;

/**
 * sms param empty exception
 *
 * @author lwj
 * @version 1.0
 * @date 2021-09-28 18:30
 */
public class SmsException extends RuntimeException {

    public SmsException() {
        super();
    }

    public SmsException(String message) {
        super(message);
    }

    public SmsException(String message, Throwable cause) {
        super(message, cause);
    }
}
