package org.sunshine.core.oss;

import com.alibaba.fastjson2.JSON;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.sunshine.core.oss.model.OssFile;
import org.sunshine.core.oss.model.PutOssFile;
import org.sunshine.core.oss.props.OssProperties;
import org.sunshine.core.oss.rule.OssRule;
import org.sunshine.core.tool.util.Exceptions;
import org.sunshine.core.tool.util.FileNameUtils;
import org.sunshine.core.tool.util.StringPool;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AliOssTemplate
 *
 * @author Chill
 */
public class AliOssTemplate implements OssTemplate {

    private final OSSClient ossClient;

    private final OssProperties ossProperties;

    private final OssRule ossRule;

    public AliOssTemplate(OSSClient ossClient, OssProperties ossProperties, OssRule ossRule) {
        this.ossClient = ossClient;
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @Override
    public void makeBucket(String bucketName) {
        if (!bucketExists(bucketName)) {
            ossClient.createBucket(getBucketName(bucketName));
        }
    }

    @Override
    public void removeBucket(String bucketName) {
        ossClient.deleteBucket(getBucketName(bucketName));
    }

    @Override
    public boolean bucketExists(String bucketName) {
        return ossClient.doesBucketExist(getBucketName(bucketName));
    }

    @Override
    public void copyFile(String bucketName, String fileName, String destBucketName) {
        ossClient.copyObject(getBucketName(bucketName), fileName, getBucketName(destBucketName), fileName);
    }

    @Override
    public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
        ossClient.copyObject(getBucketName(bucketName), fileName, getBucketName(destBucketName), destFileName);
    }

    @Override
    public OssFile statFile(String fileName) {
        return statFile(ossProperties.getBucketName(), fileName);
    }

    @Override
    public OssFile statFile(String bucketName, String fileName) {
        ObjectMetadata stat = ossClient.getObjectMetadata(getBucketName(bucketName), fileName);
        OssFile ossFile = new OssFile();
        ossFile.setName(fileName);
        ossFile.setLink(fileLink(ossFile.getName()));
        ossFile.setHash(stat.getContentMD5());
        ossFile.setLength(stat.getContentLength());
        ossFile.setPutTime(stat.getLastModified());
        ossFile.setContentType(stat.getContentType());
        return ossFile;
    }

    @Override
    public String filePath(String fileName) {
        return getOssHost().concat(StringPool.SLASH).concat(fileName);
    }

    @Override
    public String filePath(String bucketName, String fileName) {
        return getOssHost(bucketName).concat(StringPool.SLASH).concat(fileName);
    }

    @Override
    public String fileLink(String fileName) {
        return getOssHost().concat(StringPool.SLASH).concat(fileName);
    }

    @Override
    public String fileLink(String bucketName, String fileName) {
        return getOssHost(bucketName).concat(StringPool.SLASH).concat(fileName);
    }

    @Override
    public PutOssFile putFile(MultipartFile file) {
        return putFile(ossProperties.getBucketName(), file.getOriginalFilename(), file);
    }

    @Override
    public PutOssFile putFile(String fileName, MultipartFile file) {
        return putFile(ossProperties.getBucketName(), fileName, file);
    }

