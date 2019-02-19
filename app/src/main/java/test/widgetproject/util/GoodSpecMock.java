package test.widgetproject.util;

public class GoodSpecMock {
    public static String provideJson() {
        return "[\n" +
                "    {\n" +
                "        \"title\": \"颜色\",\n" +
                "        \"specList\": [\n" +
                "            {\n" +
                "                \"id\": 1,\n" +
                "                \"content\": \"红\",\n" +
                "                \"canSelectIds\": [\n" +
                "                    3,\n" +
                "                    4,\n" +
                "                    5\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 2,\n" +
                "                \"content\": \"绿\",\n" +
                "                \"canSelectIds\": [\n" +
                "                    4,\n" +
                "                    6\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"title\": \"内存\",\n" +
                "        \"specList\": [\n" +
                "            {\n" +
                "                \"id\": 3,\n" +
                "                \"content\": \"4G\",\n" +
                "                \"canSelectIds\": [\n" +
                "                    1,\n" +
                "                    5\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 4,\n" +
                "                \"content\": \"6G\",\n" +
                "                \"canSelectIds\": [\n" +
                "                    1,\n" +
                "                    2,\n" +
                "5,\n" +
                "                    6\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"title\": \"套餐\",\n" +
                "        \"specList\": [\n" +
                "            {\n" +
                "                \"id\": 5,\n" +
                "                \"content\": \"官方标配\",\n" +
                "                \"canSelectIds\": [\n" +
                "                    1,\n" +
                "3,\n" +
                "                    4\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 6,\n" +
                "                \"content\": \"套餐一\",\n" +
                "                \"canSelectIds\": [\n" +
                "                    2,\n" +
                "                    4\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";
    }
}
