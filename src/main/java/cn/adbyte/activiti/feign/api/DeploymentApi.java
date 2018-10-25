package cn.adbyte.activiti.feign.api;

import cn.adbyte.activiti.feign.api.entity.DeploymentEntityCustom;
import cn.adbyte.activiti.feign.api.entity.DeployResourceEntityCustom;
import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * REST 部署服务
 * @see <a href='https://www.activiti.org/userguide/#_deployment'></a>
 * @author Adam
 */
public interface DeploymentApi {

    @RequestLine("GET repository/deployments")
    Results<DeploymentEntityCustom> listDeployments(@QueryMap Map<String, Object> map);

    @RequestLine("GET repository/deployments/{deploymentId}")
    DeploymentEntityCustom getDeployment(@Param("deploymentId") String deploymentId);

    @RequestLine("POST repository/deployments")
    @Headers("Content-Type: multipart/form-data")
    DeploymentEntityCustom createDeployment(@Param("file") File file);

    @RequestLine("DELETE repository/deployments/{deploymentId}")
    Integer deleteDeployment(@Param("deploymentId") String deploymentId);

    @RequestLine("GET repository/deployments/{deploymentId}/resources")
    List<DeployResourceEntityCustom> listResources(@Param("deploymentId") String deploymentId);

    /**
     * Get a deployment resource
     */
    @RequestLine("GET repository/deployments/{deploymentId}/resources/{resourceId}")
    DeployResourceEntityCustom getResources(@Param("deploymentId") String deploymentId, @Param("resourceId") String resourceId);

    @RequestLine("GET repository/deployments/{deploymentId}/resourcedata/{resourceId}")
    byte[] getResourceData(@Param("deploymentId") String deploymentId, @Param("resourceId") String resourceId);

}
