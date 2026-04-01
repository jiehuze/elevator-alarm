package com.schedule.elevator.enums;

/**
 * 区域枚举
 */
public enum DistrictEnum {

    /**
     * 双桥区
     */
    SHUANGQIAO("130802", "双桥区"),

    /**
     * 双滦区
     */
    SHUANGLUAN("130803", "双滦区"),

    /**
     * 鹰手营子矿区
     */
    YINGSHOUYINGZI("130804", "鹰手营子矿区"),

    /**
     * 承德县
     */
    CHENGDE("130821", "承德县"),

    /**
     * 兴隆县
     */
    XINGLONG("130822", "兴隆县"),

    /**
     * 平泉市
     */
    PINGQUAN("130823", "平泉市"),

    /**
     * 滦平县
     */
    LUANPING("130824", "滦平县"),

    /**
     * 隆化县
     */
    LONGHUA("130825", "隆化县"),

    /**
     * 丰宁满族自治县
     */
    FENGNING("130826", "丰宁满族自治县"),

    /**
     * 宽城满族自治县
     */
    KUANCHENG("130827", "宽城满族自治县"),

    /**
     * 围场满族蒙古族自治县
     */
    WEICHANG("130828", "围场满族蒙古族自治县"),

    /**
     * 高新区
     */
    GAOXIN("1308021", "高新区");

    private final String code;
    private final String name;

    DistrictEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据区域代码获取枚举
     */
    public static DistrictEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (DistrictEnum district : values()) {
            if (district.getCode().equals(code)) {
                return district;
            }
        }
        return null;
    }

    /**
     * 根据区域名称获取枚举
     */
    public static DistrictEnum getByName(String name) {
        if (name == null) {
            return null;
        }
        for (DistrictEnum district : values()) {
            if (district.getName().equals(name)) {
                return district;
            }
        }
        return null;
    }
}