    @Override
    public PutOssFile putFile(String bucketName, String fileName, MultipartFile file) {
        try {
            return putFile(bucketName, fileName, file.getInputStream());
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    @Override
    public PutOssFile putFile(String fileName, InputStream stream) {
        return putFile(ossProperties.getBucketName(), fileName, stream);
    }

    @Override
    public PutOssFile putFile(String bucketName, String fileName, InputStream stream) {
        return put(bucketName, stream, fileName, false);
    }

    public PutOssFile put(String bucketName, InputStream stream, String key, boolean cover) {
        makeBucket(bucketName);
        ObjectMetadata objectMetadata = getObjectMetadata(FileNameUtils.extName(key));
        String originalName = key;
        key = getFileName(key);
        // 覆盖上传
        if (cover) {
            ossClient.putObject(getBucketName(bucketName), key, stream, objectMetadata);
        } else {
            PutObjectResult response = ossClient.putObject(getBucketName(bucketName), key, stream, objectMetadata);
            int retry = 0;
            int retryCount = 5;
            while (!StringUtils.hasLength(response.getETag()) && retry < retryCount) {
                response = ossClient.putObject(getBucketName(bucketName), key, stream, objectMetadata);
                retry++;
            }
        }
        PutOssFile file = new PutOssFile();
        file.setOriginalName(originalName);
        file.setName(key);
        file.setDomain(getOssHost(bucketName));
        file.setLink(fileLink(bucketName, key));
        return file;
    }

    @Override
    public void removeFile(String fileName) {
        ossClient.deleteObject(getBucketName(), fileName);
    }

    @Override
    public void removeFile(String bucketName, String fileName) {
        ossClient.deleteObject(getBucketName(bucketName), fileName);
    }

    @Override
    public void removeFiles(List<String> fileNames) {
        fileNames.forEach(this::removeFile);
    }

    @Override
    public void removeFiles(String bucketName, List<String> fileNames) {
        fileNames.forEach(fileName -> removeFile(getBucketName(bucketName), fileName));
    }

    /**
     * 根据规则生成存储桶名称规则
     *
     * @return String
     */
    private String getBucketName() {
        return getBucketName(ossProperties.getBucketName());
    }

    /**
     * 根据规则生成存储桶名称规则
     *
     * @param bucketName 存储桶名称
     * @return String
     */
    private String getBucketName(String bucketName) {
        return ossRule.bucketName(bucketName);
    }

    /**
     * 根据规则生成文件名称规则
     *
     * @param originalFilename 原始文件名
     * @return string
     */
    private String getFileName(String originalFilename) {
        return ossRule.fileName(originalFilename);
    }

    public String getUploadToken() {
        return getUploadToken(ossProperties.getBucketName());
    }

    /**
     * TODO 过期时间
     * <p>
     * 获取上传凭证，普通上传
     */
    public String getUploadToken(String bucketName) {
        // 默认过期时间2小时
        return getUploadToken(bucketName, (long) ossProperties.getArgs().getOrDefault("expireTime", 3600L));
    }

    /**
     * TODO 上传大小限制、基础路径
     * <p>
     * 获取上传凭证，普通上传
     */
    public String getUploadToken(String bucketName, long expireTime) {
        String baseDir = "upload";

        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        Date expiration = new Date(expireEndTime);

        PolicyConditions policyConds = new PolicyConditions();
        // 默认大小限制10M
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, (long) ossProperties.getArgs().getOrDefault("contentLengthRange", 10485760L));
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, baseDir);

        String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = ossClient.calculatePostSignature(postPolicy);

        Map<String, String> respMap = new LinkedHashMap<>(16);
        respMap.put("accessid", ossProperties.getAccessKey());
        respMap.put("policy", encodedPolicy);
        respMap.put("signature", postSignature);
        respMap.put("dir", baseDir);
        respMap.put("host", getOssHost(bucketName));
        respMap.put("expire", String.valueOf(expireEndTime / 1000));
        return JSON.toJSONString(respMap);
    }

    public String getOssHost(String bucketName) {
        String prefix = "https://";
        String endpoint = ossProperties.getEndpoint().contains("-internal") ? ossProperties.getEndpoint().replace("-internal", StringPool.EMPTY) : ossProperties.getEndpoint();
        return prefix + getBucketName(bucketName) + StringPool.DOT + endpoint.replaceFirst(prefix, StringPool.EMPTY);
    }

    public String getOssHost() {
        return getOssHost(ossProperties.getBucketName());
    }

    private ObjectMetadata getObjectMetadata(String extName) {
        ObjectMetadata objectMetadata;
        switch (extName.toLowerCase()) {
            case "bmp":
                objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType("image/bmp");
                break;
            case "jpeg":
            case "jpg":
                objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType("image/jpg");
                break;
            case "gif":
                objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType("image/gif");
                break;
            case "png":
                objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType("image/png");
                break;
            default:
                return null;
        }
        return objectMetadata;
    }
}
