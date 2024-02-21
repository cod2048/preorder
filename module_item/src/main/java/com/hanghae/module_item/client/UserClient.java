package com.hanghae.module_item.client;

import com.hanghae.module_item.client.dto.GetUserRoleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "userClient", url = "${feign.userClient.url}")
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/internal/users/check/{userNum}")
    GetUserRoleResponse getUserRole(@PathVariable("userNum") Long userNum);
}
