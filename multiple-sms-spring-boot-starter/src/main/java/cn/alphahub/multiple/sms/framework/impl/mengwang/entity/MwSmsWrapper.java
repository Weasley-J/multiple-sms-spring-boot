package cn.alphahub.multiple.sms.framework.impl.mengwang.entity;

import cn.alphahub.multiple.sms.config.entity.MengwangSmsProperties;
import cn.alphahub.multiple.sms.domain.BaseSmsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * sms aggregation wrapper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MwSmsWrapper implements Serializable {
    /**
     * 短信签名：短信开头的主体描述
     */
    @NotBlank
    private String signContent;
    /**
     * SMS基础入参
     */
    @Valid
    private BaseSmsRequest request;
    /**
     * 短信配置元数据
     */
    private MengwangSmsProperties mengwangSmsProperties;
}
