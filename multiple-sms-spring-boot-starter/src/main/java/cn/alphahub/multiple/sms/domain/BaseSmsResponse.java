package cn.alphahub.multiple.sms.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * sms base response
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BaseSmsResponse extends AbstractSmsResponse implements Serializable {

}
