package cn.odboy.system.security.service;

import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.system.security.model.JwtUserDto;
import cn.odboy.system.security.model.OnlineUserDto;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface OnlineUserService {
    void save(JwtUserDto jwtUserDto, String token, HttpServletRequest request);

    PageResult<OnlineUserDto> getAll(String username, Pageable pageable);

    List<OnlineUserDto> getAll(String username);

    void logout(String token);

    void download(List<OnlineUserDto> all, HttpServletResponse response) throws IOException;

    OnlineUserDto getOne(String key);

    void kickOutForUsername(String username);
}
