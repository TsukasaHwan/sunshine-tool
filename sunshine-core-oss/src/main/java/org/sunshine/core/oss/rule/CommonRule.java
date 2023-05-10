package org.sunshine.core.oss.rule;

import cn.hutool.core.io.file.FileNameUtil;
import org.sunshine.core.tool.util.DateUtils;
import org.sunshine.core.tool.util.StringPool;
import org.sunshine.core.tool.util.StringUtils;

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
        return "upload" + StringPool.SLASH + DateUtils.date().toString(DateUtils.SLASH_DATE_FORMAT) + StringPool.SLASH + StringUtils.randomUUID() + StringPool.DOT + FileNameUtil.extName(originalFilename);
    }
}