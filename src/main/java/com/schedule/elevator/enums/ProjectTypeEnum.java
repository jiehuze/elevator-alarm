    package com.schedule.elevator.enums;

    /**
     * 电梯项目类型枚举
     */
    public enum ProjectTypeEnum {

        RESIDENTIAL_AREA("住宅区", "RESIDENTIAL"),
        OFFICE_AREA("办公楼", "OFFICE"),
        MALL_SUPERMARKET("商业区", "MALL"),
        HOTEL_RESTAURANT("宾馆饭店", "HOTEL"),
        HOSPITAL("医院", "HOSPITAL"),
        SCHOOL("学校", "SCHOOL"),
        TRANSPORTATION("交通场所", "TRANSPORTATION"),
        CULTURAL_ENTERTAINMENT("文体娱乐馆", "CULTURAL"),
        OTHER_PLACE("其他", "OTHER");

        private final String description;
        private final String code;

        ProjectTypeEnum(String description, String code) {
            this.description = description;
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public String getCode() {
            return code;
        }

        /**
         * 根据描述查找枚举
         */
        public static ProjectTypeEnum getByDescription(String description) {
            for (ProjectTypeEnum type : values()) {
                if (type.getDescription().equals(description)) {
                    return type;
                }
            }
            return null;
        }

        /**
         * 根据代码查找枚举
         */
        public static ProjectTypeEnum getByCode(String code) {
            for (ProjectTypeEnum type : values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
            return null;
        }
    }
