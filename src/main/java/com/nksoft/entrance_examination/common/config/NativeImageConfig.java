package com.nksoft.entrance_examination.common.config;

import com.nksoft.entrance_examination.common.config.hints.HibernateHints;
import com.nksoft.entrance_examination.common.config.hints.LiquibaseHints;
import com.nksoft.entrance_examination.common.advice.ErrorResponse;
import com.nksoft.entrance_examination.department.dto.DepartmentDto;
import com.nksoft.entrance_examination.examination.dto.ExamCenterDto;
import com.nksoft.entrance_examination.examination.dto.ExamDto;
import com.nksoft.entrance_examination.examination.dto.ExamEntryDto;
import com.nksoft.entrance_examination.student.dto.LoginDto;
import com.nksoft.entrance_examination.student.dto.StudentDto;
import com.nksoft.entrance_examination.department.dto.UniversityDto;
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
