package com.safebox.back.rpi.service;

import com.safebox.back.rpi.dto.AddRpiDto;
import com.safebox.back.rpi.dto.RpiParcelUuidDto;
import com.safebox.back.util.ResponseDto;

public interface RpiService {

    /**
     * @param addRpiDto user - 유저의 uuid / port - 포트 / pubkey - ssh pub키
     * @return 라즈베리파이의 uuid
     */
    ResponseDto<String> addUser(AddRpiDto addRpiDto);

    /**
     * @param rpiUser 라즈베리파이 고유 uuid
     * @return
     */
    ResponseDto<Void> deleteUser(String rpiUser);

    ResponseDto<Void> arrived(RpiParcelUuidDto requestDto);

    ResponseDto<Void> pickuped(RpiParcelUuidDto requestDto);

}
