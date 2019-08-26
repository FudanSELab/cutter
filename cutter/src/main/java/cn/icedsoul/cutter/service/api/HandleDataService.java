package cn.icedsoul.cutter.service.api;

import cn.icedsoul.cutter.util.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author IcedSoul
 * @date 19-5-6 上午11:03
 */
public interface HandleDataService {
    /**
     * 处理输入文件,将信息输入至数据库
     * @param file
     */
    void handleData(String file);

    Response handleUploadFile(MultipartFile uploadDatFile);

//    /**
//     * 将数据库信息构建成调用树
//     */
//    void buildTree();
}
