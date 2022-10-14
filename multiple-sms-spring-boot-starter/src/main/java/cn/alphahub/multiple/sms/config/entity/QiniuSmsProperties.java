package cn.alphahub.multiple.sms.config.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * qiniu sms properties
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QiniuSmsProperties extends AbstractSmsProperties {
}
