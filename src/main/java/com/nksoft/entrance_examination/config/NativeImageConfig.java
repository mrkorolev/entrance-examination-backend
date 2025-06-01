package com.nksoft.entrance_examination.config;

import com.nksoft.entrance_examination.config.hints.HibernateHints;
import com.nksoft.entrance_examination.config.hints.LiquibaseHints;
import com.nksoft.entrance_examination.controller.advice.ErrorResponse;
import com.nksoft.entrance_examination.dto.DepartmentDto;
import com.nksoft.entrance_examination.dto.ExamCenterDto;
import com.nksoft.entrance_examination.dto.ExamDto;
import com.nksoft.entrance_examination.dto.ExamEntryDto;
import com.nksoft.entrance_examination.dto.LoginDto;
import com.nksoft.entrance_examination.dto.StudentDto;
import com.nksoft.entrance_examination.dto.UniversityDto;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@RegisterReflectionForBinding({
        UniversityDto.class, DepartmentDto.class,
        ExamCenterDto.class, ExamDto.class,
        ExamEntryDto.class, StudentDto.class, LoginDto.class,
        ErrorResponse.class
})
@ImportRuntimeHints({ LiquibaseHints.class, HibernateHints.class })
public class NativeImageConfig {}
