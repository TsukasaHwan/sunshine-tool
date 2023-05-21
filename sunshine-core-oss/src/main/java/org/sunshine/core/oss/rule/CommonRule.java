package org.sunshine.core.oss.rule;

import org.sunshine.core.tool.util.DateUtils;
import org.sunshine.core.tool.util.FileNameUtils;
import org.sunshine.core.tool.util.IdUtils;
import org.sunshine.core.tool.util.StringPool;

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
        return "upload" + StringPool.SLASH + DateUtils.format(DateUtils.date(), "yyyy/MM/dd") + StringPool.SLASH + IdUtils.simpleUUID() + StringPool.DOT + FileNameUtils.extName(originalFilename);
    }
}
