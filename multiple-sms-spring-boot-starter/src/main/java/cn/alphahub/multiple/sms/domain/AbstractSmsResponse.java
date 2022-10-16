package cn.alphahub.multiple.sms.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * sms base response
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
public abstract class AbstractSmsResponse {
    /**
     * 三方短信发送结果响应
     */
    private Object thirdResult;
}
