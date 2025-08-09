package com.safebox.back.rpi.service;

import com.safebox.back.rpi.dto.AddRpiDto;
import com.safebox.back.util.ResponseDto;

public interface RpiService {

    ResponseDto<String> addUser(AddRpiDto addRpiDto);

    ResponseDto<Void> deleteUser(String username);
}
