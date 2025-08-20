package com.schedule.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpUtil {
    public String get(String uri) {
        try {
            // 创建 URL 对象
            URL url = new URL(uri);

            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为 GET
            connection.setRequestMethod("GET");

            // 设置连接超时（单位：毫秒）
            int connectTimeout = 5000; // 5秒
            connection.setConnectTimeout(connectTimeout);

            // 设置读取超时（单位：毫秒）
            int readTimeout = 5000; // 5秒
            connection.setReadTimeout(readTimeout);

            // 获取响应码
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("response Info: " + response.toString());
                return response.toString();// 以格式化的方式打印 JSON
            } else {
                System.out.println("Failed to fetch user info. Response Code: " + responseCode);
            }

            // 关闭连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String get(String url, String token, String tenantId) {
        try {
            // 创建HttpClient实例
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2) // 可选，指定HTTP协议版本
                    .connectTimeout(Duration.ofSeconds(10)) // 设置连接超时时间
                    .build();

            // 创建HttpRequest，设置请求方法为GET，并在Headers中添加Authorization
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Authorization", token) // 在这里添加token
                    .header("tenant-id", tenantId) // 设置tenant-id头部
                    .GET() // 明确指定这是一个GET请求
                    .build();

            // 发送请求并接收响应
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 打印响应状态码和响应体
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String post(String url, String token, String tenantId, String requestBodyJson) {
        try {
            // 创建HttpClient实例
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2) // 可选，指定HTTP协议版本
                    .connectTimeout(Duration.ofSeconds(10)) // 设置连接超时时间
                    .build();

            // 将请求体转换为JSON字符串
//            String requestBodyJson = JSON.toJSONString(requestBody);

            // 创建HttpRequest，设置请求方法为POST，并在Headers中添加Authorization和tenant-id
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Authorization", token) // 在这里添加token
                    .header("tenant-id", tenantId) // 设置tenant-id头部
                    .header("Content-Type", "application/json") // 指定请求体的内容类型为JSON
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson)) // 设置POST请求及其body
                    .build();

            // 发送请求并接收响应
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 打印响应状态码和响应体
            System.out.println("Post Response Code: " + response.statusCode() + "  Body: " + response.body());

            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String uploadFile(String url, String token, String tenantId) {
        File uploadFile = new File("/Users/jiehu/works/test/replacefile/templete.doc"); // 替换为你要上传的文件路径
        OkHttpClient client = new OkHttpClient();

        // 创建请求体，使用MultipartBody构建multipart/form-data请求
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", uploadFile.getName(),
                        RequestBody.create(uploadFile, okhttp3.MediaType.parse("application/octet-stream")))
                .build();

        // 创建请求对象，并添加头部信息
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", token) // 设置Authorization头部
                .header("tenant-id", tenantId) // 设置tenant-id头部
                .post(requestBody)
                .build();

        try {
            // 同步执行请求
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println("File uploaded successfully: " + response.body().string());
            } else {
                System.out.println("Failed to upload file: " + response.code() + " " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String upload(String url, String token, String tenantId, String filePath) {
        // 创建 OkHttpClient
        OkHttpClient client = new OkHttpClient();

        // 要上传的文件
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("文件不存在：" + filePath);
            return null;
        }

        // 设置 multipart/form-data
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", token)
                .addHeader("tenant-id", tenantId)
                .addHeader("Content-Type", "multipart/form-data") // 确保 Content-Type 正确
                .build();

        // 发送请求
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonstr = response.body().string();
                System.out.println("文件上传成功：" + jsonstr);
                // 解析JSON字符串为JSONObject对象
                JSONObject jsonObject = JSON.parseObject(jsonstr);

                // 获取"data"节点
                JSONObject dataNode = jsonObject.getJSONObject("data");

                if (dataNode != null && dataNode.containsKey("url")) {
                    // 如果存在url节点，则打印其值
                    String jsonurl = dataNode.getString("url");
                    System.out.println("URL: " + jsonurl);

                    return jsonurl;
                } else {
                    // 如果没有找到url节点，则给出提示
                    System.out.println("Warning: URL not found in the JSON.");
                }
            } else {
                System.out.println("文件上传失败，错误码：" + response.code() + "，错误信息：" + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

//    public TokenRespDTO oauthen2(String url) {
//        // API endpoint URL
//        //        String url = "http://113.207.111.33:48770/api/admin/oauth2/token";
//
//        // Parameters
//        String grantType = "client_credentials";
//        String clientId = "wYMbSytEeKF4hEa833fB0xr63JRfDe9C";
//        String clientSecret = "17f9znWjY0TfSJi1f8EtC8nwXhJsDC7F6X6pnJ8HJNDAYP3DnB5YzWQhhjCxP23JwD0WH5MwNB5ycpP4xaZGBrAhsNFmnj2wXsn1Y0S0XRnQAf5JW0hhNB7MrTbe4zaz";
//
//        // Build the request body
//        String requestBody = "grant_type=" + grantType +
//                "&client_id=" + clientId +
//                "&client_secret=" + clientSecret;
//
//        // Create HttpClient and HttpRequest
//        HttpClient httpClient = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .header("tenant-id", "1877665103373783042")
//                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
//                .build();
//
//        // Send the request and get the response
//        HttpResponse<String> response = null;
//        try {
//            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println("Oauth2 Code: " + response.statusCode() + " Body: " + response.body());
//            if (response.statusCode() == 200) {
//                String jsonResponse = response.body();

    /// /                System.out.println("Response Body: " + jsonResponse);
//                TokenRespDTO tokenRespDTO = JSON.parseObject(jsonResponse, TokenRespDTO.class);
//                return tokenRespDTO;
//            }
//            // 使用FastJSON将JSON字符串转换为Java对象
//
//            return null;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static void main(String[] args) {

        HttpUtil httpUtil = new HttpUtil();
////        String s = httpUtil.uploadFile("http://113.207.111.33:48770/admin/sys-file/upload", "f4bf7edc-4220-40e3-8187-d99c56425776", "1877665103373783042");
//
////        httpUtil.upload("http://113.207.111.33:48770/api/admin/sys-file/upload", "Bearer 9dadc78f-6fd5-4a09-8de7-d6fa181c7b06", "1877665103373783042", "/Users/jiehu/works/test/replacefile/templete.doc");
//
//        String deptJson = httpUtil.get("http://113.207.111.33:48770/api/admin/dept/permission-list", "Bearer 255c2cbb-d36c-442c-9fc7-651e980ea8d6", "1877665103373783042");
//        System.out.println("++++++++ " + deptJson);
//        if (deptJson != null) {
//            List<DeptDTO> deptDTOS = JSON.parseArray(deptJson, DeptDTO.class);
//            System.out.println("------------ size: " + deptDTOS.size());
//        }
//
//        System.out.println("------------- " + deptDTOs);
//        TokenRespDTO tokenRespDTO = httpUtil.oauthen2("http://113.207.111.33:48770/api/admin/oauth2/token");
//        System.out.println("++++++ token: " + tokenRespDTO.toString());
//        String json = httpUtil.get("http://113.207.111.33:31991/api/admin/user/info", "Bearer 56a4851c-0156-4cad-84fc-a3fc6c4ec6dc", "1877665103373783042");
//        System.out.println("-----json: " + json);
//        UserDeptsDetail userDeptsDetail = JSON.parseObject(json, new TypeReference<UserDeptsDetail>() {
//        });
//        System.out.println("-----userDeptsDetail: " + userDeptsDetail.toString());

//        RoleDeptRequestDTO requestDTO = new RoleDeptRequestDTO();
//        requestDTO.setRoleCodes(List.of("CBLD"));
//        requestDTO.setDeptIds(List.of());
//
//        String jsonString = JSON.toJSONString(requestDTO);
//
//        String userListData = httpUtil.post("http://113.207.111.33:48770/api/admin/user/getUserListByRoleCodeList",
//                String.format("%s %s", tokenRespDTO.getToken_type(), tokenRespDTO.getAccess_token()),
//                "1877665103373783042",
//                jsonString);
//        System.out.println("****** listdata: " + userListData);
//
//
//        if (userListData != null) {
//
//            // 当code为0时，将data解析为List<UserDataDTO>
//            List<UserDataDTO> userDataList = JSON.parseArray(userListData, UserDataDTO.class);
//
//            for (UserDataDTO userData : userDataList) {
//                System.out.println("user  =  " + userData.toString());
//            }
//        }

    }
}
