package cn.odboy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 数据分页参数
 *
 * @author odboy
 * @date 2025-01-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageArgs<T> implements Serializable {
    @NotNull(message = "参数current不能为空")
    @Min(value = 1, message = "参数current最小值为1")
    private Integer current;
    @NotNull(message = "参数size不能为空")
    @Min(value = 1, message = "参数size最小值为1")
    private Integer size;
    private T args;
}
