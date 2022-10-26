package cn.alphahub.multiple.sms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * abstract sms request
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class AbstractSmsRequest {
    /**
     * 手机所属国家区号(不带"+"号)，如：86(中国)
     */
    private String countryCode;
    /**
     * 短信内容、模板参数; 多个以","隔开，若无模板参数，则为短信内容。模板参数的个数需要与【短信模板】对应模板的变量个数保持一致。
     */
    @NotBlank(message = "短信内容不能为空")
    private String contents;
    /**
     * 接收短信的手机号：一个、多个
     * <p>
     * 注意：有些国家地区需要: countryCode + phone (国家区号+手机号）
     */
    @NotEmpty(message = "手机号不能为空")
    private String[] phones;
}
