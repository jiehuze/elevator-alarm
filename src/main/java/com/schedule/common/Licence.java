package com.schedule.common;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class Licence {
    private static Random random = new Random();
    private static int licenceNum = 0;
    private static LocalDate freeUntilDate = LocalDate.of(2099, 12, 1);

    public static int getLicenceNum() {
        return licenceNum;
    }

    public static void addLicenceNum() {
        licenceNum++;
    }

    /**
     * 检查当前日期是否在免费期内。
     * 如果在免费期内，返回true；
     * 否则根据授权状态或随机数决定返回值。
     */
    public static boolean getLicence() {
        LocalDate currentDate = LocalDate.now();

        // 如果当前日期在免费期内，直接返回true
        if (ChronoUnit.DAYS.between(currentDate, freeUntilDate) > 0) {
            return true;
        }

        // 如果不在免费期内，则按照原逻辑处理
        return checkAuthorizationOrRandomResponse();
    }

    /**
     * 当不在免费期内时，检查授权状态或随机返回false/true。
     *
     * @return 授权状态或随机布尔值
     */
    private static boolean checkAuthorizationOrRandomResponse() {
        boolean isAuthorized = checkAuthorization();

        if (isAuthorized) {
            return true;
        } else {
            // 当没有授权时，以50%的概率随机返回false或true
            return random.nextBoolean();
        }
    }

    /**
     * 检查是否拥有授权的模拟方法。
     * 在实际应用中，这里应该包含具体的授权检查逻辑。
     *
     * @return 授权状态
     */
    private static boolean checkAuthorization() {
        // 这里只是示例，所以简单地返回false表示无授权
        // 实际使用时应替换为真实的授权检查逻辑
        return false;
    }

    public static void main(String[] args) {
//        Licence licenceService = new Licence();

        // 测试输出，展示不同调用的结果
        for (int i = 0; i < 10; i++) {
            System.out.println("Call " + (i + 1) + ": " + Licence.getLicence());
        }
    }
}
