package cn.odboy.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 通用模型
 *
 * @author odboy
 * @date 2025-02-11
 */
public class CommonModel {
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class IdArgs extends MyObject {
        @NotNull(message = "id不能为空")
        private Long id;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class IdsArgs extends MyObject {
        @NotNull(message = "ids不能为空")
        @Size(min = 1, message = "ids最小长度为1")
        private List<Long> ids;
    }
}
