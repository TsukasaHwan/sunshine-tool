package org.sunshine.core.sms.model;

import java.io.File;

/**
 * @author Teamo
 * @since 2023/5/5
 */
public class FileMailMessage {

    private final String text;

    private final String title;

    private final String attachmentName;

    private final File file;

    private final String sender;

    private final String cc;

    private final String[] to;

    FileMailMessage(String text, String title, String attachmentName, File file, String sender, String cc, String[] to) {
        this.text = text;
        this.title = title;
        this.attachmentName = attachmentName;
        this.file = file;
        this.sender = sender;
        this.cc = cc;
        this.to = to;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public File getFile() {
        return file;
    }

    public String getSender() {
        return sender;
    }

    public String getCc() {
        return cc;
    }

    public String[] getTo() {
        return to;
    }

    public static class Builder {

        private String text;

        private String title;

        private String attachmentName;

        private File file;

        private String sender;

        private String cc;

        private String[] to;

        Builder() {
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder attachmentName(String attachmentName) {
            this.attachmentName = attachmentName;
            return this;
        }

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public Builder sender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder cc(String cc) {
            this.cc = cc;
            return this;
        }

        public Builder to(String... to) {
            this.to = to;
            return this;
        }

        public FileMailMessage build() {
            return new FileMailMessage(this.text, this.title, this.attachmentName, this.file, this.sender, this.cc, this.to);
        }
    }
}
