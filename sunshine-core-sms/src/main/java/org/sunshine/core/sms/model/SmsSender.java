package org.sunshine.core.sms.model;

/**
 * @author Teamo
 * @since 2022/03/03
 */
public class SmsSender {

    /**
     * 电话号码
     * 支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码
     */
    private final String[] phoneNumbers;

    /**
     * 签名名称
     */
    private final String signName;

    /**
     * 模板代码
     */
    private final String templateCode;

    /**
     * 模板参数
     */
    private final String templateParam;

    /**
     * out id
     */
    private final String outId;

    SmsSender(String[] phoneNumbers, String signName, String templateCode, String templateParam, String outId) {
        this.phoneNumbers = phoneNumbers;
        this.signName = signName;
        this.templateCode = templateCode;
        this.templateParam = templateParam;
        this.outId = outId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String[] getPhoneNumbers() {
        return phoneNumbers;
    }

    public String getSignName() {
        return signName;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public String getTemplateParam() {
        return templateParam;
    }

    public String getOutId() {
        return outId;
    }

    public static class Builder {

        private String[] phoneNumbers;

        private String signName;

        private String templateCode;

        private String templateParam;

        private String outId;

        Builder() {
        }

        public Builder phoneNumbers(String... phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
            return this;
        }

        public Builder signName(String signName) {
            this.signName = signName;
            return this;
        }

        public Builder templateCode(String templateCode) {
            this.templateCode = templateCode;
            return this;
        }

        public Builder templateParam(String templateParam) {
            this.templateParam = templateParam;
            return this;
        }

        public Builder outId(String outId) {
            this.outId = outId;
            return this;
        }

        public SmsSender build() {
            return new SmsSender(this.phoneNumbers, this.signName, this.templateCode, this.templateParam, this.outId);
        }
    }
}
