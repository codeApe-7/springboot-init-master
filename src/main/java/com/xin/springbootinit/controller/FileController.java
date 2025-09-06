package com.xin.springbootinit.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.xin.springbootinit.common.BaseResponse;
import com.xin.springbootinit.common.ErrorCode;
import com.xin.springbootinit.common.ResultUtils;
import com.xin.springbootinit.constant.FileConstant;
import com.xin.springbootinit.exception.BusinessException;
import com.xin.springbootinit.manager.CosManager;
import com.xin.springbootinit.model.dto.file.UploadFileRequest;
import com.xin.springbootinit.model.entity.User;
import com.xin.springbootinit.model.enums.FileUploadBizEnum;
import com.xin.springbootinit.service.UserService;
import com.xin.springbootinit.utils.DateUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static com.xin.springbootinit.constant.RedisKeyConstant.REDIS_USER_UPLOAD_FILE_KEY;

/**
 * 文件接口
 *
 * @author <a href="https://github.com/aiaicoder">程序员小新</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
            UploadFileRequest uploadFileRequest) {
        String biz = uploadFileRequest.getBiz();
        User loginUser = userService.getLoginUser();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //校验上传文件的md5值防止重复上传
        String md5;
        try{
            md5 = DigestUtil.md5Hex(multipartFile.getBytes());
            String fileUrl = stringRedisTemplate.opsForValue().get(REDIS_USER_UPLOAD_FILE_KEY + loginUser.getId() + ":" + md5);
            if (StringUtils.isNotBlank(fileUrl)){
                log.warn("文件重复上传");
                return ResultUtils.success(fileUrl);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        validFile(multipartFile, fileUploadBizEnum);
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        //格式化到月份
        String uploadDate = DateUtils.format(new Date(System.currentTimeMillis()), DateUtils.YYYYMM);
        String filepath = String.format("/%s/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId() , uploadDate , filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return ResultUtils.success(FileConstant.COS_HOST + filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONEMB = 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONEMB) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}
