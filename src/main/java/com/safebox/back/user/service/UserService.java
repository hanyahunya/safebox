package com.safebox.back.user.service;

import com.safebox.back.user.dto.LoginDto;
import com.safebox.back.user.dto.SignUpDto;
import com.safebox.back.util.ResponseDto;

public interface UserService {
    /**
     * 
     * @param signUpDto 회원가입 정보 담긴 dto
     * @return ResponseDto.setXXX(메시지)로 회원가입 성공여부 반환
     */
    ResponseDto<Void> signUp(SignUpDto signUpDto);

    /**
     * @param loginDto id,password 담긴 dto
     * @return 토큰 문자열
     */
    ResponseDto<String> login(LoginDto loginDto);
}
