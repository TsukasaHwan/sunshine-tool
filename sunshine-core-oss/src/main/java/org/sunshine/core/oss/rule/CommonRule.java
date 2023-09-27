package org.sunshine.core.oss.rule;

import org.sunshine.core.tool.util.*;

/**
 * @author Teamo
 * @since 2021/11/02
 */
public class CommonRule implements OssRule {

    @Override
    public String bucketName(String bucketName) {
        return bucketName;
    }

    @Override
    public String fileName(String originalFilename) {
        String fileName = IdUtils.simpleUUID() + StringPool.DOT + FileNameUtils.extName(originalFilename);
        return PathUtils.buildPath("upload", DateUtils.format(DateUtils.date(), "yyyy/MM/dd"), fileName);
    }
}
