/*
 * Kruise
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: v1.21.1
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
package cn.odboy.model.openkruise;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Includes all of the parameters a Manual update strategy needs.
 */
@ApiModel(description = "Includes all of the parameters a Manual update strategy needs.")
@Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2023-07-21T15:55:09.049+08:00")
public class KruiseAppsV1alpha1ManualUpdate {
    @SerializedName("partitions")
    private Map<String, Integer> partitions = null;

    public KruiseAppsV1alpha1ManualUpdate partitions(Map<String, Integer> partitions) {
        this.partitions = partitions;
        return this;
    }

    public KruiseAppsV1alpha1ManualUpdate putPartitionsItem(String key, Integer partitionsItem) {
        if (this.partitions == null) {
            this.partitions = new HashMap<String, Integer>();
        }
        this.partitions.put(key, partitionsItem);
        return this;
    }

    /**
     * Indicates number of subset partition.
     *
     * @return partitions
     **/
    @ApiModelProperty(value = "Indicates number of subset partition.")
    public Map<String, Integer> getPartitions() {
        return partitions;
    }

    public void setPartitions(Map<String, Integer> partitions) {
        this.partitions = partitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KruiseAppsV1alpha1ManualUpdate)) {
            return false;
        }
        KruiseAppsV1alpha1ManualUpdate ioKruiseAppsV1alpha1UnitedDeploymentSpecUpdateStrategyManualUpdate = (KruiseAppsV1alpha1ManualUpdate) o;
        return Objects.equals(this.partitions, ioKruiseAppsV1alpha1UnitedDeploymentSpecUpdateStrategyManualUpdate.partitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partitions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class KruiseAppsV1alpha1UnitedDeploymentSpecUpdateStrategyManualUpdate {\n");
        sb.append("    partitions: ").append(toIndentedString(partitions)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}