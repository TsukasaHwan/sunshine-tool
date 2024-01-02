package org.sunshine.core.sms.model;

/**
 * @author Teamo
 * @since 2022/03/03
 */
public class SmsMessage {

    /**
     * 电话号码，支持批量调用
     * 阿里云 批量上限为1000个手机号码
     * 腾讯云 批量上限为200个手机号码
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
     * 阿里云参数
     */
    private AliYun aliYun;

    /**
     * 腾讯云参数
     */
    private Tencent tencent;

    SmsMessage(String[] phoneNumbers, String signName, String templateCode, AliYun aliYun) {
        this.phoneNumbers = phoneNumbers;
        this.signName = signName;
        this.templateCode = templateCode;
        this.aliYun = aliYun;
    }

    SmsMessage(String[] phoneNumbers, String signName, String templateCode, Tencent tencent) {
        this.phoneNumbers = phoneNumbers;
        this.signName = signName;
        this.templateCode = templateCode;
        this.tencent = tencent;
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

    public AliYun getAliYun() {
        return aliYun;
    }

    public Tencent getTencent() {
        return tencent;
    }

    public static class Builder {

        private String[] phoneNumbers;

        private String signName;

        private String templateCode;

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

        public SmsMessage build(AliYun aliYun) {
            return new SmsMessage(this.phoneNumbers, this.signName, this.templateCode, aliYun);
        }

        public SmsMessage build(Tencent tencent) {
            return new SmsMessage(this.phoneNumbers, this.signName, this.templateCode, tencent);
        }
    }

    public static class AliYun {

        /**
         * 模板参数
         */
        private final String templateParam;

        /**
         * out id
         */
        private final String outId;

        AliYun(String templateParam, String outId) {
            this.templateParam = templateParam;
            this.outId = outId;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getTemplateParam() {
            return templateParam;
        }

        public String getOutId() {
            return outId;
        }

        public static class Builder {

            private String templateParam;

            private String outId;

            Builder() {
            }

            public Builder templateParam(String templateParam) {
                this.templateParam = templateParam;
                return this;
            }

            public Builder outId(String outId) {
                this.outId = outId;
                return this;
            }

            public AliYun build() {
                return new AliYun(this.templateParam, this.outId);
            }
        }
    }

    public static class Tencent {
        private final String sdkAppId;

        private final String[] templateParam;

        Tencent(String sdkAppId, String... templateParam) {
            this.sdkAppId = sdkAppId;
            this.templateParam = templateParam;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getSdkAppId() {
            return sdkAppId;
        }

        public String[] getTemplateParam() {
            return templateParam;
        }

        public static class Builder {
            private String sdkAppId;

            private String[] templateParam;

            Builder() {
            }

            public Builder sdkAppId(String sdkAppId) {
                this.sdkAppId = sdkAppId;
                return this;
            }

            public Builder templateParam(String... templateParam) {
                this.templateParam = templateParam;
                return this;
            }

            public Tencent build() {
                return new Tencent(this.sdkAppId, this.templateParam);
            }
        }

    }
}
