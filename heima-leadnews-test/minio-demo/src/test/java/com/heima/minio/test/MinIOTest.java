package com.heima.minio.test;

import com.heima.file.service.FileStorageService;
import com.heima.minio.MinIOApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {

    @Resource
    private FileStorageService fileStorageService;


    @Test
    public void test() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Haenu0317\\Desktop\\list.html");
        String url = fileStorageService.uploadHtmlFile("", "list.html", fileInputStream);
        System.out.println(url);
    }


//    public static void main(String[] args) {
//
//        FileInputStream fileInputStream = null;
//        try {
//
//            fileInputStream =  new FileInputStream("D:\\list.html");;
//
//            //1.创建minio链接客户端
//            MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123").endpoint("http://192.168.200.130:9000").build();
//            //2.上传
//            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
//                    .object("list.html")//文件名
//                    .contentType("text/html")//文件类型
//                    .bucket("leadnews")//桶名词  与minio创建的名词一致
//                    .stream(fileInputStream, fileInputStream.available(), -1) //文件流
//                    .build();
//            minioClient.putObject(putObjectArgs);
//
//            System.out.println("http://192.168.200.130:9000/leadnews/ak47.jpg");
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

}